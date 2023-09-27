package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageQueryRepository {

    Optional<Image> findByIsSelectedTrueAndDiary(Diary diary);

    Optional<Image> findByImageIdAndDiaryUser(Long imageId, User user);

    List<Image> findAllByDiaryDiaryId(Long diaryId);
}