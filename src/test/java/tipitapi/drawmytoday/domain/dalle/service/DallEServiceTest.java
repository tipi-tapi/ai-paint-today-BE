package tipitapi.drawmytoday.domain.dalle.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.domain.dalle.dto.DallEUrlResponse;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@ExtendWith(MockitoExtension.class)
class DallEServiceTest {

    @Mock
    RestTemplate restTemplate;
    String apiUrl = "openaiDalleUrl";
    @Mock
    PromptTextService promptTextService;
    @Mock
    PromptService promptService;
    DallEService dallEService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dallEService = new DallEService(restTemplate, apiUrl, promptTextService, promptService);
    }

    @Nested
    @DisplayName("generateImage 메서드는")
    class GenerateImage_test {

        @BeforeEach
        void setUp() {
            given(promptTextService.createPromptText(any(Emotion.class), any(String.class)))
                .willReturn("prompt");
        }

        @Test
        @DisplayName("dalle 요청을 실패할 경우 fail prompt를 저장한다.")
        void dalle_request_fail_then_generateImage_fail() {
            // given
            Emotion emotion = TestEmotion.createEmotion();
            String keyword = "keyword";
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willReturn(null);

            // when
            // then
            Assertions.assertThatThrownBy(() -> dallEService.generateImage(emotion, keyword))
                .isInstanceOf(DallERequestFailException.class);
            verify(promptService).createPrompt(any(String.class), eq(false));
        }

        @Test
        @DisplayName("dalle 요청시 dalle 정책 위반에 대한 응답이 오면 다시 요청을 보낸다.")
        void dalle_request_fail_then_generateImage_fail_and_retry() {
            // given
            String responsebody = "\n 400 Bad Request: \"{<EOL>  \"error\": {<EOL>    \"code\": \"content_policy_violation\",<EOL>    \"message\": \"Your request was rejected as a result of our safety system. Your prompt may contain text that is not allowed by our safety system.\",<EOL>    \"param\": null,<EOL>    \"type\": \"invalid_request_error\"<EOL>  }<EOL>}<EOL>\"";
            HttpClientErrorException badRequest = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "bad request",
                responsebody.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
            Emotion emotion = TestEmotion.createEmotion();
            String keyword = "keyword";
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willThrow(badRequest);

            // when
            // then
            Assertions.assertThatThrownBy(() -> dallEService.generateImage(emotion, keyword))
                .isInstanceOf(DallERequestFailException.class);
            verify(promptService).createPrompt(any(String.class), eq(false));
            verify(promptTextService).createPromptText(eq(emotion), eq(null));
        }

        @Nested
        @DisplayName("dalle 요청을 성공할 경우")
        class Dalle_request_success {


            @Test
            @DisplayName("응답값이 비정상적이면 예외를 던지고 fail prompt를 저장한다.")
            void invalid_response_then_throw_exception_and_save_prompt() {
                // given
                Emotion emotion = TestEmotion.createEmotion();
                String keyword = "keyword";
                DallEUrlResponse response = new DallEUrlResponse();
                ReflectionTestUtils.setField(response, "data", List.of());
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(response);

                // when
                // then
                Assertions.assertThatThrownBy(() -> dallEService.generateImage(emotion, keyword))
                    .isInstanceOf(DallERequestFailException.class);
                verify(promptService).createPrompt(any(String.class), eq(false));
            }

        }


    }
}