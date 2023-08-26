package tipitapi.drawmytoday.domain.diary.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.repository.ImageRepository;
import tipitapi.drawmytoday.domain.r2.service.R2Service;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final R2Service r2Service;

    @Value("${spring.profiles.active:Unknown}")
    private String profile;

    public Image getImage(Diary diary) {
        return imageRepository.findByIsSelectedTrueAndDiary(diary)
            .orElseThrow(ImageNotFoundException::new);
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
}
