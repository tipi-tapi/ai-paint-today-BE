package tipitapi.drawmytoday.domain.gallery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;

public interface PaintingRepository extends JpaRepository<Painting, Long>, PaintingQueryRepository {

    @EntityGraph(attributePaths = {"image", "image.diary"})
    Optional<Painting> findWithImageAndDiaryByPaintingId(Long paintingId);
}
