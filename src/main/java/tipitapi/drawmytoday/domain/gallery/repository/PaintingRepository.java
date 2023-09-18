package tipitapi.drawmytoday.domain.gallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;

public interface PaintingRepository extends JpaRepository<Painting, Long>, PaintingQueryRepository {

}
