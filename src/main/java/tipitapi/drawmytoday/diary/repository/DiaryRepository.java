package tipitapi.drawmytoday.diary.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tipitapi.drawmytoday.diary.domain.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryQueryRepository {

    @EntityGraph(attributePaths = {"imageList"})
    List<Diary> findAllByUserUserIdAndDiaryDateBetween(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth);

    Optional<Diary> findFirstByUserUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select d from Diary d where d.user.userId = ?1 and DATE(d.diaryDate) = ?2 order by d.createdAt desc")
    List<Diary> findByUserIdAndDiaryDate(Long userId, Date diaryDate);
}