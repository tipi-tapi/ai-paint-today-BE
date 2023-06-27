package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.user.domain.User;

@ExtendWith(MockitoExtension.class)
class ValidateDiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;

    @InjectMocks
    ValidateDiaryService validateDiaryService;

    @Nested
    @DisplayName("validateDiaryById 메소드 테스트")
    class ValidateDiaryByIdTest {

        @Nested
        @DisplayName("diaryId에 해당하는 일기가 존재하지 않을 경우")
        class if_diary_not_exists {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                given(diaryRepository.findById(any(Long.class)))
                    .willReturn(Optional.empty());

                assertThatThrownBy(() -> validateDiaryService.validateDiaryById(1L, createUser()))
                    .isInstanceOf(DiaryNotFoundException.class);
            }

        }

        @Nested
        @DisplayName("user 소유의 일기가 아닌 경우")
        class if_not_owner_of_diary {

            @Test
            @DisplayName("NotOwnerOfDiaryException 예외를 발생시킨다.")
            void it_throws_NotOwnerOfDiaryException() {
                User user = createUserWithId(1L);
                Diary diary = TestDiary.createDiaryWithId(
                    1L, createUserWithId(2L), TestEmotion.createEmotion());
                given(diaryRepository.findById(any(Long.class)))
                    .willReturn(Optional.of(diary));

                assertThatThrownBy(() -> validateDiaryService.validateDiaryById(1L, user))
                    .isInstanceOf(NotOwnerOfDiaryException.class);
            }
        }

        @Nested
        @DisplayName("일기가 삭제된 경우")
        class if_diary_deleted {

            @Test
            @DisplayName("DiaryNotFoundException 예외를 발생시킨다.")
            void it_throws_DiaryNotFoundException() {
                given(diaryRepository.findById(any(Long.class)))
                    .willReturn(Optional.empty());

                assertThatThrownBy(() -> validateDiaryService.validateDiaryById(1L, createUser()))
                    .isInstanceOf(DiaryNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("일기가 존재할 경우")
        class if_diary_exists {

            @Test
            @DisplayName("일기를 삭제한다.")
            void return_diary() {
                User user = createUserWithId(1L);
                Diary diary = createDiaryWithId(1L, user, createEmotion());
                given(diaryRepository.findById(any(Long.class)))
                    .willReturn(Optional.of(diary));

                Diary validatedDiary = validateDiaryService.validateDiaryById(1L, user);

                assertThat(validatedDiary).isEqualTo(diary);
            }
        }
    }
}