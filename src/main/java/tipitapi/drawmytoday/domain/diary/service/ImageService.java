package tipitapi.drawmytoday.domain.diary.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.exception.DiaryNeedsImageException;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.SelectedImageDeletionDeniedException;
import tipitapi.drawmytoday.domain.diary.repository.ImageRepository;
import tipitapi.drawmytoday.domain.r2.service.R2Service;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final R2Service r2Service;
    private final ValidateUserService validateUserService;
    private final ValidateDiaryService validateDiaryService;
    private final ValidateImageService validateImageService;

    @Value("${spring.profiles.active:Unknown}")
    private String profile;

    public List<Image> getLatestImages(Diary diary) {
        return imageRepository.findLatestByDiary(diary.getDiaryId());
    }

    public Optional<Image> getOneLatestImage(Long diaryId) {
        return imageRepository.findOneLastedByDiary(diaryId);
    }

    public Image createImage(Diary diary, String imagePath, boolean isSelected) {
        return imageRepository.save(Image.create(diary, imagePath, isSelected));
    }

    public Image uploadAndCreateImage(Diary diary, byte[] dallEImage, boolean isSelected) {
        String imagePath = String.format(profile + "/post/%d/%s_%d.png", diary.getDiaryId(),
            new Date().getTime(), 1);
        r2Service.uploadImage(dallEImage, imagePath);
        return createImage(diary, imagePath, isSelected);
    }

    @Transactional
    public void unSelectAllImage(Long diaryId) {
        imageRepository.findByDiary(diaryId)
            .forEach(image -> image.setSelected(false));
    }

    @Transactional
    public void deleteImage(Long userId, Long imageId) {
        User user = validateUserService.validateUserById(userId);
        Image image = validateImage(imageId, user);

        imageRepository.delete(image);
    }

    @Transactional
    public void reviewImage(Long userId, Long imageId, String review) {
        User user = validateUserService.validateUserById(userId);
        Image image = validateImageService.validateImageById(imageId);
        validateImageService.validateImageOwner(imageId, user);

        image.reviewImage(review);
    }

    @Transactional
    public void setSelectedImage(Long userId, Long imageId) {
        User user = validateUserService.validateUserById(userId);
        Image image = validateImageService.validateImageById(imageId);
        Diary diary = validateDiaryService.validateDiaryById(image.getDiary().getDiaryId(), user);

        unSelectAllImage(diary.getDiaryId());
        image.setSelected(true);
    }

    private Image validateImage(Long imageId, User user) {
        Image image = imageRepository.findImage(imageId).orElseThrow(ImageNotFoundException::new);
        if (image.isSelected()) {
            throw new SelectedImageDeletionDeniedException();
        }

        validateDiaryService.validateDiaryById(image.getDiary().getDiaryId(), user);

        if (imageRepository.countImage(image.getDiary().getDiaryId()) <= 1) {
            throw new DiaryNeedsImageException();
        }
        return image;
    }
}
