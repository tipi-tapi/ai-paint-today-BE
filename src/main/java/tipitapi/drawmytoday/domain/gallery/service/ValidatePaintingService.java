package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.gallery.exception.PaintingOwnerException;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidatePaintingService {

    private final PaintingRepository paintingRepository;

    public void validateIsNotPaintingOwner(Long userId, Long paintingId) {
        paintingRepository.findWithImageAndDiaryByPaintingId(paintingId)
            .filter(
                painting -> !painting.getImage().getDiary().getUser().getUserId().equals(userId))
            .orElseThrow(() -> new PaintingOwnerException(ErrorCode.PAINTING_OWNER_HEART));
    }

    public void validateIsPaintingOwner(Long userId, Long paintingId) {
        paintingRepository.findWithImageAndDiaryByPaintingId(paintingId)
            .filter(
                painting -> painting.getImage().getDiary().getUser().getUserId().equals(userId))
            .orElseThrow(() -> new PaintingOwnerException(ErrorCode.NOT_PAINTING_OWNER));
    }
}
