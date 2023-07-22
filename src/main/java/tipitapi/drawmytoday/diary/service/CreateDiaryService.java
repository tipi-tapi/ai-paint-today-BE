package tipitapi.drawmytoday.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
import tipitapi.drawmytoday.s3.service.S3Service;
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
    private final S3Service s3Service;
    private final DallEService dallEService;
    private final PromptService promptService;
    private final PromptTextService promptTextService;
    private final Encryptor encryptor;
    private final String DUMMY_IMAGE_PATH = "test/dummy.png";

    @Transactional(
        noRollbackFor = {DallERequestFailException.class, DallERequestFailException.class,
            ImageInputStreamFailException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes, LocalDate diaryDate, boolean test)
        throws DallERequestFailException, ImageInputStreamFailException {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        // TODO: 광고 추가시 일기 생성 제한 로직으로 변경 필요
        User user = validateUserService.validateUserWithDrawLimit(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String encryptedNotes = encryptor.encrypt(notes);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        Diary diary = Diary.of(user, emotion, diaryDate, encryptedNotes);

        if (test) {
            return createDummyDiary(user, emotion, prompt, encryptedNotes, diaryDate);
        }

        try {
            byte[] dallEImage = dallEService.getImageAsUrl(prompt);

            diaryRepository.save(diary);
            promptService.createPrompt(diary, prompt, true);

            String imagePath = getImagePath(diary.getDiaryId(), 1);
            s3Service.uploadImage(dallEImage, imagePath);
            imageService.createImage(diary, imagePath, true);
            user.setLastDiaryDate(LocalDateTime.now());
            return new CreateDiaryResponse(diary.getDiaryId());
        } catch (DallERequestFailException | ImageInputStreamFailException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    private String getImagePath(Long diaryId, int index) {
        return String.format("post/%d/%s_%d.png", diaryId,
            new Date().getTime(), index);
    }

    private CreateDiaryResponse createDummyDiary(User user, Emotion emotion, String prompt,
        String notes, LocalDate diaryDate) {
        Diary diary = diaryRepository.save(
            Diary.ofTest(user, emotion, diaryDate, notes));
        promptService.createPrompt(diary, prompt, true);
        imageService.createImage(diary, DUMMY_IMAGE_PATH, true);
        user.setLastDiaryDate(LocalDateTime.now());
        return new CreateDiaryResponse(diary.getDiaryId());
    }
}
