package tipitapi.drawmytoday.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.NotOwnerOfImageException;
import tipitapi.drawmytoday.domain.diary.repository.ImageRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateImageService {

    private final ImageRepository imageRepository;

    public Image validateImageById(Long imageId) {
        return imageRepository.findImage(imageId).orElseThrow(ImageNotFoundException::new);
    }

    public void validateImageOwner(Long imageId, User user) {
        imageRepository.findByImageIdAndDiaryUser(imageId, user)
            .orElseThrow(NotOwnerOfImageException::new);
    }
}
