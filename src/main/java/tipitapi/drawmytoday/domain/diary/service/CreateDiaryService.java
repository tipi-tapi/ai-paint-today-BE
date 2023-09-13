package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.dalle.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.dalle.exception.DallEException;
import tipitapi.drawmytoday.domain.dalle.service.DallEService;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.service.ValidateEmotionService;
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
    private final DallEService dallEService;
    private final PromptService promptService;
    private final PromptTextService promptTextService;
    private final Encryptor encryptor;
    private final String DUMMY_IMAGE_PATH = "test/dummy.png";

    @Transactional(noRollbackFor = {DallEException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, LocalTime userTime) throws DallEException {

        User user = validateUserService.validateUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        validateTicketService.findAndUseTicket(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        LocalDateTime diaryDateTime = diaryDate.atTime(userTime);

        GeneratedImageAndPrompt generated = dallEService.generateImage(emotion, keyword);
        String prompt = generated.getPrompt();
        byte[] dallEImage = generated.getImage();

        Diary diary = saveDiary(notes, user, emotion, diaryDateTime, false);
        promptService.createPrompt(diary, prompt, true);
        imageService.uploadAndCreateImage(diary, dallEImage, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    @Transactional
    public CreateDiaryResponse createTestDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, LocalTime userTime) {
        User user = validateUserService.validateAdminUserById(userId);
        validateDiaryService.validateExistsByDate(userId, diaryDate);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        LocalDateTime diaryDateTime = diaryDate.atTime(userTime);

        Diary diary = saveDiary(notes, user, emotion, diaryDateTime, true);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        promptService.createPrompt(diary, prompt, true);
        imageService.createImage(diary, DUMMY_IMAGE_PATH, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    @Transactional(noRollbackFor = {DallEException.class})
    public void regenerateDiaryImage(Long userId, Long diaryId, String keyword)
        throws DallEException {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        validateTicketService.findAndUseTicket(userId);

        GeneratedImageAndPrompt generated = dallEService.generateImage(diary.getEmotion(), keyword);
        String prompt = generated.getPrompt();
        byte[] dallEImage = generated.getImage();

        promptService.createPrompt(diary, prompt, true);
        imageService.unSelectAllImage(diary.getDiaryId());
        imageService.uploadAndCreateImage(diary, dallEImage, true);
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
