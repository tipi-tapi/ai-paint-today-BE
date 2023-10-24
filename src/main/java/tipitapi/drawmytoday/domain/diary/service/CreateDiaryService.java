package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.exception.PromptNotExistException;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
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
    private final ImageGeneratorService karloService;
    private final PromptService promptService;
    private final Encryptor encryptor;

    @Transactional(noRollbackFor = {ImageGeneratorException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, LocalTime userTime) throws ImageGeneratorException {

        User user = validateUserService.validateUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        validateTicketService.findAndUseTicket(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        LocalDateTime diaryDateTime = diaryDate.atTime(userTime);

        GeneratedImageAndPrompt generated = karloService.generateImage(emotion, keyword);
        String prompt = generated.getPrompt();
        byte[] dallEImage = generated.getImage();

        Diary diary = saveDiary(notes, user, emotion, diaryDateTime, false);
        promptService.createPrompt(diary, prompt, true);
        imageService.uploadAndCreateImage(diary, dallEImage, true);

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

        byte[] image = karloService.generateTestImage(request);

        Diary diary = saveDiary(request.getNotes(), user, emotion, diaryDateTime, true);
        promptService.createPrompt(diary, request.getKarloParameter().getPrompt(), true);
        imageService.uploadAndCreateImage(diary, image, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    @Transactional(noRollbackFor = {ImageGeneratorException.class})
    public void regenerateDiaryImage(Long userId, Long diaryId) throws ImageGeneratorException {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        validateTicketService.findAndUseTicket(userId);

        Prompt prompt = promptService.getPromptByDiaryId(diaryId)
            .orElseThrow(PromptNotExistException::new);
        GeneratedImageAndPrompt generated = karloService.generateImage(prompt);

        imageService.unSelectAllImage(diary.getDiaryId());
        imageService.uploadAndCreateImage(diary, generated.getImage(), true);
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
