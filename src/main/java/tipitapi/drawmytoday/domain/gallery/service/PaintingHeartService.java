package tipitapi.drawmytoday.domain.gallery.service;

import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingHeart;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingHeartRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaintingHeartService {

    private final EntityManager em;
    private final PaintingHeartRepository paintingHeartRepository;

    @Transactional
    public void changePaintingHeart(Long userId, Long paintingId) {
        Optional<PaintingHeart> paintingHeart =
            paintingHeartRepository.findByUserUserIdAndPaintingPaintingId(userId, paintingId);
        if (paintingHeart.isPresent()) {
            paintingHeartRepository.delete(paintingHeart.get());
        } else {
            User user = em.getReference(User.class, userId);
            Painting painting = em.getReference(Painting.class, paintingId);
            paintingHeartRepository.save(PaintingHeart.createPaintingHeart(user, painting));
        }
    }
}
