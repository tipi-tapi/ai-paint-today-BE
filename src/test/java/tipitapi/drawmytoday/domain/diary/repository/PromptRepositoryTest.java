package tipitapi.drawmytoday.domain.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.config.QuerydslConfig;
import tipitapi.drawmytoday.domain.diary.domain.Diary;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfig.class)
class PromptRepositoryTest extends BaseRepositoryTest {

    @Autowired
    PromptRepository promptRepository;

    @Nested
    @DisplayName("findAllByDiaryDiaryIdAndIsSuccessTrue 메소드 테스트")
    class findAllByDiaryDiaryIdAndIsSuccessTrueTest {

        @Nested
        @DisplayName("주어진 일기의 성공한 Prompt가 존재할 경우")
        class if_success_prompt_of_diary_exists {


            @Test
            @DisplayName("성공한 Prompt들만 반환한다.")
            void return_success_prompts() {
                Diary diary = createDiary(createUser(), createEmotion());
                createPrompt(diary, "1", true);
                createPrompt(diary, "2", false);
                createPrompt(diary, "3", true);

                assertThat(
                    promptRepository.findAllByDiaryDiaryIdAndIsSuccessTrue(diary.getDiaryId())
                        .size()).isEqualTo(2);
            }
        }
    }

}