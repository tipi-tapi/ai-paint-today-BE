package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestImage.createImage;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.GetDiariesResponse;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;
    @Mock
    ImageService imageService;
    @Mock
    ValidateUserService validateUserService;
    @InjectMocks
    DiaryService diaryService;

    @Nested
    @DisplayName("getDiary 메소드 테스트")
    class GetDairyTest {

        @Nested
        @DisplayName("유저 소유의 주어진 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기를 반환한다.")
            void it_returns_diary() {
                User user = createUserWithId(1L);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                Image image = createImage(diary);

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findById(1L)).willReturn(
                    Optional.of(diary));
                given(imageService.getImage(diary)).willReturn(image);

                GetDiaryResponse getDiaryResponse = diaryService.getDiary(1L, 1L);

                assertThat(getDiaryResponse.getId()).isEqualTo(diary.getDiaryId());
            }
        }

        @Nested
        @DisplayName("주어진 일기가 없을 경우")
        class if_diary_not_exists_or_not_user_owned_diary {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                given(diaryRepository.findById(1L)).willReturn(Optional.empty());
                given(validateUserService.validateUserById(1L)).willReturn(createUser());

                assertThatThrownBy(() -> diaryService.getDiary(1L, 1L))
                    .isInstanceOf(DiaryNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("주어진 유저 소유의 일기가 아닐 경우")
        class if_diary_not_user_owned {

            @Test
            @DisplayName("NotOwnerOfDiaryException 예외를 발생시킨다.")
            void it_throws_NotOwnerOfDiaryException() {
                createUserWithId(1L);
                User otherUser = createUserWithId(2L);
                Diary diary = createDiaryWithId(1L, otherUser, createEmotion());

                given(diaryRepository.findById(1L)).willReturn(Optional.of(diary));

                assertThatThrownBy(() -> diaryService.getDiary(1L, 1L))
                    .isInstanceOf(NotOwnerOfDiaryException.class);
            }
        }
    }

    @Nested
    @DisplayName("getDiaries 메소드 테스트")
    class GetDiariesTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.getDiaries(1L, 2023, 6))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("month 값이")
        class if_month_value {

            @ParameterizedTest
            @DisplayName("1보다 작을 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {0, -1})
            void less_1_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaries(1L, 2023, value))
                    .isInstanceOf(BusinessException.class);
            }

            @ParameterizedTest
            @DisplayName("12보다 클 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {13, 14, 999})
            void more_12_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaries(1L, 2023, value))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("주어진 유저의 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기들을 반환한다.")
            void it_returns_diaries() {
                User user = createUserWithId(1L);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                    any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(List.of(diary));

                List<GetDiariesResponse> getDiaryResponses = diaryService.getDiaries(1L, 2023, 6);

                assertThat(getDiaryResponses).hasSize(1);
                assertThat(getDiaryResponses.get(0).getId()).isEqualTo(diary.getDiaryId());
            }
        }
    }
}