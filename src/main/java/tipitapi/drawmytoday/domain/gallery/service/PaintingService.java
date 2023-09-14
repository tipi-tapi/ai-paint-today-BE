package tipitapi.drawmytoday.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tipitapi.drawmytoday.common.converter.GallerySort;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.gallery.repository.PaintingRepository;
import tipitapi.drawmytoday.domain.r2.service.R2PreSignedService;

@Service
@RequiredArgsConstructor
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final R2PreSignedService r2PreSignedService;
    @Value("${presigned-image.expiration.admin-diaries}")
    private int imageExpiration;

    public Page<GetPaintingResponse> getAllPaintings(int size, int page, GallerySort sort) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        switch (sort) {
            case POPULARITY:
                return paintingRepository.findAllByPopularity(pageable)
                    .map(this::generatePresignedURL);
            case LATEST:
                return paintingRepository.findAllByRecent(pageable)
                    .map(this::generatePresignedURL);
            default:
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private GetPaintingResponse generatePresignedURL(GetPaintingResponse response) {
        response.updateImageUrl(
            r2PreSignedService.getPreSignedUrlForShare(response.getImageURL(), imageExpiration));
        return response;
    }
}
