package tipitapi.drawmytoday.domain.diary.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.diary.domain.Diary;

public interface DiaryQueryRepository {

    Page<GetDiaryAdminResponse> getDiariesForMonitorAsPage(Pageable pageable,
        Direction direction, Long emotionId);

    Optional<Diary> getDiaryExistsByDiaryDate(Long userId, LocalDate diaryDate);
}
