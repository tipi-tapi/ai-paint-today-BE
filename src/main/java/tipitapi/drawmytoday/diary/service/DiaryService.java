package tipitapi.drawmytoday.diary.service;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.dalle.service.DallEService;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.s3.service.S3Service;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final ValidateEmotionService validateEmotionService;
    private final S3Service s3Service;
    private final DallEService dallEService;
    private final PromptService promptService;

    public GetDiaryResponse getDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);
        Image image = imageService.getImage(diary);

        return GetDiaryResponse.of(diary, image, diary.getEmotion());
    }

    @Transactional(noRollbackFor = DallERequestFailException.class)
    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes) {
        // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
        User user = validateUserService.validateUserById(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        String prompt = createPromptText(emotion, keyword);

        try {
            byte[] dallEImage = dallEService.getDallEImage(prompt);

            Diary diary = diaryRepository.save(
                Diary.builder().user(user).emotion(emotion).diaryDate(LocalDateTime.now())
                    .notes(notes)
                    .isAi(true).build());
            promptService.createPrompt(diary, prompt, true);

            String imagePath = getImagePath(diary.getDiaryId(), 1);
            s3Service.uploadFromBase64(dallEImage, imagePath);
            imageService.createImage(diary, imagePath, true);

            return new CreateDiaryResponse(diary.getDiaryId());
        } catch (DallERequestFailException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }

    private String getImagePath(Long diaryId, int index) {
        return String.format("post/%d/%s_%d.png", diaryId,
            new Date().getTime(), index);
    }

    // TODO: 별도의 서비스로 분리, 로직 구현 필요
    private String createPromptText(Emotion emotion, String keyword) {
        return String.format(
            "%s illustration in %s tones, painted in watercolor fairy tale style, with %s",
            emotion.getEmotionPrompt(), emotion.getColorPrompt(), keyword);
    }
}
