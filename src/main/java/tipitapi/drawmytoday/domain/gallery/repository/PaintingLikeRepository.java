package tipitapi.drawmytoday.domain.gallery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingLike;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface PaintingLikeRepository extends JpaRepository<PaintingLike, Long> {

    Optional<PaintingLike> findByUserAndPainting(User user, Painting painting);
}
