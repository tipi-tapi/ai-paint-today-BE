package tipitapi.drawmytoday.diary.service;

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
        String notes, boolean test)
        throws DallERequestFailException, ImageInputStreamFailException {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        // TODO: 광고 추가시 일기 생성 제한 로직으로 변경 필요
        User user = validateUserService.validateUserById(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String prompt = promptTextService.createPromptText(emotion, keyword);
        String encryptedNotes = encryptor.encrypt(notes);

        if (test) {
            return createDummyDiary(user, emotion, prompt, encryptedNotes);
        }

        try {
            byte[] dallEImage = dallEService.getImageAsUrl(prompt);

            Diary diary = diaryRepository.save(
                Diary.builder().user(user).emotion(emotion).diaryDate(LocalDateTime.now())
                    .notes(encryptedNotes)
                    .isAi(true).build());
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
        String notes) {
        Diary diary = diaryRepository.save(
            Diary.builder().user(user).emotion(emotion).diaryDate(LocalDateTime.now())
                .notes(notes).isAi(true).build());
        promptService.createPrompt(diary, prompt, true);
        imageService.createImage(diary, DUMMY_IMAGE_PATH, true);
        user.setLastDiaryDate(LocalDateTime.now());
        return new CreateDiaryResponse(diary.getDiaryId());
    }
}
