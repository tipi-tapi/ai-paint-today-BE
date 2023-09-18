package tipitapi.drawmytoday.domain.gallery.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.converter.GallerySort;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingRepository;
import tipitapi.drawmytoday.domain.r2.service.R2PreSignedService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final R2PreSignedService r2PreSignedService;
    @Value("${presigned-image.expiration.admin-diaries}")
    private int imageExpiration;

    public Page<GetPaintingResponse> getAllPaintings(int size, int page, GallerySort sort) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        if (sort == GallerySort.POPULARITY) {
            return paintingRepository.findAllByPopularity(pageable)
                .map(this::generatePresignedURL);
        } else {
            return paintingRepository.findAllByRecent(pageable)
                .map(this::generatePresignedURL);
        }
    }

    @Transactional
    public void deletePainting(Long paintingId) {
        paintingRepository.deleteById(paintingId);
    }

    private GetPaintingResponse generatePresignedURL(GetPaintingResponse response) {
        response.updateImageUrl(
            r2PreSignedService.getPreSignedUrlForShare(response.getImageURL(), imageExpiration));
        return response;
    }
}
