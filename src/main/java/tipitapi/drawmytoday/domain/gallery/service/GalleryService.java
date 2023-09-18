package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.converter.GallerySort;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GalleryService {

    private final ValidateUserService validateUserService;
    private final ValidatePaintingService validatePaintingService;
    private final PaintingService paintingService;
    private final PaintingLikeService PaintingLikeService;
    private final PaintingReportService paintingReportService;

    public Page<GetPaintingResponse> getGallery(Long userId, int size, int page, GallerySort sort) {
        validateUserService.validateUserById(userId);
        return paintingService.getAllPaintings(size, page, sort);
    }

    @Transactional
    public void togglePaintingLike(Long userId, Long paintingId) {
        User user = validateUserService.validateUserById(userId);
        Painting painting = validatePaintingService.validateIsNotPaintingOwner(userId, paintingId);
        PaintingLikeService.togglePaintingLike(user, painting);
    }

    @Transactional
    public void deletePainting(Long userId, Long paintingId) {
        validateUserService.validateUserById(userId);
        validatePaintingService.validateIsPaintingOwner(userId, paintingId);
        paintingService.deletePainting(paintingId);
    }

    @Transactional
    public void reportPainting(Long userId, Long paintingId) {
        User user = validateUserService.validateUserById(userId);
        Painting painting = validatePaintingService.validateIsNotPaintingOwner(userId, paintingId);
        paintingReportService.reportPainting(user, painting);
    }
}
