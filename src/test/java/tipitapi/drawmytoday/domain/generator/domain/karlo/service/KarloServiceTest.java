package tipitapi.drawmytoday.domain.generator.domain.karlo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestPrompt;
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

    @DisplayName("generateImage(Prompt) 메서드는")
    @Nested
    class GenerateImageTest {

        @DisplayName("karlo 요청을 실패할 경우 fail prompt를 저장한다.")
        @Test
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

        @DisplayName("karlo 요청을 성공할 경우 성공한 prompt를 저장한다.")
        @Test
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

}