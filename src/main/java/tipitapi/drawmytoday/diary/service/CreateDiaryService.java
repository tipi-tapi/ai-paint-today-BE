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
    private final Encryptor encryptor;

    @Transactional(
        noRollbackFor = {DallERequestFailException.class, DallERequestFailException.class,
            ImageInputStreamFailException.class})
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes) throws DallERequestFailException, ImageInputStreamFailException {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        User user = validateUserService.validateUserWithDrawLimit(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String prompt = createPromptText(emotion, keyword);
        String encryptedNotes = encryptor.encrypt(notes);

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

    // TODO: 별도의 서비스로 분리, 로직 구현 필요
    private String createPromptText(Emotion emotion, String keyword) {
        return String.format(
            "%s , %s , canvas-textured, Oil Pastel, %s",
            emotion.getEmotionPrompt(), emotion.getColorPrompt(), keyword);
    }
}
