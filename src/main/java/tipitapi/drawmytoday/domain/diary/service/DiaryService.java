package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.converter.Language;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.domain.ReviewType;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryExistByDateResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryLimitResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.r2.service.R2PreSignedService;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.service.ValidateTicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final PromptService promptService;
    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final ValidateUserService validateUserService;
    private final R2PreSignedService r2PreSignedService;
    private final Encryptor encryptor;
    private final ValidateDiaryService validateDiaryService;
    private final ValidateTicketService validateTicketService;

    public GetDiaryResponse getDiary(Long userId, Long diaryId, Language language) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        diary.setNotes(encryptor.decrypt(diary.getNotes()));

        String imageUrl = r2PreSignedService.getPreSignedUrlForShare(
            imageService.getImage(diary).getImageUrl(), 30);

        String emotionText = diary.getEmotion().getEmotionText(language);

        Optional<Prompt> prompt = promptService.getPromptByDiaryId(diaryId);
        String promptText = prompt.map(Prompt::getPromptText).orElse(null);

        return GetDiaryResponse.of(diary, imageUrl, emotionText, promptText);
    }

    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, int year, int month) {
        User user = validateUserService.validateUserById(userId);
        LocalDateTime startMonth = DateUtils.getStartDate(year, month);
        LocalDateTime endMonth = DateUtils.getEndDate(year, month);
        List<Diary> getDiaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
            user.getUserId(), startMonth, endMonth);
        return convertDiariesToResponse(getDiaryList);
    }

    public GetDiaryExistByDateResponse getDiaryExistByDate(Long userId, int year, int month,
        int day) {
        User user = validateUserService.validateUserById(userId);
        LocalDate date = DateUtils.getDate(year, month, day);

        Optional<Diary> diary = diaryRepository.getDiaryExistsByDiaryDate(user.getUserId(), date);

        if (diary.isEmpty()) {
            return GetDiaryExistByDateResponse.ofNotExist();
        } else {
            return GetDiaryExistByDateResponse.ofExist(diary.get().getDiaryId());
        }
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

        diary.setNotes(encryptor.encrypt(notes));
    }

    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);

        diaryRepository.delete(diary);
    }

    public GetDiaryLimitResponse getDrawLimit(Long userId) {
        User user = validateUserService.validateUserById(userId);

        boolean available = false;
        LocalDateTime lastDiaryDate = user.getLastDiaryDate();
        LocalDateTime ticketCreatedAt = null;

        Optional<Ticket> ticket = validateTicketService.findValidTicket(userId);
        if (ticket.isPresent()) {
            available = true;
            ticketCreatedAt = ticket.get().getCreatedAt();
        }

        return GetDiaryLimitResponse.of(available, lastDiaryDate, ticketCreatedAt);
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
                String imageUrl = r2PreSignedService.getPreSignedUrlForShare(
                    diary.getImageList().get(0).getImageUrl(), 30);
                return GetMonthlyDiariesResponse.of(diary.getDiaryId(), imageUrl,
                    diary.getDiaryDate());
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void reviewDiary(Long userId, Long diaryId, ReviewType reviewType) {
        User user = validateUserService.validateUserById(userId);
        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);

        diary.reviewDiary(reviewType);
    }
}
