package tipitapi.drawmytoday.domain.gallery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;

public interface PaintingQueryRepository {

    Page<GetPaintingResponse> findAllByPopularity(Pageable pageable);

    Page<GetPaintingResponse> findAllByRecent(Pageable pageable);

}
