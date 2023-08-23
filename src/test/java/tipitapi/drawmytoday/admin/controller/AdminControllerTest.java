package tipitapi.drawmytoday.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.domain.admin.controller.AdminController;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.service.AdminService;

@WebMvcTest(AdminController.class)
@WithCustomUser
class AdminControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/admin";

    @MockBean
    private AdminService adminService;

    @Nested
    @DisplayName("getDiaries 메서드는")
    class GetDiariesTest {

        @Test
        @DisplayName("모니터링을 위한 일기 데이터 목록을 Pagination 형태로 응답한다.")
        void return_diaries_as_pagination() throws Exception {
            // given
            int size = 10;
            int page = 0;
            Direction direction = Direction.ASC;
            Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, "created_at", "diary_id"));
            List<GetDiaryAdminResponse> diaries = new ArrayList<>();
            diaries.add(new GetDiaryAdminResponse(1L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/1.png",
                "happy , pink , canvas-textured, Oil Pastel, a crowded subway",
                LocalDateTime.now().minusDays(5)));
            diaries.add(new GetDiaryAdminResponse(2L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/2.png",
                "angry , blue , glass-textured, crayon, school",
                LocalDateTime.now().minusDays(1)));
            given(adminService.getDiaries(anyLong(), anyInt(), anyInt(), any(Direction.class),
                anyLong()))
                .willReturn(new PageImpl<>(diaries, pageable, 2));

            // when
            ResultActions result = mockMvc.perform(get(BASIC_URL + "/diaries")
                .queryParam("size", String.valueOf(size))
                .queryParam("page", String.valueOf(page))
                .queryParam("direction", "ASC")
                .queryParam("emotion", "1"));

            // then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(diaries.get(0).getId()))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.pageable").exists())
                .andExpect(jsonPath("$.data.size").value(size))
                .andExpect(jsonPath("$.data.number").value(page));
        }
    }
}
