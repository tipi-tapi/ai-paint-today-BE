package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingReportFoundException;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingReportRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidatePaintingReportService {

    private final PaintingReportRepository paintingReportRepository;

    public void validatePaintingReport(Long paintingId) {
        paintingReportRepository.findByPaintingPaintingId(paintingId)
            .orElseThrow(PaintingReportFoundException::new);
    }
}
