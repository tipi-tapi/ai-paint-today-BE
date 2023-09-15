package tipitapi.drawmytoday.domain.gallery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingReport;

public interface PaintingReportRepository extends JpaRepository<PaintingReport, Long> {

    Optional<PaintingReport> findByUserUserIdAndPaintingPaintingId(Long userId, Long paintingId);

    Optional<PaintingReport> findByPaintingPaintingId(Long paintingId);
}
