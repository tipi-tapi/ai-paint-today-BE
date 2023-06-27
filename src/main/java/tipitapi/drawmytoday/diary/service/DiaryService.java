package tipitapi.drawmytoday.diary.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Prompt;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.s3.service.S3PreSignedService;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final PromptService promptService;
    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final S3PreSignedService s3PreSignedService;
    private final ValidateDiaryService validateDiaryService;

    public GetDiaryResponse getDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);

        String imageUrl = s3PreSignedService.getPreSignedUrlForShare(
            imageService.getImage(diary).getImageUrl(), 30);

        Optional<Prompt> prompt = promptService.getPromptByDiaryId(diaryId);
        String promptText = prompt.map(Prompt::getPromptText).orElse(null);

        return GetDiaryResponse.of(diary, imageUrl, diary.getEmotion(), promptText);
    }

    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, int year, int month) {
        User user = validateUserService.validateUserById(userId);
        LocalDateTime startMonth = DateUtils.getStartDate(year, month);
        LocalDateTime endMonth = DateUtils.getEndDate(year, month);
        List<Diary> getDiaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
            user.getUserId(), startMonth, endMonth);
        return convertDiariesToResponse(getDiaryList);
    }

    public GetLastCreationResponse getLastCreation(Long userId) {
        validateUserService.validateUserById(userId);
        return new GetLastCreationResponse(
            diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(userId)
                .map(BaseEntity::getCreatedAt)
                .orElse(null));
    }

    @Transactional
    public void updateDiaryNotes(Long userId, Long diaryId, String notes) {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);

        diary.setNotes(notes);
    }

    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);

        diaryRepository.delete(diary);
    }

    private List<GetMonthlyDiariesResponse> convertDiariesToResponse(List<Diary> getDiaryList) {
        return getDiaryList.stream()
            .filter(diary -> {
                if (diary.getImageList().isEmpty()) {
                    throw new ImageNotFoundException();
                }
                return true;
            })
            .map(diary -> {
                String imageUrl = s3PreSignedService.getPreSignedUrlForShare(
                    diary.getImageList().get(0).getImageUrl(), 30);
                return GetMonthlyDiariesResponse.of(diary.getDiaryId(), imageUrl,
                    diary.getDiaryDate());
            })
            .collect(Collectors.toList());
    }
}
