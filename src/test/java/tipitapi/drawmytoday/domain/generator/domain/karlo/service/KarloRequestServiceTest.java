package tipitapi.drawmytoday.domain.generator.domain.karlo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.generator.domain.karlo.dto.KarloImageUrlResponse;
import tipitapi.drawmytoday.domain.generator.domain.karlo.dto.KarloUrlResponse;
import tipitapi.drawmytoday.domain.generator.domain.karlo.exception.KarloRequestFailException;
import tipitapi.drawmytoday.domain.generator.exception.ImageInputStreamFailException;

@ExtendWith(MockitoExtension.class)
class KarloRequestServiceTest {

    @Mock
    RestTemplate restTemplate;

    String karloImageCreateUrl = "karlo-api-url";

    KarloRequestService karloRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.karloRequestService = new KarloRequestService(restTemplate, karloImageCreateUrl);
    }

    @Nested
    @DisplayName("getImageAsUrl 메서드 테스트")
    class GetImageAsUrl_test {

        @Nested
        @DisplayName("karlo 요청을 보내고")
        class Send_karlo_request {

            @Test
            @DisplayName("응답이 없으면 예외를 던진다")
            void when_response_is_null_then_throw_exception() {
                // given
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(null);

                // when
                // then
                assertThatThrownBy(() -> karloRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(KarloRequestFailException.class);
            }

            @Test
            @DisplayName("비정상적인 응답이 오면 예외를 던진다")
            void when_invalid_response_then_throw_exception() throws Exception {
                // given
                HttpClientErrorException badRequest = new HttpClientErrorException(
                    HttpStatus.BAD_REQUEST, "bad request");
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willThrow(badRequest);

                // when
                // then
                assertThatThrownBy(() -> karloRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(KarloRequestFailException.class);
            }

            @Test
            @DisplayName("응답받은 url에 해당하는 이미지를 가져올 수 없다면 예외를 던진다")
            void when_get_image_fail_then_throw_exception() {
                // given
                KarloImageUrlResponse karloImageUrlResponse = new KarloImageUrlResponse(
                    "id", 1L, "invalid url");
                KarloUrlResponse response = new KarloUrlResponse(
                    "id", "modelVersion", List.of(karloImageUrlResponse));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(response);

                // when
                // then
                assertThatThrownBy(() -> karloRequestService.getImageAsUrl("prompt"))
                    .isInstanceOf(ImageInputStreamFailException.class);
            }

            @Test
            @DisplayName("정상적인 응답이 오면 응답받은 url에 해당하는 이미지를 가져온다")
            void when_get_image_success_then_return_image() throws Exception {
                // given
                KarloImageUrlResponse karloImageUrlResponse = new KarloImageUrlResponse(
                    "id", 1L, "https://google.com");
                KarloUrlResponse response = new KarloUrlResponse(
                    "id", "modelVersion", List.of(karloImageUrlResponse));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(response);

                // when
                byte[] image = karloRequestService.getImageAsUrl("prompt");

                // then
                assertThat(image).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("getTestImageAsUrl 테스트")
    class GetTestImageAsUrl_test {

        @Test
        @DisplayName("응답이 없으면 예외를 던진다")
        void when_response_is_null_then_throw_exception() {
            // given
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willReturn(null);

            // when
            // then
            assertThatThrownBy(() -> karloRequestService.getTestImageAsUrl(getKarloParameter()))
                .isInstanceOf(KarloRequestFailException.class);
        }

        @Test
        @DisplayName("비정상적인 응답이 오면 예외를 던진다")
        void when_invalid_response_then_throw_exception() throws Exception {
            // given
            HttpClientErrorException badRequest = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "bad request");
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willThrow(badRequest);

            // when
            // then
            assertThatThrownBy(() -> karloRequestService.getTestImageAsUrl(getKarloParameter()))
                .isInstanceOf(KarloRequestFailException.class);
        }

        @Test
        @DisplayName("응답받은 url에 해당하는 이미지를 가져올 수 없다면 예외를 던진다")
        void when_get_image_fail_then_throw_exception() {
            // given
            KarloImageUrlResponse karloImageUrlResponse = new KarloImageUrlResponse(
                "id", 1L, "invalid url");
            KarloUrlResponse response = new KarloUrlResponse(
                "id", "modelVersion", List.of(karloImageUrlResponse));
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willReturn(response);

            // when
            // then
            assertThatThrownBy(() -> karloRequestService.getTestImageAsUrl(getKarloParameter()))
                .isInstanceOf(ImageInputStreamFailException.class);
        }

        @Test
        @DisplayName("정상적인 응답이 오면 응답받은 url에 해당하는 이미지들을 가져온다")
        void when_get_image_success_then_return_image() throws Exception {
            // given
            KarloImageUrlResponse karloImageUrlResponse1 = new KarloImageUrlResponse(
                "id1", 1L, "https://google.com");
            KarloImageUrlResponse karloImageUrlResponse2 = new KarloImageUrlResponse(
                "id2", 2L, "https://google.com");
            KarloUrlResponse response = new KarloUrlResponse(
                "id", "modelVersion",
                List.of(karloImageUrlResponse1, karloImageUrlResponse2));
            given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                any(Class.class))).willReturn(response);

            // when
            List<byte[]> testImages = karloRequestService.getTestImageAsUrl(getKarloParameter());

            // then
            assertThat(testImages).hasSize(2);
        }

        private KarloParameter getKarloParameter() {
            return new KarloParameter(
                "prompt",
                "negativePrompt",
                2,
                10,
                50D,
                new Long[]{1L, 2L}
            );
        }
    }
}