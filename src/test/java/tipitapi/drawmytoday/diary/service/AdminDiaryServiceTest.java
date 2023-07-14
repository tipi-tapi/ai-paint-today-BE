package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import tipitapi.drawmytoday.diary.dto.DiaryForMonitorQueryResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;

@ExtendWith(MockitoExtension.class)
class AdminDiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;
    @InjectMocks
    AdminDiaryService adminDiaryService;

    @Nested
    @DisplayName("getDiaries 메소드 테스트")
    class GetDiariesTest {

        @Test
        @DisplayName("페이지네이션이 적용된 일기 목록을 반환한다.")
        void it_returns_diaries_with_pagination() {
            // given
            List<DiaryForMonitorQueryResponse> diaries = new ArrayList<>();
            diaries.add(createDiaryForMonitorQueryResponse(1L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/1.png",
                "joyful , pink , canvas-textured, Oil Pastel, a crowded subway",
                "2023-06-16T15:00:00.000+00:00"));
            diaries.add(createDiaryForMonitorQueryResponse(2L,
                "https://drawmytoday.s3.ap-northeast-2.amazonaws.com/2021-08-16/2.png",
                "angry , purple , canvas-textured, Oil Pastel, school",
                "2023-06-17T15:00:00.000+00:00"));
            given(
                diaryRepository.getAllDiariesForMonitorAsPage(any(Pageable.class)))
                .willReturn(new PageImpl<>(diaries));

            // when
            Page<DiaryForMonitorQueryResponse> response = adminDiaryService.getDiaries(10, 0,
                Direction.ASC);

            // then
            assertThat(response.getContent().size()).isEqualTo(2);
            assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        }

        private DiaryForMonitorQueryResponse createDiaryForMonitorQueryResponse(Long id,
            String imageUrl, String prompt, String createdAt) {
            ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
            Map<String, Object> map = Map.of(
                "id", id,
                "imageUrl", imageUrl,
                "prompt", prompt,
                "createdAt", createdAt
            );
            return factory.createProjection(DiaryForMonitorQueryResponse.class, map);
        }
    }
}