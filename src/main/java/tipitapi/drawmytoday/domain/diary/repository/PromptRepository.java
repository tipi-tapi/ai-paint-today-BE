package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    List<Prompt> findAllByDiaryDiaryId(Long diaryId);
}