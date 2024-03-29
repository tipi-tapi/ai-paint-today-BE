package tipitapi.drawmytoday.domain.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.r2.service.R2PreSignedService;

@ExtendWith(MockitoExtension.class)
class AdminDiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;
    @Mock
    R2PreSignedService r2PreSignedService;
    @InjectMocks
    AdminDiaryService adminDiaryService;

    @Nested
    @DisplayName("getDiaries 메소드 테스트")
    class GetDiariesTest {

        @Test
        @DisplayName("페이지네이션이 적용된 일기 목록을 반환한다.")
        void it_returns_diaries_with_pagination() {
            // given
            List<GetDiaryAdminResponse> diaries = new ArrayList<>();
            diaries.add(new GetDiaryAdminResponse(1L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/1.png",
                "joyful , pink , canvas-textured, Oil Pastel, a crowded subway",
                LocalDateTime.of(2023, 6, 16, 15, 0, 0), LocalDateTime.now(), "4", false));
            diaries.add(new GetDiaryAdminResponse(2L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/2.png",
                "angry , purple , canvas-textured, Oil Pastel, school",
                LocalDateTime.of(2023, 6, 17, 15, 0, 0), LocalDateTime.now(), null, false));
            given(
                diaryRepository.getDiariesForMonitorAsPage(any(Pageable.class),
                    any(Direction.class), eq(1L), anyBoolean()))
                .willReturn(new PageImpl<>(diaries));
            given(r2PreSignedService.getCustomDomainUrl(any(String.class)))
                .willReturn("https://test.com");

            // when
            Page<GetDiaryAdminResponse> response = adminDiaryService.getDiaries(10, 0,
                Direction.ASC, 1L, true);

            // then
            assertThat(response.getContent().size()).isEqualTo(2);
            assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        }
    }
}