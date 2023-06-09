package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.ImageRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image getImage(Diary diary) {
        return imageRepository.findByIsSelectedTrueAndDiary(diary)
            .orElseThrow(ImageNotFoundException::new);
    }

    public Image createImage(Diary diary, String imagePath, boolean isSelected) {
        return imageRepository.save(Image.create(diary, imagePath, isSelected));
    }
}
