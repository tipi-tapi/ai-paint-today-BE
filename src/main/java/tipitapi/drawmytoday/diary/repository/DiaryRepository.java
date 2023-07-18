package tipitapi.drawmytoday.diary.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.dto.DiaryForMonitorQueryResponse;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @EntityGraph(attributePaths = {"imageList"})
    List<Diary> findAllByUserUserIdAndDiaryDateBetween(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth);

    Optional<Diary> findFirstByUserUserIdOrderByCreatedAtDesc(Long userId);

    @Query(
        value =
            "SELECT d.diary_id AS id, i.image_url AS imageUrl, p.prompt_text AS prompt, d.created_at AS createdAt FROM diary AS d "
                + "LEFT JOIN image AS i ON d.diary_id = i.diary_id "
                + "LEFT JOIN prompt AS p ON d.diary_id = p.diary_id "
                + "WHERE i.image_url != :dummyImageUrl",
        countQuery = "SELECT COUNT(*) FROM diary",
        nativeQuery = true
    )
    Page<DiaryForMonitorQueryResponse> getAllDiariesForMonitorAsPage(Pageable pageable,
        String dummyImageUrl);

    @Query("select d from Diary d where d.user.userId = ?1 and DATE(d.diaryDate) = ?2 order by d.createdAt desc")
    List<Diary> findByUserIdAndDiaryDate(Long userId, Date diaryDate);
}