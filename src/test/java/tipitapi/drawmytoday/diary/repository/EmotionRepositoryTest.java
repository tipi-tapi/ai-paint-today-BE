package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfig.class)
class EmotionRepositoryTest extends BaseRepositoryTest {

    @Autowired
    EmotionRepository emotionRepository;

    @Nested
    @DisplayName("findAllActiveEmotions 메소드 테스트")
    class FindAllActiveEmotionsTest {

        @Nested
        @DisplayName("활성화된 감정이 존재하지 않을 경우")
        class is_active_emotion_not_exists {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_empty_list() {
                List<Emotion> foundEmotions = emotionRepository.findAllActiveEmotions();

                assertThat(foundEmotions).isEmpty();
            }
        }

        @Nested
        @DisplayName("활성화된 감정이 존재할 경우")
        class if_active_emotions_exist {

            @Test
            @DisplayName("활성화된 감정 목록을 반환한다.")
            void it_returns_emotions_list() {
                Emotion inActiveEmotion = TestEmotion.createEmotionInActive();
                Emotion activeEmotion = TestEmotion.createEmotion();
                emotionRepository.saveAll(List.of(inActiveEmotion, activeEmotion));

                List<Emotion> foundEmotions = emotionRepository.findAllActiveEmotions();
                assertThat(foundEmotions).hasSize(1);
                assertThat(foundEmotions.get(0)).isEqualTo(activeEmotion);
            }
        }
    }
}