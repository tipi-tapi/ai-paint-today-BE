package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;
import tipitapi.drawmytoday.domain.ticket.service.ValidateTicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateDiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final ValidateEmotionService validateEmotionService;
    private final ValidateDiaryService validateDiaryService;
    private final ValidateTicketService validateTicketService;
    private final ValidatePromptService validatePromptService;
    private final ImageGeneratorService karloService;
    private final PromptService promptService;
    private final Encryptor encryptor;
    private final PromptTextService promptTextService;

    @Transactional(noRollbackFor = {ImageGeneratorException.class})
    public CreateDiaryResponse createDiary(Long userId, CreateDiaryRequest request)
        throws ImageGeneratorException {
        LocalDate diaryDate = request.getDiaryDate();
        User user = validateUserService.validateUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        validateTicketService.findAndUseTicket(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(request.getEmotionId());
        LocalDateTime diaryDateTime = diaryDate.atTime(request.getUserTime());

        Prompt prompt;
        if (isNewVersion(request.getTranslatedNotes())) {
            prompt = promptTextService.generatePromptUsingGpt(emotion,
                request.getTranslatedNotes());
        } else {
            prompt = promptTextService.createPrompt(emotion, request.getKeyword());
        }

        byte[] image = karloService.generateImage(prompt);

        prompt.imageGeneratorSuccess();
        Diary diary = saveDiary(request.getNotes(), user, emotion, diaryDateTime, false);
        imageService.uploadAndCreateImage(diary, prompt, image, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    @Transactional(noRollbackFor = {ImageGeneratorException.class})
    public CreateDiaryResponse createTestDiary(Long userId, CreateTestDiaryRequest request)
        throws ImageGeneratorException {
        LocalDate diaryDate = request.getDiaryDate();
        User user = validateUserService.validateAdminUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        Emotion emotion = validateEmotionService.validateEmotionById(request.getEmotionId());
        LocalDateTime diaryDateTime = diaryDate.atTime(request.getUserTime());

        List<byte[]> images = karloService.generateTestImage(request);

        Diary diary = saveDiary(request.getNotes(), user, emotion, diaryDateTime, true);
        Prompt prompt = promptService.createPrompt(request.getKarloParameter().getPrompt(), true);
        for (int i = 0; i < images.size(); i++) {
            imageService.uploadAndCreateImage(diary, prompt, images.get(i), i == 0);
        }

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    @Transactional(noRollbackFor = {ImageGeneratorException.class})
    public void regenerateDiaryImage(Long userId, Long diaryId, String diaryNote)
        throws ImageGeneratorException {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        validateTicketService.findAndUseTicket(userId);

        if (isNewVersion(diaryNote)) {
            regenerateDiaryImageWithNewPrompt(diary, diaryNote);
        } else {
            regenerateDiaryImageWithPreviousPrompt(diary);
        }
    }

    private boolean isNewVersion(String diaryNote) {
        return StringUtils.hasText(diaryNote);
    }

    private void regenerateDiaryImageWithNewPrompt(Diary diary, String diaryNote)
        throws ImageGeneratorException {
        Emotion emotion = validateEmotionService.validateEmotionById(
            diary.getEmotion().getEmotionId());
        Prompt prompt = validatePromptService.validatePromptByImageId(
            diary.getSelectedImage().getImageId());

        String promptGeneratorContent = prompt.getPromptGeneratorResult()
            .getPromptGeneratorContent();
        if (promptGeneratorContent == null || promptGeneratorContent.isBlank()) {
            prompt = promptTextService.generatePromptUsingGpt(emotion, diaryNote);
        } else {
            prompt = promptTextService.regeneratePromptUsingGpt(emotion, diaryNote, prompt);
        }

        byte[] image = karloService.generateImage(prompt);

        prompt.imageGeneratorSuccess();
        imageService.unSelectAllImage(diary.getDiaryId());
        imageService.uploadAndCreateImage(diary, prompt, image, true);
    }

    private void regenerateDiaryImageWithPreviousPrompt(Diary diary)
        throws ImageGeneratorException {
        Long imageId = diary.getSelectedImage().getImageId();
        Prompt prompt = validatePromptService.validatePromptByImageId(imageId);

        byte[] image = karloService.generateImage(prompt);

        imageService.unSelectAllImage(diary.getDiaryId());
        imageService.uploadAndCreateImage(diary, prompt, image, true);
    }

    private Diary saveDiary(String notes, User user, Emotion emotion, LocalDateTime diaryDate,
        boolean testDiary) {
        String encryptedNotes = encryptor.encrypt(notes);
        user.setLastDiaryDate(LocalDateTime.now());

        if (testDiary) {
            return diaryRepository.save(Diary.ofTest(user, emotion, diaryDate, encryptedNotes));
        } else {
            return diaryRepository.save(Diary.of(user, emotion, diaryDate, encryptedNotes));
        }
    }
}
