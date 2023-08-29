package tipitapi.drawmytoday.domain.emotion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.common.converter.Language;
import tipitapi.drawmytoday.domain.emotion.dto.CreateEmotionResponse;
import tipitapi.drawmytoday.domain.emotion.dto.GetActiveEmotionsResponse;
import tipitapi.drawmytoday.domain.emotion.service.EmotionService;

@WebMvcTest(EmotionController.class)
@WithCustomUser
class EmotionControllerTest extends ControllerTestSetup {

    private final String BASE_URL = "/emotions";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EmotionService emotionService;

    static class BlankStringArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(""),
                Arguments.of(" ")
            );
        }
    }

    @Nested
    @DisplayName("getAllEmotions 메서드는")
    class GetAllEmotions {

        @Nested
        @DisplayName("lan 쿼리 파라미터가")
        class LanguageQueryParameter {

            @Test
            @DisplayName("ko이면 한국어 감정을 반환한다.")
            void ko_than_return_korean_emotion() throws Exception {
                //given
                String language = "ko";
                List<GetActiveEmotionsResponse> emotionResponses = GetActiveEmotionsResponse.buildWithEmotions(
                    List.of(createEmotion(), createEmotion()), Language.ko);
                given(emotionService.getActiveEmotions(any(Long.class), any(Language.class)))
                    .willReturn(emotionResponses);

                //when
                ResultActions result = mockMvc.perform(get(BASE_URL + "/all")
                    .queryParam("language", language));

                //then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].id").value(emotionResponses.get(0).getId()))
                    .andExpect(jsonPath("$.data[0].name").value(emotionResponses.get(0).getName()))
                    .andExpect(
                        jsonPath("$.data[0].color").value(emotionResponses.get(0).getColor()))
                    .andExpect(jsonPath("$.data[1].id").value(emotionResponses.get(1).getId()))
                    .andExpect(jsonPath("$.data[1].name").value(emotionResponses.get(1).getName()))
                    .andExpect(
                        jsonPath("$.data[1].color").value(emotionResponses.get(1).getColor()));
            }

            @Test
            @DisplayName("en이면 영어 감정을 반환한다.")
            void en_than_return_english_emotion() throws Exception {
                //given
                String language = "en";
                List<GetActiveEmotionsResponse> emotionResponses = GetActiveEmotionsResponse.buildWithEmotions(
                    List.of(createEmotion(), createEmotion()), Language.en);
                given(emotionService.getActiveEmotions(any(Long.class), any(Language.class)))
                    .willReturn(emotionResponses);

                //when
                ResultActions result = mockMvc.perform(get(BASE_URL + "/all")
                    .queryParam("language", language));

                //then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].id").value(emotionResponses.get(0).getId()))
                    .andExpect(jsonPath("$.data[0].name").value(emotionResponses.get(0).getName()))
                    .andExpect(
                        jsonPath("$.data[0].color").value(emotionResponses.get(0).getColor()))
                    .andExpect(jsonPath("$.data[1].id").value(emotionResponses.get(1).getId()))
                    .andExpect(jsonPath("$.data[1].name").value(emotionResponses.get(1).getName()))
                    .andExpect(
                        jsonPath("$.data[1].color").value(emotionResponses.get(1).getColor()));
            }

            @Test
            @DisplayName("ko, en이 아니면 한국어 감정을 반환한다.")
            void not_ko_and_en_than_return_korean_emotion() throws Exception {
                //given
                String language = "hello";
                List<GetActiveEmotionsResponse> emotionResponses = GetActiveEmotionsResponse.buildWithEmotions(
                    List.of(createEmotion(), createEmotion()), Language.ko);
                given(emotionService.getActiveEmotions(any(Long.class), any(Language.class)))
                    .willReturn(emotionResponses);

                //when
                ResultActions result = mockMvc.perform(get(BASE_URL + "/all")
                    .queryParam("language", language));

                //then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].id").value(emotionResponses.get(0).getId()))
                    .andExpect(jsonPath("$.data[0].name").value(emotionResponses.get(0).getName()))
                    .andExpect(
                        jsonPath("$.data[0].color").value(emotionResponses.get(0).getColor()))
                    .andExpect(jsonPath("$.data[1].id").value(emotionResponses.get(1).getId()))
                    .andExpect(jsonPath("$.data[1].name").value(emotionResponses.get(1).getName()))
                    .andExpect(
                        jsonPath("$.data[1].color").value(emotionResponses.get(1).getColor()));
            }

            @Test
            @DisplayName("쿼리 파라미터가 없으면 한국어 감정을 반환한다.")
            void not_query_parameter_than_return_korean_emotion() throws Exception {
                //given
                List<GetActiveEmotionsResponse> emotionResponses = GetActiveEmotionsResponse.buildWithEmotions(
                    List.of(createEmotion(), createEmotion()), Language.ko);
                given(emotionService.getActiveEmotions(any(Long.class), any(Language.class)))
                    .willReturn(emotionResponses);

                //when
                ResultActions result = mockMvc.perform(get(BASE_URL + "/all"));

                //then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].id").value(emotionResponses.get(0).getId()))
                    .andExpect(jsonPath("$.data[0].name").value(emotionResponses.get(0).getName()))
                    .andExpect(
                        jsonPath("$.data[0].color").value(emotionResponses.get(0).getColor()))
                    .andExpect(jsonPath("$.data[1].id").value(emotionResponses.get(1).getId()))
                    .andExpect(jsonPath("$.data[1].name").value(emotionResponses.get(1).getName()))
                    .andExpect(
                        jsonPath("$.data[1].color").value(emotionResponses.get(1).getColor()));
            }
        }
    }

    @Nested
    @DisplayName("createEmotions 메서드는")
    class CreateEmotionTest {

        private final String emotionName = "기쁨";
        private final String emotionPrompt = "happy";
        private final String colorHex = "#FFFFFF";
        private final String colorPrompt = "white";

        @Test
        @DisplayName("content 값이 정상적으로 전달되면 감정을 생성한다.")
        void create_emotion() throws Exception {
            //given
            Map<String, Object> content = new HashMap<>();
            content.put("emotionName", emotionName);
            content.put("emotionPrompt", emotionPrompt);
            content.put("colorHex", colorHex);
            content.put("colorPrompt", colorPrompt);
            List<CreateEmotionResponse> createEmotionResponses = Stream.of(
                    createEmotion(), createEmotion())
                .map(CreateEmotionResponse::of)
                .collect(Collectors.toList());
            given(emotionService.createEmotions(anyList())).willReturn(createEmotionResponses);

            //when
            ResultActions result = mockMvc.perform(post(BASE_URL)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(content))));

            //then
            result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0].id").value(createEmotionResponses.get(0).getId()))
                .andExpect(jsonPath("$.data[0].emotionName").value(
                    createEmotionResponses.get(0).getEmotionName()));
        }

        @Nested
        @DisplayName("content 값 중")
        class ContentValue {

            @ParameterizedTest
            @ArgumentsSource(BlankStringArgumentsProvider.class)
            @DisplayName("emotionName이 비어있으면 400 예외를 반환한다.")
            void emotion_name_is_blank_than_throw_400_exception(String emotionName)
                throws Exception {
                //given
                Map<String, Object> content = new HashMap<>();
                content.put("emotionName", emotionName);
                content.put("emotionPrompt", emotionPrompt);
                content.put("colorHex", colorHex);
                content.put("colorPrompt", colorPrompt);

                //when
                ResultActions result = mockMvc.perform(post(BASE_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(content))));

                //then
                result.andExpect(status().isBadRequest());
                verify(emotionService, never()).createEmotions(anyList());
            }

            @ParameterizedTest
            @ArgumentsSource(BlankStringArgumentsProvider.class)
            @DisplayName("emotionPrompt가 비어있으면 400 예외를 반환한다.")
            void emotion_prompt_is_blank_than_throw_400_exception(String emotionPrompt)
                throws Exception {
                //given
                Map<String, Object> content = new HashMap<>();
                content.put("emotionName", emotionName);
                content.put("emotionPrompt", emotionPrompt);
                content.put("colorHex", colorHex);
                content.put("colorPrompt", colorPrompt);

                //when
                ResultActions result = mockMvc.perform(post(BASE_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(content))));

                //then
                result.andExpect(status().isBadRequest());
                verify(emotionService, never()).createEmotions(anyList());
            }

            @ParameterizedTest
            @ArgumentsSource(BlankStringArgumentsProvider.class)
            @DisplayName("colorHex가 비어있으면 400 예외를 반환한다.")
            void color_hex_is_blank_than_throw_400_exception(String colorHex)
                throws Exception {
                //given
                Map<String, Object> content = new HashMap<>();
                content.put("emotionName", emotionName);
                content.put("emotionPrompt", emotionPrompt);
                content.put("colorHex", colorHex);
                content.put("colorPrompt", colorPrompt);

                //when
                ResultActions result = mockMvc.perform(post(BASE_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(content))));

                //then
                result.andExpect(status().isBadRequest());
                verify(emotionService, never()).createEmotions(anyList());
            }

            @ParameterizedTest
            @ArgumentsSource(BlankStringArgumentsProvider.class)
            @DisplayName("colorPrompt가 비어있으면 400 예외를 반환한다.")
            void color_prompt_is_blank_than_throw_400_exception(String colorPrompt)
                throws Exception {
                //given
                Map<String, Object> content = new HashMap<>();
                content.put("emotionName", emotionName);
                content.put("emotionPrompt", emotionPrompt);
                content.put("colorHex", colorHex);
                content.put("colorPrompt", colorPrompt);

                //when
                ResultActions result = mockMvc.perform(post(BASE_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(content))));

                //then
                result.andExpect(status().isBadRequest());
                verify(emotionService, never()).createEmotions(anyList());
            }
        }
    }
}