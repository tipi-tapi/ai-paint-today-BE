package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    @Query("SELECT p FROM Image i JOIN i.prompt p WHERE i.imageId = :imageId AND p.isSuccess = true")
    List<Prompt> findAllSuccessPromptByImageId(Long imageId);
}