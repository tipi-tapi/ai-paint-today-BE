package tipitapi.drawmytoday.domain.diary.repository;

import tipitapi.drawmytoday.domain.diary.domain.Image;

public interface ImageRepository extends ImageQueryRepository {

    Image save(Image image);

    void delete(Image image);
}
