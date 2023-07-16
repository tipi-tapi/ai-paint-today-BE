package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.dalle.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.dalle.service.DallEService;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.s3.service.S3Service;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class CreateDiaryServiceTest {

    @InjectMocks
    private CreateDiaryService createDiaryService;
    @Mock
    private DiaryRepository diaryRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private ValidateUserService validateUserService;
    @Mock
    private ValidateEmotionService validateEmotionService;
    @Mock
    private S3Service s3Service;
    @Mock
    private DallEService dallEService;
    @Mock
    private PromptService promptService;
    @Mock
    private PromptTextService promptTextService;
    @Mock
    private Encryptor encryptor;

    @Nested
    @DisplayName("createDiary 메서드는")
    class Create_diary_test {

        private final Long USER_ID = 1L;
        private final Long EMOTION_ID = 1L;
        private final String KEYWORD = "키워드";
        private final String NOTES = "노트";
        private final LocalDate CREATE_DIARY_DATE = LocalDate.now();
        private final boolean TEST = false;

        @Nested
        @DisplayName("파라미터로 받은 createDiaryDate가")
        class Create_diary_date_param_is {

            @Test
            @DisplayName("오늘 이후의 날짜일 경우 BusinessException을 던진다")
            void is_after_today_date_then_throw_exception() throws Exception {
                //given
                LocalDate afterCreateDiaryDate = CREATE_DIARY_DATE.plusDays(1L);

                //when
                //then
                assertThatThrownBy(() -> createDiaryService.createDiary(
                    USER_ID, EMOTION_ID, KEYWORD, NOTES, afterCreateDiaryDate, TEST))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("파라미터로 받은 test가")
        class Test_param_is {

            @Test
            @DisplayName("true일 경우 그림을 생성하지 않는다.")
            void true_then_not_draw_image() throws Exception {
                //given
                boolean test = true;
                Long diaryId = 1L;
                User user = TestUser.createUserWithId(USER_ID);
                LocalDateTime lastDateTime = CREATE_DIARY_DATE.minusDays(1L).atTime(1, 1);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                String prompt = "test prompt";
                String encryptedNotes = "encrypted notes";
                Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);
                given(validateUserService.validateUserById(USER_ID)).willReturn(user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(promptTextService.createPromptText(emotion, KEYWORD)).willReturn(prompt);
                given(encryptor.encrypt(NOTES)).willReturn(encryptedNotes);
                given(diaryRepository.save(any(Diary.class))).willReturn(diary);

                //when
                CreateDiaryResponse createDiaryResponse = createDiaryService.createDiary(
                    USER_ID, EMOTION_ID, KEYWORD, NOTES, CREATE_DIARY_DATE, test);

                //then
                assertThat(createDiaryResponse.getId()).isEqualTo(diaryId);
                assertThat(user.getLastDiaryDate().isAfter(lastDateTime)).isTrue();
                verify(dallEService, never()).getImageAsUrl(any(String.class));
                verify(s3Service, never()).uploadImage(any(byte[].class), any(String.class));
                verify(promptService).createPrompt(eq(diary), eq(prompt), eq(true));
                verify(imageService).createImage(eq(diary), any(String.class), eq(true));
            }

            @Test
            @DisplayName("false일 경우 그림을 생성한다.")
            void false_then_draw_image() throws Exception {
                //given
                boolean test = false;
                Long diaryId = 1L;
                User user = TestUser.createUserWithId(USER_ID);
                LocalDateTime lastDateTime = CREATE_DIARY_DATE.minusDays(1L).atTime(1, 1);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                String prompt = "test prompt";
                String encryptedNotes = "encrypted notes";
                byte[] image = new byte[1];
                Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);
                given(validateUserService.validateUserById(USER_ID)).willReturn(user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(promptTextService.createPromptText(emotion, KEYWORD)).willReturn(prompt);
                given(encryptor.encrypt(NOTES)).willReturn(encryptedNotes);
                given(dallEService.getImageAsUrl(prompt)).willReturn(image);
                given(diaryRepository.save(any(Diary.class))).willReturn(diary);

                //when
                CreateDiaryResponse createDiaryResponse = createDiaryService.createDiary(
                    USER_ID, EMOTION_ID, KEYWORD, NOTES, CREATE_DIARY_DATE, test);

                //then
                assertThat(createDiaryResponse.getId()).isEqualTo(diaryId);
                assertThat(user.getLastDiaryDate().isAfter(lastDateTime)).isTrue();
                verify(dallEService).getImageAsUrl(any(String.class));
                verify(s3Service).uploadImage(eq(image), any(String.class));
                verify(promptService).createPrompt(eq(diary), eq(prompt), eq(true));
                verify(imageService).createImage(eq(diary), any(String.class), eq(true));
            }
        }

        @Nested
        @DisplayName("dallE 요청 시")
        class DallE_request {

            @ParameterizedTest
            @ValueSource(classes = {DallERequestFailException.class,
                ImageInputStreamFailException.class})
            @DisplayName("에러가 발생하면 일기를 생성하지 않고, 실패 프롬프트를 생성한다.")
            void exception_throw_then_create_fail_prompt(Class<? extends Throwable> exceptionClass)
                throws Exception {
                //given
                User user = TestUser.createUserWithId(USER_ID);
                LocalDateTime lastDateTime = CREATE_DIARY_DATE.minusDays(1L).atTime(1, 1);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                String prompt = "test prompt";
                given(validateUserService.validateUserById(USER_ID)).willReturn(user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(promptTextService.createPromptText(emotion, KEYWORD)).willReturn(prompt);
                given(dallEService.getImageAsUrl(eq(prompt))).willThrow(exceptionClass);

                //when
                //then
                assertThatThrownBy(() -> createDiaryService.createDiary(
                    USER_ID, EMOTION_ID, KEYWORD, NOTES, CREATE_DIARY_DATE, TEST))
                    .isInstanceOf(exceptionClass);
                assertThat(user.getLastDiaryDate().isEqual(lastDateTime)).isTrue();
                verify(promptService).createPrompt(eq(prompt), eq(false));
                verify(promptService, never()).createPrompt(any(Diary.class), eq(prompt), eq(true));
            }
        }
    }
}