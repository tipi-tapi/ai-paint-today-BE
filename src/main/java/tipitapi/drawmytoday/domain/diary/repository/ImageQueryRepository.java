package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface ImageQueryRepository {

    List<Image> findLatestByDiary(String diaryId);

    Optional<Image> findImage(String imageId);

    Long countImage(String diaryId);

    List<Image> findByDiary(String diaryId);

    Optional<Image> findByImageIdAndDiaryUser(String imageId, User user);

    Optional<Image> findRecentByDiary(String diaryId);
}
