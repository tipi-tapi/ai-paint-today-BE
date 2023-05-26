package tipitapi.drawmytoday.diary.service;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public GetDiaryResponse getDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);
        Image image = imageService.getImage(diary);
        
        return GetDiaryResponse.of(diary, image, diary.getEmotion());
    }

    public CreateDiaryResponse createDiary(Long userId, Long emotionId, String keyword,
        String notes) {
        User user = validateUserService.validateUserById(userId);
        Emotion emotion = validateEmotionService.validateEmotionById(emotionId);
        // 일기 객체 생성
        Diary diary = diaryRepository.save(
            Diary.builder().user(user).emotion(emotion).diaryDate(LocalDateTime.now()).notes(notes)
                .isAi(true).build()
        );
        try {
            // TODO: 이미지 여러 개로 요청할 경우의 핸들링 필요
            // 이미지 생성 요청
            String prompt = createPrompt(emotionId, keyword);
            byte[] dallEImage = dallEService.getDallEImage(prompt);
            // 생성 요청 성공시, S3 업로드
            String imagePath = getImagePath(diary.getDiaryId(), 1);
            s3Service.uploadFromBase64(dallEImage, imagePath);
            // 이미지 업로드 성공 후, 이미지 객체 생성
            imageService.createImage(diary, imagePath, true);
        } catch (Exception e) {
            // TODO: Dall-E 에러 핸들링 필요
            // TODO: S3 에러 핸들링 필요
        }
        // TODO: PromptLog 생성 필요
        // TODO: Dall-E 생성 실패시 Diary 생성 안되게 롤백 필요
        return new CreateDiaryResponse(diary.getDiaryId());
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }

    private String getImagePath(Long diaryId, int index) {
        return String.format("/post/%d/%s_%d.png", diaryId,
            new Date().getTime(), index);
    }

    // TODO: 별도의 서비스로 분리, 로직 구현 필요
    private String createPrompt(long emotionId, String keyword) {
        return "";
    }
}
