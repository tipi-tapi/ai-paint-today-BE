package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface ImageQueryRepository {

    List<Image> findLatestByDiary(Long diaryId);

    Optional<Image> findImage(Long imageId);

    Long countImage(Long diaryId);

    List<Image> findByDiary(Long diaryId);

    Optional<Image> findByImageIdAndDiaryUser(Long imageId, User user);

}
