package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
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

    public GetDiaryResponse getDiary(Long userId, Long diaryId) {
        User user = validateUserService.validateUserById(userId);

        Diary diary = validateDiaryService.validateDiaryById(diaryId, user);
        diary.setNotes(encryptor.decrypt(diary.getNotes()));

        List<Image> images = imageService.getLatestImages(diary);
        Image selectedImage = images.stream()
            .filter(Image::isSelected)
            .findFirst()
            .orElseThrow(ImageNotFoundException::new);
        String selectedImageUrl = r2PreSignedService.getCustomDomainUrl(
            selectedImage.getImageUrl());

        List<GetImageResponse> sortedImages = images.stream()
            .map(image ->
                GetImageResponse.of(image.getImageId(), image.getCreatedAt(), image.isSelected(),
                    r2PreSignedService.getCustomDomainUrl(image.getImageUrl())))
            .collect(Collectors.toList());

        String emotionText = diary.getEmotion().getEmotionPrompt();

        String promptText = promptService.getPromptByImageId(selectedImage.getImageId())
            .map(Prompt::getPromptText).orElse(null);

        return GetDiaryResponse.of(diary, selectedImageUrl, sortedImages, emotionText, promptText);
    }

    @Transactional
    public List<GetMonthlyDiariesResponse> getMonthlyDiaries(Long userId, int year, int month) {
        validateUserService.validateUserById(userId);
        LocalDateTime startMonth = DateUtils.getStartDate(year, month);
        LocalDateTime endMonth = DateUtils.getEndDate(year, month);
        List<GetMonthlyDiariesResponse> monthlyDiaries = diaryRepository.getMonthlyDiaries(
            userId, startMonth, endMonth);

        validateSelectedImageAndConvertUrl(monthlyDiaries);

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

    private void validateSelectedImageAndConvertUrl(
        List<GetMonthlyDiariesResponse> monthlyDiaries) {
        for (int i = 0; i < monthlyDiaries.size(); i++) {
            GetMonthlyDiariesResponse diaryResponse = monthlyDiaries.get(i);
            if (diaryResponse.getImageUrl() == null) {
                log.error("DiaryId가 {}인 일기에 해당하는 대표 이미지가 없습니다.", diaryResponse.getId());
                Optional<Image> latestImage = imageService.getOneLatestImage(diaryResponse.getId());
                if (latestImage.isPresent()) {
                    latestImage.get().setSelected(true);
                    diaryResponse.setImageUrl(
                        r2PreSignedService.getCustomDomainUrl(latestImage.get().getImageUrl()));
                } else {
                    monthlyDiaries.remove(i--);
                }
            } else {
                diaryResponse.setImageUrl(
                    r2PreSignedService.getCustomDomainUrl(diaryResponse.getImageUrl()));
            }
        }
    }
}
