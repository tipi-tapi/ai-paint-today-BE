package tipitapi.drawmytoday.domain.diary.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryExistByDateResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryLimitResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetImageResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.domain.diary.service.CreateDiaryService;
import tipitapi.drawmytoday.domain.diary.service.DiaryService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.user.domain.User;

@WebMvcTest(DiaryController.class)
@WithCustomUser
class DiaryControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/diary";

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private DiaryService diaryService;
    @MockBean
    private CreateDiaryService createDiaryService;

    @Nested
    @DisplayName("getDiary 메서드는")
    class GetDiaryTest {

        @Nested
        @DisplayName("파라미터로 받은 diaryId에 해당하는 일기가")
        class If_diaryId_is {

            @Test
            @DisplayName("요청한 유저의 일기라면 OK 상태코드와 일기 아이디,이미지 url,"
                + " 일기 작성일, 일기 생성일, 감정, 일기 내용, 프롬프트를 응답한다.")
            void exist_than_return_diary() throws Exception {
                // given
                long diaryId = 1L;
                User user = TestUser.createUser();
                Emotion emotion = TestEmotion.createEmotion();
                String emotionText = emotion.getEmotionPrompt();
                Diary diary = TestDiary.createDiaryWithIdAndCreatedAt(
                    diaryId, LocalDateTime.now(), user, emotion);
                String imageUrl = "imageUrl";
                List<GetImageResponse> imageList = List.of(GetImageResponse.of(
                    1L, LocalDateTime.now(), true, imageUrl));
                String promptText = "promptText";
                GetDiaryResponse getDiaryResponse = GetDiaryResponse.of(diary, imageUrl,
                    imageList, emotionText, promptText);
                given(diaryService.getDiary(REQUEST_USER_ID, diaryId)).willReturn(
                    getDiaryResponse);

                // when
                ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + diaryId));

                // then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(diaryId))
                    .andExpect(jsonPath("$.data.imageUrl").value(imageUrl))
                    .andExpect(jsonPath("$.data.date").value(
                        parseLocalDateTime(diary.getDiaryDate())))
                    .andExpect(jsonPath("$.data.createdAt").value(
                        parseLocalDateTime(diary.getCreatedAt())))
                    .andExpect(jsonPath("$.data.emotion").value(emotionText))
                    .andExpect(jsonPath("$.data.notes").value(diary.getNotes()))
                    .andExpect(jsonPath("$.data.prompt").value(promptText));
            }
        }
    }

    @Nested
    @DisplayName("getMonthlyDiaries 메서드는")
    class GetMonthlyDiariesTest {

        @Test
        @DisplayName("요청한 유저의 해당 월 일기 아이디, 이미지 url, 생성일을 리스트로 응답한다.")
        void return_monthly_diaries() throws Exception {
            // given
            int year = 2023;
            int month = 7;
            List<GetMonthlyDiariesResponse> monthlyDiaries = new ArrayList<>();
            for (long diaryId = 1; diaryId < 4; diaryId++) {
                monthlyDiaries.add(new GetMonthlyDiariesResponse(diaryId, "imageUrl",
                    LocalDateTime.of(year, month, 1, 1, 1, 1)));
            }
            given(diaryService.getMonthlyDiaries(REQUEST_USER_ID, 2023, 7)).willReturn(
                monthlyDiaries);

            // when
            ResultActions result = mockMvc.perform(get(BASIC_URL + "/calendar/monthly")
                .queryParam("year", String.valueOf(year))
                .queryParam("month", String.valueOf(month)));

            // then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(monthlyDiaries.size()))
                .andExpect(jsonPath("$.data[0].id").value(monthlyDiaries.get(0).getId()))
                .andExpect(
                    jsonPath("$.data[0].imageUrl").value(monthlyDiaries.get(0).getImageUrl()))
                .andExpect(
                    jsonPath("$.data[0].date").value(monthlyDiaries.get(0).getDate().toString()));
        }
    }

    @Nested
    @DisplayName("getDiaryExistByDate 메서드는")
    class GetDiaryExistByDateTest {

        @Nested
        @DisplayName("주어진 날짜에 일기가 존재하지 않는다면")
        class if_diary_not_exist_at_date {

            @Test
            @DisplayName("false를 응답한다.")
            void return_false() throws Exception {
                // given
                int year = 2023, month = 7, day = 1;
                given(diaryService.getDiaryExistByDate(REQUEST_USER_ID, year, month, day))
                    .willReturn(GetDiaryExistByDateResponse.ofNotExist());

                // when
                ResultActions result = mockMvc.perform(get(BASIC_URL + "/calendar/date")
                    .queryParam("year", String.valueOf(year))
                    .queryParam("month", String.valueOf(month))
                    .queryParam("day", String.valueOf(day)));

                // then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exist").value(false))
                    .andExpect(jsonPath("$.data.diaryId").doesNotExist());
            }
        }

        @Nested
        @DisplayName("주어진 날짜에 일기가 존재한다면")
        class if_diary_exist_at_date {

            @Test
            @DisplayName("true와 일기 ID를 응답한다.")
            void return_true_and_diary_id() throws Exception {
                // given
                int year = 2023, month = 7, day = 1;
                long diaryId = 1L;
                given(diaryService.getDiaryExistByDate(REQUEST_USER_ID, year, month, day))
                    .willReturn(GetDiaryExistByDateResponse.ofExist(diaryId));

                // when
                ResultActions result = mockMvc.perform(get(BASIC_URL + "/calendar/date")
                    .queryParam("year", String.valueOf(year))
                    .queryParam("month", String.valueOf(month))
                    .queryParam("day", String.valueOf(day)));

                // then
                result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exist").value(true))
                    .andExpect(jsonPath("$.data.diaryId").value(diaryId));
            }
        }

    }

    @Nested
    @DisplayName("getLastCreation 메서드는")
    class GetLastCreationTest {

        @Test
        @DisplayName("유저가 마지막으로 일기를 생성한 시각을 반환한다.")
        void return_last_creation() throws Exception {
            // given
            LocalDateTime lastCreation = LocalDateTime.now();
            given(diaryService.getLastCreation(REQUEST_USER_ID)).willReturn(
                new GetLastCreationResponse(lastCreation));

            // when
            ResultActions result = mockMvc.perform(get(BASIC_URL + "/last-creation"));

            // then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lastCreation").value(parseLocalDateTime(lastCreation)));
        }
    }

    @Nested
    @DisplayName("createDiary 메서드는")
    class CreateDiaryTest {

        private final String keyword = "keyword";
        private final String notes = "notes";
        private final LocalDate diaryDate = LocalDate.now();
        private final Long emotionId = 1L;
        private final LocalTime userTime = LocalTime.now();

        @Nested
        @DisplayName("content 값 중")
        class If_content_value {

            @Test
            @DisplayName("emotionId가 없다면 BAD_REQUEST 상태코드를 응답한다.")
            void emotionId_is_null_than_return_bad_request() throws Exception {
                // given
                // when
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("keyword", keyword);
                requestMap.put("notes", notes);
                requestMap.put("diaryDate", diaryDate);
                String requestBody = objectMapper.writeValueAsString(requestMap);
                ResultActions result = mockMvc.perform(post(BASIC_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
                verify(createDiaryService, never()).createDiary(any(Long.class),
                    any(Long.class), any(String.class), any(String.class), any(LocalDate.class),
                    any(LocalTime.class));
            }

            @Test
            @DisplayName("notes 값이 6010byte가 넘는다면 BAD_REQUEST 상태코드를 응답한다.")
            void notes_is_null_than_return_bad_request() throws Exception {
                // given
                // when
                String overNotes = "a".repeat(6011);
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("emotionId", emotionId);
                requestMap.put("keyword", keyword);
                requestMap.put("notes", overNotes);
                requestMap.put("diaryDate", diaryDate);
                String requestBody = objectMapper.writeValueAsString(requestMap);
                ResultActions result = mockMvc.perform(post(BASIC_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
                verify(createDiaryService, never()).createDiary(any(Long.class),
                    any(Long.class), any(String.class), any(String.class), any(LocalDate.class),
                    any(LocalTime.class));
            }

            @Test
            @DisplayName("diaryDate가 미래의 날짜라면 BAD_REQUEST 상태코드를 응답한다.")
            void diaryDate_is_After_now() throws Exception {
                // given
                // when
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("keyword", keyword);
                requestMap.put("notes", notes);
                requestMap.put("diaryDate", LocalDate.now().plusDays(1));
                String requestBody = objectMapper.writeValueAsString(requestMap);
                ResultActions result = mockMvc.perform(post(BASIC_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
                verify(createDiaryService, never()).createDiary(any(Long.class),
                    any(Long.class), any(String.class), any(String.class), any(LocalDate.class),
                    any(LocalTime.class));
            }
        }

        @Nested
        @DisplayName("정상 content값이 들어온다면")
        class If_test_param_is_normal {

            @Test
            @DisplayName("요청한 유저의 일기를 생성한다.")
            void create_diary() throws Exception {
                // given
                Long diaryId = 1L;
                given(createDiaryService.createDiary(
                    REQUEST_USER_ID, emotionId, keyword, notes, diaryDate, userTime))
                    .willReturn(new CreateDiaryResponse(diaryId));

                // when
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("emotionId", emotionId);
                requestMap.put("keyword", keyword);
                requestMap.put("notes", notes);
                requestMap.put("diaryDate", diaryDate);
                String requestBody = objectMapper.writeValueAsString(requestMap);

                ResultActions result = mockMvc.perform(post(BASIC_URL)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isCreated());
            }
        }
    }

    @Nested
    @DisplayName("updateDiary 메서드는")
    class UpdateDiaryTest {

        private final Long diaryId = 1L;

        @Test
        @DisplayName("notes 값이 정상적으로 들어오고, diaryId에 해당하는 일기가 존재한다면 일기를 수정한다.")
        void update_diary() throws Exception {
            // given
            String updatedNotes = "updatedNotes";

            // when
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("notes", updatedNotes);
            String requestBody = objectMapper.writeValueAsString(requestMap);
            ResultActions result = mockMvc.perform(put(BASIC_URL + "/" + diaryId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

            // then
            result.andExpect(status().isNoContent());
            verify(diaryService).updateDiaryNotes(diaryId, REQUEST_USER_ID, updatedNotes);
        }

        @Nested
        @DisplayName("content 값 중")
        class If_content {

            @Test
            @DisplayName("notes 값이 6010byte가 넘는다면 BAD_REQUEST 상태코드를 응답한다.")
            void notes_is_null_than_return_bad_request() throws Exception {
                // given
                // when
                String overNotes = "a".repeat(6011);
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("notes", overNotes);
                String requestBody = objectMapper.writeValueAsString(requestMap);
                ResultActions result = mockMvc.perform(put(BASIC_URL + "/" + diaryId)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

                // then
                result.andExpect(status().isBadRequest());
                verify(diaryService, never()).updateDiaryNotes(any(Long.class),
                    any(Long.class), any(String.class));
            }
        }
    }

    @Nested
    @DisplayName("deleteDiary 메서드는")
    class DeleteDiaryTest {

        @Test
        @DisplayName("diaryId에 해당하는 일기를 삭제한다.")
        void delete_diary() throws Exception {
            // given
            Long diaryId = 1L;

            // when
            ResultActions result = mockMvc.perform(delete(BASIC_URL + "/" + diaryId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            result.andExpect(status().isNoContent());
            verify(diaryService).deleteDiary(diaryId, REQUEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("getDrawLimit 메서드는")
    class GetDrawLimitTest {

        @Test
        @DisplayName("일기 생성 가능 여부, 마지막 일기 작성 시간, 유효 리워드 생성일자를 응답한다.")
        void return_draw_limit() throws Exception {
            // given
            LocalDateTime lastDiaryCreatedDate = LocalDateTime.now();
            LocalDateTime validRewardCreatedDate = LocalDateTime.now().minusDays(1);
            given(diaryService.getDrawLimit(REQUEST_USER_ID)).willReturn(
                GetDiaryLimitResponse.of(true, lastDiaryCreatedDate, validRewardCreatedDate));

            // when
            ResultActions result = mockMvc.perform(get(BASIC_URL + "/limit"));

            // then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.data.lastDiaryCreatedAt").value(
                    parseLocalDateTime(lastDiaryCreatedDate)))
                .andExpect(jsonPath("$.data.ticketCreatedAt").value(
                    parseLocalDateTime(validRewardCreatedDate)));
        }
    }

    @Nested
    @DisplayName("regenerateDiaryImage 메서드는")
    class RegenerateDiaryImageTest {

        @Test
        @DisplayName("diaryId에 해당하는 일기의 이미지를 재생성한다.")
        void regenerate_diary_image() throws Exception {
            // given
            Long diaryId = 1L;

            // when
            ResultActions result = mockMvc.perform(
                post(BASIC_URL + "/" + diaryId + "/regenerate")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            result.andExpect(status().isCreated());
            verify(createDiaryService).regenerateDiaryImage(REQUEST_USER_ID, diaryId);
        }
    }
}
