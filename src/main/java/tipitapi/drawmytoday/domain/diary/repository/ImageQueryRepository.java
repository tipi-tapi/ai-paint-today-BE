package tipitapi.drawmytoday.domain.diary.repository;

import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Image;

public interface ImageQueryRepository {

    Optional<Image> findImage(Long imageId);

    Long countImage(Long diaryId);
}
