package tipitapi.drawmytoday.diary.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.ImageRepository;
import tipitapi.drawmytoday.s3.service.S3Service;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    public Image getImage(Diary diary) {
        return imageRepository.findByIsSelectedTrueAndDiary(diary)
            .orElseThrow(ImageNotFoundException::new);
    }

    public Image createImage(Diary diary, String imagePath, boolean isSelected) {
        return imageRepository.save(Image.create(diary, imagePath, isSelected));
    }

    public Image uploadAndCreateImage(Diary diary, byte[] dallEImage, boolean isSelected) {
        String imagePath = String.format("post/%d/%s_%d.png", diary.getDiaryId(),
            new Date().getTime(), 1);
        s3Service.uploadImage(dallEImage, imagePath);
        return createImage(diary, imagePath, isSelected);
    }
}
