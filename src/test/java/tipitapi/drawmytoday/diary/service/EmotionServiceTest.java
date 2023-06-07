package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotionInActive;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.dto.GetActiveEmotionsResponse;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.emotion.service.EmotionService;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
public class EmotionServiceTest {

    @Mock
    EmotionRepository emotionRepository;

    @Mock
    ValidateUserService validateUserService;

    @InjectMocks
    EmotionService emotionService;

    @Nested
    @DisplayName("getActiveEmotions 메소드 테스트")
    class GetActiveEmotionsTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> emotionService.getActiveEmotions(1L))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재할 경우")
        class if_user_exists {

            @Test
            @DisplayName("활성화된 감정 목록을 반환한다.")
            void it_returns_active_emotions() {
                User user = createUserWithId(1L);
                Emotion activeEmotion = createEmotion();
                Emotion inActiveEmotion = createEmotionInActive();
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(emotionRepository.findAllActiveEmotions()).willReturn(List.of(activeEmotion));

                List<GetActiveEmotionsResponse> emotions = emotionService.getActiveEmotions(1L);

                assertThat(emotions.get(0).getId()).isEqualTo(activeEmotion.getEmotionId());
                assertThat(emotions).extracting(GetActiveEmotionsResponse::getId)
                    .isNotEqualTo(inActiveEmotion.getEmotionId());
            }
        }

    }
}
