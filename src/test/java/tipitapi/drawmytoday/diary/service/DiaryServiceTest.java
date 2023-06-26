package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithIdAndCreatedAt;
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
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
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
    @Mock
    ValidateDiaryService validateDiaryService;
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
                given(validateDiaryService.validateDiaryById(1L, user)).willReturn(diary);
                given(imageService.getImage(diary)).willReturn(image);

                GetDiaryResponse getDiaryResponse = diaryService.getDiary(1L, 1L);

                assertThat(getDiaryResponse.getId()).isEqualTo(diary.getDiaryId());
            }
        }

        @Nested
        @DisplayName("주어진 일기가 없거나 삭제되었을 경우")
        class if_diary_not_exists_or_deleted {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user)).willThrow(
                    DiaryNotFoundException.class);

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
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user))
                    .willThrow(NotOwnerOfDiaryException.class);

                assertThatThrownBy(() -> diaryService.getDiary(1L, 1L))
                    .isInstanceOf(NotOwnerOfDiaryException.class);
            }
        }
    }

    @Nested
    @DisplayName("getMonthlyDiaries 메소드 테스트")
    class GetMonthlyDiariesTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.getMonthlyDiaries(1L, 2023, 6))
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

                assertThatThrownBy(() -> diaryService.getMonthlyDiaries(1L, 2023, value))
                    .isInstanceOf(BusinessException.class);
            }

            @ParameterizedTest
            @DisplayName("12보다 클 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {13, 14, 999})
            void more_12_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getMonthlyDiaries(1L, 2023, value))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("주어진 유저의 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기에 해당하는 이미지가 없을 경우")
            void it_returns_diaries_without_image() {
                User user = createUserWithId(1L);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                    any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(List.of(diary));

                assertThatThrownBy(() -> diaryService.getMonthlyDiaries(1L, 2023, 6))
                    .isInstanceOf(ImageNotFoundException.class);
            }

            @Test
            @DisplayName("일기들을 반환한다.")
            void it_returns_diaries() {
                User user = createUserWithId(1L);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                createImage(diary);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                    any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .willReturn(List.of(diary));

                List<GetMonthlyDiariesResponse> getDiaryResponses = diaryService.getMonthlyDiaries(
                    1L,
                    2023, 6);

                assertThat(getDiaryResponses).hasSize(1);
                assertThat(getDiaryResponses.get(0).getId()).isEqualTo(diary.getDiaryId());
            }
        }
    }

    @Nested
    @DisplayName("getLastCreation 메소드 테스트")
    class GetLastCreationTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.getLastCreation(1L))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("주어진 유저의 일기가 존재하지 않을 경우")
        class if_user_doesnt_have_diary {

            @Test
            @DisplayName("lastCreation에 null을 반환한다.")
            void it_returns_null() {
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(any(Long.class)))
                    .willReturn(Optional.empty());

                GetLastCreationResponse response = diaryService.getLastCreation(1L);

                assertThat(response.getLastCreation()).isNull();
            }
        }

        @Nested
        @DisplayName("주어진 유저의 일기가 존재할 경우")
        class if_user_has_diary {

            @Test
            @DisplayName("마지막으로 일기를 생성한 날짜를 반환한다.")
            void it_returns_last_creation() {
                User user = createUserWithId(1L);
                LocalDateTime lastCreation = LocalDateTime.now().minusDays(2);
                Diary diary = createDiaryWithIdAndCreatedAt(1L, lastCreation, user,
                    createEmotion());

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(any(Long.class)))
                    .willReturn(Optional.of(diary));

                GetLastCreationResponse response = diaryService.getLastCreation(1L);

                assertThat(response.getLastCreation()).isEqualTo(lastCreation);
            }
        }
    }

    @Nested
    @DisplayName("updateDiaryNotes 메소드 테스트")
    class UpdateDiaryNotesTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.updateDiaryNotes(1L, 1L, "notes"))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("diaryId에 해당하는 일기가 존재하지 않을 경우")
        class if_diary_not_exists {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user))
                    .willThrow(DiaryNotFoundException.class);

                assertThatThrownBy(() -> diaryService.updateDiaryNotes(1L, 1L, "notes"))
                    .isInstanceOf(DiaryNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("일기가 존재할 경우")
        class if_diary_exists {

            @Nested
            @DisplayName("요청한 내용이 null일 경우")
            class if_new_notes_is_null {

                @Test
                @DisplayName("일기의 내용을 제거해 null로 만든다.")
                void it_removes_diary_notes() {
                    User user = createUserWithId(1L);
                    Diary diary = createDiaryWithId(1L, user, createEmotion());
                    given(validateUserService.validateUserById(1L)).willReturn(user);
                    given(validateDiaryService.validateDiaryById(1L, user))
                        .willReturn(diary);

                    diaryService.updateDiaryNotes(1L, 1L, null);

                    assertThat(diary.getNotes()).isNull();
                }
            }

            @Nested
            @DisplayName("요청한 내용이 null이 아닐 경우")
            class if_new_notes_is_not_null {

                @Test
                @DisplayName("일기의 내용을 주어진 내용으로 수정한다.")
                void it_updates_diary_notes() {
                    User user = createUserWithId(1L);
                    Diary diary = createDiaryWithId(1L, user, createEmotion());
                    given(validateUserService.validateUserById(1L)).willReturn(user);
                    given(validateDiaryService.validateDiaryById(1L, user))
                        .willReturn(diary);

                    diaryService.updateDiaryNotes(1L, 1L, "notes");

                    assertThat(diary.getNotes()).isEqualTo("notes");
                }
            }

        }

    }

    @Nested
    @DisplayName("deleteDiary 메소드 테스트")
    class DeleteDiaryTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.deleteDiary(1L, 1L))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("diaryId에 해당하는 일기가 존재하지 않을 경우")
        class if_diary_not_exists {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user))
                    .willThrow(DiaryNotFoundException.class);

                assertThatThrownBy(() -> diaryService.deleteDiary(1L, 1L))
                    .isInstanceOf(DiaryNotFoundException.class);
            }
        }
    }
}