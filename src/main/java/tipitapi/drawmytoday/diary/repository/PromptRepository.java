package tipitapi.drawmytoday.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.diary.domain.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

}