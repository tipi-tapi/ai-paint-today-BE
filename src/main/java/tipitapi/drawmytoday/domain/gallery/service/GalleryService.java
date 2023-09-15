package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.converter.GallerySort;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GalleryService {

    private final ValidateUserService validateUserService;
    private final ValidatePaintingService validatePaintingService;
    private final PaintingService paintingService;
    private final PaintingHeartService paintingHeartService;

    public Page<GetPaintingResponse> getGallery(Long userId, int size, int page, GallerySort sort) {
        validateUserService.validateUserById(userId);
        return paintingService.getAllPaintings(size, page, sort);
    }

    @Transactional
    public void changePaintingHeart(Long userId, Long paintingId) {
        validateUserService.validateUserById(userId);
        validatePaintingService.validateIsNotPaintingOwner(userId, paintingId);
        paintingHeartService.changePaintingHeart(userId, paintingId);
    }

    @Transactional
    public void deletePainting(Long userId, Long paintingId) {
        validateUserService.validateUserById(userId);
        validatePaintingService.validateIsPaintingOwner(userId, paintingId);
        paintingService.deletePainting(paintingId);
    }
}
