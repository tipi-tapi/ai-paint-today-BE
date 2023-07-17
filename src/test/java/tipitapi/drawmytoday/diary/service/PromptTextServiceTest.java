package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@ExtendWith(MockitoExtension.class)
class PromptTextServiceTest {

    @InjectMocks
    private PromptTextService promptTextService;

    @Nested
    @DisplayName("createPromptText 메서드는")
    class CreatePromptTextTest {

        @Nested
        @DisplayName("keyword가")
        class KeywordIs {

            @Test
            @DisplayName("null이면 keyword 자리에 emotional을 넣어 반환한다.")
            void nullThanAddEmotional() throws Exception {
                //given
                Emotion emotion = TestEmotion.createEmotion();
                String keyword = null;
                //when
                String promptText = promptTextService.createPromptText(emotion, keyword);

                //then
                assertThat(promptText).contains("emotional");
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "  "})
            @DisplayName("비어있으면 keyword 자리에 emotional을 넣어 반환한다.")
            void emptyThanAddEmotional(String keyword) throws Exception {
                //given
                Emotion emotion = TestEmotion.createEmotion();

                //when
                String promptText = promptTextService.createPromptText(emotion, keyword);

                //then
                assertThat(promptText).contains("emotional");
            }
        }
    }
}