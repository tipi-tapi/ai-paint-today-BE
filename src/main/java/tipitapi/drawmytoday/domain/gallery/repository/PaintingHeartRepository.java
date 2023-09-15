package tipitapi.drawmytoday.domain.gallery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingHeart;

public interface PaintingHeartRepository extends JpaRepository<PaintingHeart, Long> {

    Optional<PaintingHeart> findByUserUserIdAndPaintingPaintingId(Long userId, Long paintingId);
}
