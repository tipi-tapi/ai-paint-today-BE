package tipitapi.drawmytoday.domain.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestUser.createAdminUserWithId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.diary.service.AdminDiaryService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.exception.UserAccessDeniedException;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    ValidateUserService validateUserService;
    @Mock
    AdminDiaryService adminDiaryService;
    @InjectMocks
    AdminService adminService;

    @Nested
    @DisplayName("getDiaries 메소드 테스트")
    class GetDiariesTest {

        @Nested
        @DisplayName("관리자가 아닌 유저가 호출하면")
        class if_user_is_not_admin {

            @Test
            @DisplayName("UserAccessDeniedException 예외가 발생한다.")
            void it_throws_UserAccessDeniedException() {
                // given
                given(validateUserService.validateAdminUserById(anyLong()))
                    .willThrow(UserAccessDeniedException.class);

                // when
                // then
                assertThatThrownBy(
                    () -> adminService.getDiaries(1L, 10, 0, Direction.ASC, 1L, true))
                    .isInstanceOf(UserAccessDeniedException.class);
            }
        }

        @Nested
        @DisplayName("관리자인 유저가 호출하면")
        class is_user_is_admin {

            @Test
            @DisplayName("페이지네이션이 적용된 일기 목록을 반환한다.")
            void it_returns_diaries_with_pagination() {
                // given
                User user = createAdminUserWithId(1L);
                given(validateUserService.validateAdminUserById(anyLong())).willReturn(user);

                List<GetDiaryAdminResponse> diaries = new ArrayList<>();
                diaries.add(new GetDiaryAdminResponse(1L,
                    "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/1.png",
                    "joyful , pink , canvas-textured, Oil Pastel, a crowded subway",
                    LocalDateTime.of(2023, 6, 16, 15, 0, 0), LocalDateTime.now(), "4", false));
                diaries.add(new GetDiaryAdminResponse(2L,
                    "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/2.png",
                    "angry , purple , canvas-textured, Oil Pastel, school",
                    LocalDateTime.of(2023, 6, 17, 15, 0, 0), LocalDateTime.now(), "3", false));
                given(adminDiaryService.getDiaries(any(Integer.class), any(Integer.class),
                    any(Direction.class), anyLong(), anyBoolean())).willReturn(
                    new PageImpl<>(diaries));

                // when
                Page<GetDiaryAdminResponse> response = adminService.getDiaries(1L, 10, 0,
                    Direction.ASC, 1L, true);

                // then
                assertThat(response.getContent().size()).isEqualTo(2);
                assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
            }
        }
    }
}