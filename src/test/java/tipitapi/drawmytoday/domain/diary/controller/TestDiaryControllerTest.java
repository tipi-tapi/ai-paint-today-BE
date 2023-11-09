package tipitapi.drawmytoday.domain.diary.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.diary.service.CreateDiaryService;

@WebMvcTest(TestDiaryController.class)
@WithCustomUser
class TestDiaryControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/diary";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateDiaryService createDiaryService;

    @DisplayName("createTestDiary 메서드는")
    @Nested
    class CreateTestDiaryTest {

        @BeforeEach
        void beforeAll() {
            requestMap = new HashMap<>();
            requestMap.put("emotionId", 1L);
            requestMap.put("notes", "notes");
            requestMap.put("diaryDate", LocalDate.now());
            requestMap.put("userTime",
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }

        private Map<String, Object> requestMap;
        private final String prompt = "prompt";
        private final String negativePrompt = "negativePrompt";
        private final Integer samples = 3;
        private final Integer priorNumInferenceSteps = 10;
        private final Double priorGuidanceScale = 50D;
        private final Integer numInferenceSteps = 50;
        private final Double guidanceScale = 5D;
        private final String scheduler = "decoder_ddim_v_prediction";
        private final Long[] seed = new Long[]{1L, 2L, 3L};

        @DisplayName("request body에 들어온 karloParam 중")
        @Nested
        class Request_body_karlo_param_is {

            @DisplayName("프롬프트 필드가 비었다면 400 에러를 반환한다")
            @ParameterizedTest
            @ValueSource(strings = {"", " ", "  "})
            void it_returns_400_if_prompt_field_is_blank(String prompt) throws Exception {
                // given
                KarloParameter karloParameter = new KarloParameter(prompt, negativePrompt,
                    samples, priorNumInferenceSteps, priorGuidanceScale, numInferenceSteps,
                    guidanceScale, scheduler, seed);
                requestMap.put("karloParameter", karloParameter);
                String requestBody = objectMapper.writeValueAsString(requestMap);

                // when
                ResultActions result = mockMvc.perform(post(BASIC_URL + "/test")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
            }

            @DisplayName("samples 필드가 양수가 아니면 400 에러를 반환한다")
            @ParameterizedTest
            @ValueSource(ints = {-1, 0})
            void it_returns_400_if_samples_not_positive(int samples) throws Exception {
                // given
                KarloParameter karloParameter = new KarloParameter(prompt, negativePrompt,
                    samples, priorNumInferenceSteps, priorGuidanceScale, numInferenceSteps,
                    guidanceScale, scheduler, seed);
                requestMap.put("karloParameter", karloParameter);
                String requestBody = objectMapper.writeValueAsString(requestMap);

                // when
                ResultActions result = mockMvc.perform(post(BASIC_URL + "/test")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
            }
        }

        @DisplayName("테스트 일기 생성에 성공하면 201 응답을 반환한다")
        @Test
        void it_returns_201_if_create_test_diary_success() throws Exception {
            // given
            CreateDiaryResponse createDiaryResponse = new CreateDiaryResponse(1L);
            given(createDiaryService.createTestDiary(anyLong(), any(CreateTestDiaryRequest.class)))
                .willReturn(createDiaryResponse);
            KarloParameter karloParameter = new KarloParameter(prompt, negativePrompt,
                samples, priorNumInferenceSteps, priorGuidanceScale, numInferenceSteps,
                guidanceScale, scheduler, seed);
            requestMap.put("karloParameter", karloParameter);
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // when
            ResultActions result = mockMvc.perform(post(BASIC_URL + "/test")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

            // then
            result.andExpect(status().isCreated());
        }
    }

}