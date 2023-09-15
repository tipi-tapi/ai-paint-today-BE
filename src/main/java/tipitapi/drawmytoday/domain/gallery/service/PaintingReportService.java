package tipitapi.drawmytoday.domain.gallery.service;

import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingReport;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingReportFoundException;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingReportRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaintingReportService {

    private final EntityManager em;
    private final PaintingReportRepository paintingReportRepository;

    @Transactional
    public void reportPainting(Long userId, Long paintingId) {
        isPaintingReportExist(paintingId);
        User userReference = em.getReference(User.class, userId);
        Painting paintingReference = em.getReference(Painting.class, paintingId);
        paintingReportRepository.save(
            PaintingReport.create(userReference, paintingReference, null));
    }

    public void isPaintingReportExist(Long paintingId) {
        Optional<PaintingReport> paintingReport =
            paintingReportRepository.findByPaintingPaintingId(paintingId);
        if (paintingReport.isPresent()) {
            throw new PaintingReportFoundException();
        }
    }
}
