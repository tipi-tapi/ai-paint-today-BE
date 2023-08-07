package tipitapi.drawmytoday.diary.service;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.dalle.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.dalle.service.DallEService;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateDiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final ValidateEmotionService validateEmotionService;
    private final DallEService dallEService;
    private final PromptService promptService;
    private final PromptTextService promptTextService;
    private final Encryptor encryptor;
    private final String DUMMY_IMAGE_PATH = "test/dummy.png";

    @Transactional(
        noRollbackFor = {DallERequestFailException.class, DallERequestFailException.class,
            ImageInputStreamFailException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, ZoneId timezone)
        throws DallERequestFailException, ImageInputStreamFailException {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        User user = validateUserService.validateUserWithDrawLimit(userId, timezone);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String prompt = promptTextService.createPromptText(emotion, keyword);

        try {
            byte[] dallEImage = dallEService.getImageAsUrl(prompt);

            Diary diary = saveDiary(notes, user, emotion, diaryDate, timezone, false);
            promptService.createPrompt(diary, prompt, true);
            imageService.uploadAndCreateImage(diary, dallEImage, true);

            return new CreateDiaryResponse(diary.getDiaryId());
        } catch (DallERequestFailException | ImageInputStreamFailException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public CreateDiaryResponse createTestDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, ZoneId timezone) {
        User user = validateUserService.validateUserWithDrawLimit(userId, timezone);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);

        Diary diary = saveDiary(notes, user, emotion, diaryDate, timezone, true);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        promptService.createPrompt(diary, prompt, true);
        imageService.createImage(diary, DUMMY_IMAGE_PATH, true);

        return new CreateDiaryResponse(diary.getDiaryId());
    }

    private Diary saveDiary(String notes, User user, Emotion emotion, LocalDate diaryDate,
        ZoneId timezone, boolean testDiary) {
        String encryptedNotes = encryptor.encrypt(notes);
        user.updateLastDiaryDate(timezone);

        if (testDiary) {
            return diaryRepository.save(
                Diary.ofTest(user, emotion, diaryDate, timezone, encryptedNotes));
        } else {
            return diaryRepository.save(
                Diary.of(user, emotion, diaryDate, timezone, encryptedNotes));
        }
    }
}
