package tipitapi.drawmytoday.domain.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithIdAndCreatedAt;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestImage.createImage;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.time.LocalDate;
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
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.common.converter.Language;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryExistByDateResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryLimitResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetLastCreationResponse;
import tipitapi.drawmytoday.domain.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.domain.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.r2.service.R2PreSignedService;
import tipitapi.drawmytoday.domain.ticket.domain.Ticket;
import tipitapi.drawmytoday.domain.ticket.domain.TicketType;
import tipitapi.drawmytoday.domain.ticket.service.ValidateTicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;
    @Mock
    ImageService imageService;
    @Mock
    ValidateUserService validateUserService;
    @Mock
    R2PreSignedService r2PreSignedService;
    @Mock
    ValidateDiaryService validateDiaryService;
    @Mock
    Encryptor encryptor;
    @Mock
    PromptService promptService;
    @Mock
    ValidateTicketService validateTicketService;
    @Mock
    ValidateImageService validateImageService;
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
                List<Image> images = List.of(createImage(diary));
                Language language = Language.ko;

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user)).willReturn(diary);
                given(imageService.getLatestImages(diary)).willReturn(images);
                given(r2PreSignedService.getCustomDomainUrl(any(String.class)))
                    .willReturn("https://test.com");
                given(encryptor.decrypt(diary.getNotes())).willReturn("decrypted notes");
                given(promptService.getPromptByDiaryId(anyLong())).willReturn(Optional.empty());

                GetDiaryResponse getDiaryResponse = diaryService.getDiary(1L, 1L, language);

                assertThat(getDiaryResponse.getId()).isEqualTo(diary.getDiaryId());
            }

            @Test
            @DisplayName("language가 en일 경우 emotion에 emotioPrompt를 반환한다.")
            void if_lan_en_then_return_emotionPrompt() {
                User user = createUserWithId(1L);
                Emotion emotion = createEmotion();
                Diary diary = createDiaryWithId(1L, user, emotion);
                List<Image> images = List.of(createImage(diary));

                Language language = Language.en;

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user)).willReturn(diary);
                given(imageService.getLatestImages(diary)).willReturn(images);
                given(r2PreSignedService.getCustomDomainUrl(any(String.class)))
                    .willReturn("https://test.com");
                given(encryptor.decrypt(diary.getNotes())).willReturn("decrypted notes");
                given(promptService.getPromptByDiaryId(anyLong())).willReturn(Optional.empty());

                GetDiaryResponse getDiaryResponse = diaryService.getDiary(1L, 1L, language);

                assertThat(getDiaryResponse.getEmotion()).isEqualTo(emotion.getEmotionPrompt());
            }

            @Test
            @DisplayName("language가 ko일 경우 emotion에 name을 반환한다.")
            void if_lan_ko_then_return_name() {
                User user = createUserWithId(1L);
                Emotion emotion = createEmotion();
                Diary diary = createDiaryWithId(1L, user, emotion);
                List<Image> images = List.of(createImage(diary));

                Language language = Language.ko;

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user)).willReturn(diary);
                given(imageService.getLatestImages(diary)).willReturn(images);
                given(r2PreSignedService.getCustomDomainUrl(any(String.class)))
                    .willReturn("https://test.com");
                given(encryptor.decrypt(diary.getNotes())).willReturn("decrypted notes");
                given(promptService.getPromptByDiaryId(anyLong())).willReturn(Optional.empty());

                GetDiaryResponse getDiaryResponse = diaryService.getDiary(1L, 1L, language);

                assertThat(getDiaryResponse.getEmotion()).isEqualTo(emotion.getName());
            }
        }

        @Nested
        @DisplayName("주어진 일기가 없거나 삭제되었을 경우")
        class if_diary_not_exists_or_deleted {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                User user = createUser();
                Language language = Language.ko;
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user)).willThrow(
                    DiaryNotFoundException.class);

                assertThatThrownBy(() -> diaryService.getDiary(1L, 1L, language))
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
                Language language = Language.ko;
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateDiaryService.validateDiaryById(1L, user))
                    .willThrow(NotOwnerOfDiaryException.class);

                assertThatThrownBy(() -> diaryService.getDiary(1L, 1L, language))
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
    @DisplayName("getDiaryExistByDate 메소드 테스트")
    class GetDiaryExistByDateTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, 6, 1))
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

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, value, 1))
                    .isInstanceOf(BusinessException.class);
            }

            @ParameterizedTest
            @DisplayName("12보다 클 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {13, 14, 999})
            void more_12_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, value, 1))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("day 값이")
        class if_day_value {

            @ParameterizedTest
            @DisplayName("1보다 작을 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {0, -1})
            void less_1_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, 6, value))
                    .isInstanceOf(BusinessException.class);
            }

            @ParameterizedTest
            @DisplayName("31 보다 클 경우 BusinessException 예외를 발생시킨다.")
            @ValueSource(ints = {32, 100})
            void more_12_then_throws_BusinessException(int value) {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, 6, value))
                    .isInstanceOf(BusinessException.class);
            }

            @Test
            @DisplayName("30일이 최대인 달에 31일이 입력될 경우 BusinessException 예외를 발생시킨다.")
            void exceeds_maximum_day_30_then_throws_BusinessException() {
                User user = createUser();
                given(validateUserService.validateUserById(1L)).willReturn(user);

                assertThatThrownBy(() -> diaryService.getDiaryExistByDate(1L, 2023, 6, 31))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("해당 날짜의 일기가 존재하지 않는 경우")
        class if_diary_not_exists {

            @Test
            @DisplayName("false를 반환한다.")
            void it_returns_false() {
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(diaryRepository.getDiaryExistsByDiaryDate(anyLong(), any(LocalDate.class)))
                    .willReturn(Optional.empty());

                GetDiaryExistByDateResponse response = diaryService.getDiaryExistByDate(
                    1L, 2023, 6, 1);

                assertThat(response.isExist()).isFalse();
            }
        }

        @Nested
        @DisplayName("해당 날짜의 일기가 존재하는 경우")
        class if_diary_exists {

            @Test
            @DisplayName("true를 반환한다.")
            void it_returns_true() {
                User user = createUserWithId(1L);
                given(validateUserService.validateUserById(1L)).willReturn(user);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                given(diaryRepository.getDiaryExistsByDiaryDate(anyLong(), any(LocalDate.class)))
                    .willReturn(Optional.of(diary));

                GetDiaryExistByDateResponse response = diaryService.getDiaryExistByDate(
                    1L, 2023, 6, 1);

                assertThat(response.isExist()).isTrue();
                assertThat(response.getDiaryId()).isEqualTo(1L);
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
                    given(encryptor.encrypt(null)).willReturn(null);

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
                    given(encryptor.encrypt(anyString())).willReturn("encrypted notes");

                    diaryService.updateDiaryNotes(1L, 1L, "notes");

                    assertThat(diary.getNotes()).isEqualTo("encrypted notes");
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

    @Nested
    @DisplayName("getDrawLimit 메소드 테스트")
    class GetDrawLimitTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class if_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void it_throws_UserNotFoundException() {
                given(validateUserService.validateUserById(1L)).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> diaryService.getDrawLimit(1L))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("유효한 티켓이 있을 경우")
        class if_valid_ticket_exists {

            @Test
            @DisplayName("일기 생성 가능한 내용의 GetDrawLimitResponse 객체를 반환한다.")
            void it_returns_available() {
                User user = createUser();
                LocalDateTime lastDiaryDate = LocalDateTime.now().minusMinutes(10);
                user.setLastDiaryDate(lastDiaryDate);

                Ticket ticket = Ticket.of(user, TicketType.AD_REWARD);
                LocalDateTime ticketCreatedAt = LocalDateTime.now().minusMinutes(30);
                ReflectionTestUtils.setField(ticket, "createdAt", ticketCreatedAt);

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateTicketService.findValidTicket(1L)).willReturn(
                    Optional.of(ticket));

                GetDiaryLimitResponse response = diaryService.getDrawLimit(1L);

                assertThat(response.isAvailable()).isTrue();
                assertThat(response.getLastDiaryCreatedAt()).isEqualTo(lastDiaryDate);
                assertThat(response.getTicketCreatedAt()).isEqualTo(ticketCreatedAt);
            }
        }

        @Nested
        @DisplayName("유효한 티켓이 없을 경우")
        class if_valid_ticket_not_exists {

            @Test
            @DisplayName("일기 생성 불가한 내용의 GetDrawLimitResponse 객체를 반환한다.")
            void it_returns_unavailable() {
                User user = createUserWithId(1L);
                LocalDateTime lastDiaryDate = LocalDateTime.now();
                ReflectionTestUtils.setField(user, "lastDiaryDate", lastDiaryDate);

                given(validateUserService.validateUserById(1L)).willReturn(user);
                given(validateTicketService.findValidTicket(1L)).willReturn(
                    Optional.empty());

                GetDiaryLimitResponse response = diaryService.getDrawLimit(1L);

                assertThat(response.isAvailable()).isFalse();
                assertThat(response.getLastDiaryCreatedAt()).isEqualTo(lastDiaryDate);
                assertThat(response.getTicketCreatedAt()).isNull();
            }
        }
    }
}