package tipitapi.drawmytoday.domain.gallery.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.domain.PaintingReport;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingReportFoundException;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingReportRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidatePaintingReportService {

    private final PaintingReportRepository paintingReportRepository;

    public void validatePaintingReport(Painting painting) {
        Optional<PaintingReport> paintingReport = paintingReportRepository.findByPainting(painting);
        if (paintingReport.isPresent()) {
            throw new PaintingReportFoundException();
        }
    }
}
