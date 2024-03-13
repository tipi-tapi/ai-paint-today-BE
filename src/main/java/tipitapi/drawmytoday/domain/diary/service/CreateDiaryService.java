package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
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
    private final TransactionTemplate readTransactionTemplate;
    private final TransactionTemplate writeTransactionTemplate;

    public CreateDiaryResponse createDiary(Long userId, CreateDiaryRequest request)
        throws ImageGeneratorException {
        LocalDate diaryDate = request.getDiaryDate();
        User user = validateUserService.validateUserById(userId);
        Emotion emotion = readTransactionTemplate.execute(status -> {
            validateDiaryService.validateExistsByDate(userId, diaryDate);
            validateTicketService.validateTicket(userId);
            return validateEmotionService.validateEmotionById(request.getEmotionId());
        });
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
        Diary diary = writeTransactionTemplate.execute(status -> {
            try {
                Diary newDiary = saveDiary(request.getNotes(), user, emotion, diaryDateTime, false);
                imageService.uploadAndCreateImage(newDiary, prompt, image, true);
                validateTicketService.findAndUseTicket(userId);
                return newDiary;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    public CreateDiaryResponse createTestDiary(Long userId, CreateTestDiaryRequest request)
        throws ImageGeneratorException {
        LocalDate diaryDate = request.getDiaryDate();
        User user = validateUserService.validateUserById(userId);
        Emotion emotion = readTransactionTemplate.execute(status -> {
            validateDiaryService.validateExistsByDate(userId, diaryDate);
            return validateEmotionService.validateEmotionById(request.getEmotionId());
        });
        LocalDateTime diaryDateTime = diaryDate.atTime(request.getUserTime());

        List<byte[]> images = karloService.generateTestImage(request);

        Diary diary = writeTransactionTemplate.execute(status -> {
            try {
                Diary newDiary = saveDiary(request.getNotes(), user, emotion, diaryDateTime, false);
                Prompt prompt = promptService.createPrompt(request.getKarloParameter().getPrompt(),
                    true);
                for (int i = 0; i < images.size(); i++) {
                    imageService.uploadAndCreateImage(newDiary, prompt, images.get(i), i == 0);
                }
                return newDiary;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    public void regenerateDiaryImage(Long userId, Long diaryId, String diaryNote)
        throws ImageGeneratorException {
        if (isNewVersion(diaryNote)) {
            regenerateDiaryImageWithNewPrompt(userId, diaryId, diaryNote);
        } else {
            regenerateDiaryImageWithPreviousPrompt(userId, diaryId);
        }
    }

    private boolean isNewVersion(String diaryNote) {
        return StringUtils.hasText(diaryNote);
    }

    private void regenerateDiaryImageWithNewPrompt(Long userId, Long diaryId, String diaryNote)
        throws ImageGeneratorException {

        Diary[] diaryWrapper = new Diary[1];
        Emotion[] emotionWrapper = new Emotion[1];
        Prompt[] promptWrapper = new Prompt[1];
        readTransactionTemplate.executeWithoutResult(status -> {
            User user = validateUserService.validateUserById(userId);
            diaryWrapper[0] = validateDiaryService.validateDiaryById(diaryId, user);
            validateTicketService.validateTicket(userId);
            emotionWrapper[0] = validateEmotionService.validateEmotionById(
                diaryWrapper[0].getEmotion().getEmotionId());
            promptWrapper[0] = validatePromptService.validatePromptByImageId(
                diaryWrapper[0].getSelectedImage().getImageId());
        });
        Diary diary = diaryWrapper[0];
        Emotion emotion = emotionWrapper[0];
        Prompt prompt = promptWrapper[0];

        String promptGeneratorContent = prompt.getPromptGeneratorResult()
            .getPromptGeneratorContent();
        if (promptGeneratorContent == null || promptGeneratorContent.isBlank()) {
            prompt = promptTextService.generatePromptUsingGpt(emotion, diaryNote);
        } else {
            prompt = promptTextService.regeneratePromptUsingGpt(emotion, diaryNote, prompt);
        }

        byte[] image = karloService.generateImage(prompt);

        Prompt finalPrompt = prompt;
        writeTransactionTemplate.executeWithoutResult(status -> {
            try {
                finalPrompt.imageGeneratorSuccess();
                imageService.unSelectAllImage(diary.getDiaryId());
                imageService.uploadAndCreateImage(diary, finalPrompt, image, true);
                validateTicketService.findAndUseTicket(userId);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    private void regenerateDiaryImageWithPreviousPrompt(Long userId, Long diaryId)
        throws ImageGeneratorException {
        Diary[] diaryWrapper = new Diary[1];
        Prompt[] promptWrapper = new Prompt[1];
        readTransactionTemplate.executeWithoutResult(status -> {
            User user = validateUserService.validateUserById(userId);
            diaryWrapper[0] = validateDiaryService.validateDiaryById(diaryId, user);
            validateTicketService.validateTicket(userId);
            Long imageId = diaryWrapper[0].getSelectedImage().getImageId();
            promptWrapper[0] = validatePromptService.validatePromptByImageId(imageId);
        });
        Diary diary = diaryWrapper[0];
        Prompt prompt = promptWrapper[0];

        byte[] image = karloService.generateImage(prompt);

        writeTransactionTemplate.executeWithoutResult(status -> {
            try {
                imageService.unSelectAllImage(diary.getDiaryId());
                Prompt newPrompt = validatePromptService.validatePromptById(prompt.getPromptId());
                imageService.uploadAndCreateImage(diary, newPrompt, image, true);
                validateTicketService.findAndUseTicket(userId);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
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
