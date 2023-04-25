package tipitapi.drawmytoday.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.diary.domain.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

}