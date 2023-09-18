package tipitapi.drawmytoday.domain.gallery.service;

import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingReport;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingReportRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaintingReportService {

    private final EntityManager em;
    private final PaintingReportRepository paintingReportRepository;
    private final ValidatePaintingReportService validatePaintingReportService;

    @Transactional
    public void reportPainting(User user, Painting painting) {
        validatePaintingReportService.validatePaintingReport(painting);
        paintingReportRepository.save(PaintingReport.create(user, painting, null));
    }
}
