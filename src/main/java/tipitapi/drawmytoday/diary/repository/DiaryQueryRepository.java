package tipitapi.drawmytoday.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import tipitapi.drawmytoday.admin.dto.GetDiaryAdminResponse;

public interface DiaryQueryRepository {

    Page<GetDiaryAdminResponse> getDiariesForMonitorAsPage(Pageable pageable,
        Direction direction);
}
