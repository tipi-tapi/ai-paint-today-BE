package tipitapi.drawmytoday.domain.gallery.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingLike;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingLikeRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaintingLikeService {

    private final PaintingLikeRepository PaintingLikeRepository;

    @Transactional
    public void togglePaintingLike(User user, Painting painting) {
        Optional<PaintingLike> paintingLike =
            PaintingLikeRepository.findByUserAndPainting(user, painting);
        if (paintingLike.isPresent()) {
            PaintingLikeRepository.delete(paintingLike.get());
        } else {
            PaintingLikeRepository.save(PaintingLike.createPaintingLike(user, painting));
        }
    }
}
