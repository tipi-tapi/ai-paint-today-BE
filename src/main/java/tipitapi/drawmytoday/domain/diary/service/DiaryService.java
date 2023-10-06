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
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryExistByDateResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryLimitResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetImageResponse;
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

        List<Image> images = imageService.getLatestImages(diary);
        String selectedImageUrl = images.stream()
            .filter(Image::isSelected)
            .findFirst()
            .map(image -> r2PreSignedService.getCustomDomainUrl(image.getImageUrl()))
            .orElseThrow(ImageNotFoundException::new);

        List<GetImageResponse> sortedImages = images.stream()
            .map(image ->
                GetImageResponse.of(image.getImageId(), image.getCreatedAt(), image.isSelected(),
                    r2PreSignedService.getCustomDomainUrl(image.getImageUrl())))
            .collect(Collectors.toList());

        String emotionText = diary.getEmotion().getEmotionText(language);

        String promptText = promptService.getPromptByDiaryId(diaryId)
            .map(Prompt::getPromptText).orElse(null);

        return GetDiaryResponse.of(diary, selectedImageUrl, sortedImages, emotionText, promptText);
    }

    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, int year, int month) {
        validateUserService.validateUserById(userId);
        LocalDateTime startMonth = DateUtils.getStartDate(year, month);
        LocalDateTime endMonth = DateUtils.getEndDate(year, month);
        List<GetMonthlyDiariesResponse> monthlyDiaries = diaryRepository.getMonthlyDiaries(
            userId, startMonth, endMonth);
        for (GetMonthlyDiariesResponse monthlyDiary : monthlyDiaries) {
            monthlyDiary.setImageUrl(
                r2PreSignedService.getCustomDomainUrl(monthlyDiary.getImageUrl()));
        }
        return monthlyDiaries;
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
}
