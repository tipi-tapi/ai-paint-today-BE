package tipitapi.drawmytoday.diary.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.service.CreateDiaryService;
import tipitapi.drawmytoday.diary.service.DiaryService;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

@WebMvcTest(DiaryController.class)
public class DiaryControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/diary";

    @MockBean
    private DiaryService diaryService;
    @MockBean
    private CreateDiaryService createDiaryService;

    @Nested
    @DisplayName("getDiary 메서드는")
    class GetDiaryTest {

        @Nested
        @DisplayName("파라미터로 받은 diaryId가")
        class If_diaryId_is {

            @Test
            @DisplayName("존재하지 않는다면 404 NOT_FOUND 상태코드를 응답한다.")
            void not_exist_than_404_NOT_FOUND() throws Exception {
                // given
                long notExistDiaryId = 1L;
                given(diaryService.getDiary(USER_ID, notExistDiaryId)).willThrow(
                    new DiaryNotFoundException());

                // when
                ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + notExistDiaryId));

                // then
                result.andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("존재한다면 200 OK 상태코드를 응답한다.")
            void exist_than_200_OK() throws Exception {
                // given
                long diaryId = 1L;
                User user = getUser(SocialCode.GOOGLE);
                Emotion emotion = getEmotion();
                Diary diary = getDiary(user, emotion);
                GetDiaryResponse getDiaryResponse = GetDiaryResponse.of(diary, "imageUrl", emotion,
                    "promptText");
                given(diaryService.getDiary(USER_ID, diaryId)).willReturn(getDiaryResponse);

                // when
                ResultActions result = mockMvc.perform(get(BASIC_URL + "/" + diaryId));

                // then
                result.andExpect(status().isOk());
            }
        }
    }
}
