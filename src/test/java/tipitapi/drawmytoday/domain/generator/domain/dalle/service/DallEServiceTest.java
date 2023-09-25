package tipitapi.drawmytoday.domain.generator.domain.dalle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.exception.ErrorCode.DALLE_CONTENT_POLICY_VIOLATION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallEPolicyViolationException;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;

@ExtendWith(MockitoExtension.class)
class DallEServiceTest {

    @Mock
    PromptTextService promptTextService;
    @Mock
    PromptService promptService;
    @Mock
    DallERequestService dalleRequestService;
    @InjectMocks
    DallEService dallEService;

    @Nested
    @DisplayName("generateImage 메서드는")
    class GenerateImage_test {

        @Test
        @DisplayName("dalle 요청을 실패할 경우 fail prompt를 저장한다.")
        void dalle_request_fail_then_generateImage_fail() throws Exception {
            // given
            Emotion emotion = TestEmotion.createEmotion();
            String keyword = "keyword";
            String prompt = "prompt";
            given(promptTextService.createPromptText(any(Emotion.class), any(String.class)))
                .willReturn(prompt);
            given(dalleRequestService.getImageAsUrl(eq(prompt)))
                .willThrow(DallERequestFailException.class);

            // when
            // then
            assertThatThrownBy(() -> dallEService.generateImage(emotion, keyword))
                .isInstanceOf(DallERequestFailException.class);
            verify(promptService).createPrompt(any(String.class), eq(false));
        }

        @Nested
        @DisplayName("dalle 요청시 dalle 정책 위반에 대한 응답이 오면")
        class Dalle_request_policy_violation {

            @Test
            @DisplayName("fail prompt 저장 후 다시 요청을 보낸 후 이미지를 반환한다.")
            void retry_and_return_image() throws Exception {
                // given
                String prompt = "prompt";
                String otherPrompt = "otherPrompt";
                String keyword = "keyword";
                byte[] image = new byte[0];
                Emotion emotion = TestEmotion.createEmotion();
                given(promptTextService.createPromptText(eq(emotion), eq(keyword)))
                    .willReturn(prompt);
                given(dalleRequestService.getImageAsUrl(eq(prompt))).willThrow(
                    DallEPolicyViolationException.class);
                given(promptTextService.createPromptText(eq(emotion), eq(null)))
                    .willReturn(otherPrompt);
                given(dalleRequestService.getImageAsUrl(eq(otherPrompt))).willReturn(image);

                // when
                GeneratedImageAndPrompt generatedImageAndPrompt = dallEService.generateImage(
                    emotion, keyword);

                // then
                verify(promptService).createPrompt(eq(prompt), eq(false));
                verify(promptTextService).createPromptText(any(Emotion.class), eq(null));
                assertThat(generatedImageAndPrompt.getImage()).isEqualTo(image);
            }

            @Test
            @DisplayName("fail prompt 저장 후 다시 요청을 보내도 정책 위반 응답이 오면 예외를 던진다.")
            void retry_but_policy_violation_then_throw_exception() throws Exception {
                // given
                String prompt = "prompt";
                String otherPrompt = "otherPrompt";
                String keyword = "keyword";
                Emotion emotion = TestEmotion.createEmotion();
                given(promptTextService.createPromptText(eq(emotion), eq(keyword)))
                    .willReturn(prompt);
                given(dalleRequestService.getImageAsUrl(eq(prompt))).willThrow(
                    DallEPolicyViolationException.class);
                given(promptTextService.createPromptText(eq(emotion), eq(null)))
                    .willReturn(otherPrompt);
                given(dalleRequestService.getImageAsUrl(eq(otherPrompt))).willThrow(
                    DallEPolicyViolationException.class);

                // when
                // then
                assertThatThrownBy(() -> dallEService.generateImage(emotion, keyword))
                    .isInstanceOf(DallERequestFailException.class)
                    .hasMessage(DALLE_CONTENT_POLICY_VIOLATION.getMessage());
                verify(promptService).createPrompt(eq(prompt), eq(false));
                verify(promptTextService).createPromptText(any(Emotion.class), eq(null));
                verify(promptService).createPrompt(eq(otherPrompt), eq(false));
            }
        }

        @Nested
        @DisplayName("dalle 요청을 성공할 경우")
        class Dalle_request_success {

            @Test
            @DisplayName("이미지를 반환한다.")
            void save_prompt_and_return_image() throws Exception {
                // given
                String prompt = "prompt";
                String keyword = "keyword";
                byte[] image = new byte[0];
                Emotion emotion = TestEmotion.createEmotion();
                given(promptTextService.createPromptText(any(Emotion.class), any(String.class)))
                    .willReturn(prompt);
                given(dalleRequestService.getImageAsUrl(eq(prompt))).willReturn(image);

                // when
                GeneratedImageAndPrompt generatedImageAndPrompt = dallEService.generateImage(
                    emotion, keyword);

                // then
                assertThat(generatedImageAndPrompt.getImage()).isEqualTo(image);
            }
        }
    }
}