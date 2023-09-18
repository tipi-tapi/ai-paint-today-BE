package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingNotFoundException;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingOwnerException;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidatePaintingService {

    private final PaintingRepository paintingRepository;

    public Painting validateIsNotPaintingOwner(Long userId, Long paintingId) {
        Painting painting = validatePainting(paintingId);
        if (painting.getUser().getUserId().equals(userId)) {
            throw new PaintingOwnerException(ErrorCode.PAINTING_OWNER);
        }
        return painting;
    }

    public Painting validateIsPaintingOwner(Long userId, Long paintingId) {
        Painting painting = validatePainting(paintingId);
        if (!painting.getUser().getUserId().equals(userId)) {
            throw new PaintingOwnerException(ErrorCode.NOT_PAINTING_OWNER);
        }
        return painting;
    }

    public Painting validatePainting(Long paintingId) {
        return paintingRepository.findById(paintingId)
            .orElseThrow(PaintingNotFoundException::new);
    }
}
