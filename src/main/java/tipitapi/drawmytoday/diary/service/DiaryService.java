package tipitapi.drawmytoday.diary.service;

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
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Prompt;
import tipitapi.drawmytoday.diary.dto.GetDiaryExistByDateResponse;
import tipitapi.drawmytoday.diary.dto.GetDiaryLimitResponse;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.s3.service.S3PreSignedService;
import tipitapi.drawmytoday.ticket.domain.Ticket;
import tipitapi.drawmytoday.ticket.service.ValidateTicketService;
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
    private final Encryptor encryptor;
    private final ValidateDiaryService validateDiaryService;
    private final ValidateTicketService validateTicketService;

    public GetDiaryResponse getDiary(Long userId, Long diaryId, Language language) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        diary.setNotes(encryptor.decrypt(diary.getNotes()));

        String imageUrl = s3PreSignedService.getPreSignedUrlForShare(
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

        if (user.checkDrawLimit()) {
            available = true;
        } else {
            Optional<Ticket> ticket = validateTicketService.findValidTicket(userId);
            if (ticket.isPresent()) {
                available = true;
                ticketCreatedAt = ticket.get().getCreatedAt();
            }
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
                String imageUrl = s3PreSignedService.getPreSignedUrlForShare(
                    diary.getImageList().get(0).getImageUrl(), 30);
                return GetMonthlyDiariesResponse.of(diary.getDiaryId(), imageUrl,
                    diary.getDiaryDate());
            })
            .collect(Collectors.toList());
    }
}
