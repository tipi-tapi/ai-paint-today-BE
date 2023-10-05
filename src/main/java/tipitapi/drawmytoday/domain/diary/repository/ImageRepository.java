package tipitapi.drawmytoday.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.diary.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageQueryRepository {

}