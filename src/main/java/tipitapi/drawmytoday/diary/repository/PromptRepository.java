package tipitapi.drawmytoday.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.diary.domain.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    List<Prompt> findAllByDiaryDiaryId(Long diaryId);
}