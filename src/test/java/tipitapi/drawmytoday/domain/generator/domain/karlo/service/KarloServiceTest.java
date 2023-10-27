package tipitapi.drawmytoday.domain.generator.domain.karlo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestPrompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.generator.domain.karlo.exception.KarloRequestFailException;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;

@ExtendWith(MockitoExtension.class)
class KarloServiceTest {

    @Mock
    PromptService promptService;
    @Mock
    KarloRequestService karloRequestService;
    @InjectMocks
    KarloService karloService;

    @Nested
    @DisplayName("generateImage(Prompt) 메서드 테스트")
    class GenerateImage_test {

        @Test
        @DisplayName("karlo 요청을 실패할 경우 fail prompt를 저장한다.")
        void karlo_request_fail_then_generateImage_fail() throws Exception {
            // given
            String prompt = "prompt";
            given(karloRequestService.getImageAsUrl(eq(prompt)))
                .willThrow(KarloRequestFailException.class);

            // when
            // then
            assertThatThrownBy(() -> karloService.generateImage(
                TestPrompt.createPromptWithId(1L, prompt)))
                .isInstanceOf(KarloRequestFailException.class);
            verify(promptService).createPrompt(any(String.class), eq(false));
        }

        @Test
        @DisplayName("karlo 요청을 성공할 경우 성공한 prompt를 저장한다.")
        void karlo_request_success_then_generateImage_success() throws Exception {
            // given
            String prompt = "prompt";
            byte[] image = new byte[0];
            given(karloRequestService.getImageAsUrl(eq(prompt))).willReturn(image);

            // when
            GeneratedImageAndPrompt generatedImageAndPrompt = karloService.generateImage(
                TestPrompt.createPromptWithId(1L, prompt));

            // then
            verify(promptService, never()).createPrompt(any(String.class), eq(false));
            assertThat(generatedImageAndPrompt.getPrompt()).isEqualTo(prompt);
            assertThat(generatedImageAndPrompt.getImage()).isEqualTo(image);
        }
    }

    @Nested
    @DisplayName("generateTestImage 메서드 테스트")
    class GenerateTestImage_test {

        @Test
        @DisplayName("karlo 요청을 실패할 경우 fail prompt를 저장한다.")
        void karlo_request_fail_then_generateImage_fail() throws Exception {
            // given
            KarloParameter karloParameter = new KarloParameter("prompt", "negativePrompt",
                1, 10, 10D, null);
            CreateTestDiaryRequest request = new CreateTestDiaryRequest(1L, "notes",
                LocalDate.now(), LocalTime.now(), karloParameter);
            given(karloRequestService.getTestImageAsUrl(any(KarloParameter.class)))
                .willThrow(KarloRequestFailException.class);

            // when
            // then
            assertThatThrownBy(() -> karloService.generateTestImage(request))
                .isInstanceOf(KarloRequestFailException.class);
            verify(promptService).createPrompt(any(String.class), eq(false));
        }

        @Test
        @DisplayName("karlo 요청을 성공할 경우 이미지들을 반환한다.")
        void karlo_request_success_then_return_images() throws Exception {
            // given
            KarloParameter karloParameter = new KarloParameter("prompt", "negativePrompt",
                1, 10, 10D, null);
            CreateTestDiaryRequest request = new CreateTestDiaryRequest(1L, "notes",
                LocalDate.now(), LocalTime.now(), karloParameter);
            List<byte[]> images = List.of(new byte[0]);
            given(karloRequestService.getTestImageAsUrl(any(KarloParameter.class)))
                .willReturn(images);

            // when
            List<byte[]> returnImages = karloService.generateTestImage(request);

            // then
            verify(promptService, never()).createPrompt(any(String.class), eq(false));
            assertThat(returnImages).isEqualTo(images);
        }
    }
}