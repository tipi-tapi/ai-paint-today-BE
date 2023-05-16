package tipitapi.drawmytoday.diary.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.user.domain.User;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByIdAndUser(Long id, User user);
}