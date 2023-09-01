package tipitapi.drawmytoday.domain.dalle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
import tipitapi.drawmytoday.domain.dalle.dto.DallEUrlResponse;
import tipitapi.drawmytoday.domain.dalle.dto.DallEUrlResponse.DallEUrl;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.dalle.exception.ImageInputStreamFailException;

@ExtendWith(MockitoExtension.class)
class DallERequestServiceTest {

    @Mock
    RestTemplate restTemplate;
    String apiUrl = "dalle-api-url";
    DallERequestService dalleRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dalleRequestService = new DallERequestService(restTemplate, apiUrl);
    }

    @Nested
    @DisplayName("getImageAsUrl 메서드 테스트")
    class GetImageAsUrl_test {

        @Nested
        @DisplayName("DallE 요청을 보내고")
        class Send_dallE_request {

            @Test
            @DisplayName("응답이 없으면 예외를 던진다")
            void when_response_is_null_then_throw_exception() {
                // given
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(null);

                // when
                // then
                assertThatThrownBy(() -> dalleRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(DallERequestFailException.class);
            }

            @Test
            @DisplayName("정책 위반 응답이 오면 null을 반환한다")
            void when_response_is_content_policy_error_then_return_null() throws Exception {
                // given
                String responsebody = "{\n"
                    + "  \"error\": {\n"
                    + "    \"code\": \"content_policy_violation\",\n"
                    + "    \"message\": \"Your request was rejected as a result of our safety system. Your prompt may contain text that is not allowed by our safety system.\",\n"
                    + "    \"param\": null,\n"
                    + "    \"type\": \"invalid_request_error\"\n"
                    + "  }\n"
                    + "}\n";
                HttpClientErrorException badRequest = new HttpClientErrorException(
                    HttpStatus.BAD_REQUEST,
                    "bad request",
                    responsebody.getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willThrow(badRequest);

                // when
                byte[] image = dalleRequestService.getImageAsUrl("prompt");

                // then
                assertThat(image).isNull();
            }

            @Test
            @DisplayName("정책 위반이 아닌 비정상적인 응답이 오면 예외를 던진다")
            void when_invalid_response_then_throw_exception() throws Exception {
                // given
                HttpClientErrorException badRequest = new HttpClientErrorException(
                    HttpStatus.BAD_REQUEST, "bad request");
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willThrow(badRequest);

                // when
                // then
                assertThatThrownBy(() -> dalleRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(DallERequestFailException.class);
            }

            @Test
            @DisplayName("응답받은 url에 해당하는 이미지를 가져올 수 없다면 예외를 던진다")
            void when_get_image_fail_then_throw_exception() {
                // given
                DallEUrlResponse response = new DallEUrlResponse();
                DallEUrl dallEUrl = new DallEUrl();
                ReflectionTestUtils.setField(dallEUrl, "url", "invalid-url");
                ReflectionTestUtils.setField(response, "data", List.of(dallEUrl));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(response);

                // when
                // then
                assertThatThrownBy(() -> dalleRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(ImageInputStreamFailException.class);
            }

            @Test
            @DisplayName("응답 파싱에 성공하면 url을 반환한다")
            void when_response_parsing_success_then_return_url() throws Exception {
                // given
                DallEUrlResponse response = new DallEUrlResponse();
                DallEUrl dallEUrl = new DallEUrl();
                ReflectionTestUtils.setField(dallEUrl, "url", "https://google.com");
                ReflectionTestUtils.setField(response, "data", List.of(dallEUrl));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(response);

                // when
                byte[] image = dalleRequestService.getImageAsUrl("prompt");

                // then
                assertThat(image).isNotEmpty();
            }
        }
    }

}