package tipitapi.drawmytoday.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.diary.domain.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}