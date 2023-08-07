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
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        private final LocalDate DIARY_DATE = LocalDate.now();

        private final ZoneId TIMEZONE = ZoneId.of("UTC");

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
                LocalDateTime lastDateTime = DIARY_DATE.minusDays(1L).atTime(1, 1);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                String prompt = "test prompt";

                given(validateUserService.validateUserWithDrawLimit(USER_ID, TIMEZONE)).willReturn(
                    user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(promptTextService.createPromptText(emotion, KEYWORD)).willReturn(prompt);
                given(dallEService.getImageAsUrl(eq(prompt))).willThrow(exceptionClass);

                //when
                //then
                assertThatThrownBy(
                    () -> createDiaryService.createDiary(USER_ID, EMOTION_ID, KEYWORD, NOTES,
                        DIARY_DATE, TIMEZONE)).isInstanceOf(exceptionClass);
                assertThat(user.getLastDiaryDate().isEqual(lastDateTime)).isTrue();

                verify(promptService).createPrompt(eq(prompt), eq(false));
                verify(promptService, never()).createPrompt(any(Diary.class), eq(prompt), eq(true));
            }

            @Test
            @DisplayName("정상일 경우 일기를 생성한다.")
            void success_then_create_diary() throws Exception {
                //given
                Long diaryId = 1L;
                LocalDateTime lastDateTime = DIARY_DATE.minusDays(1L).atTime(1, 1);
                String prompt = "test prompt";
                byte[] image = new byte[1];

                User user = TestUser.createUserWithId(USER_ID);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);

                given(validateUserService.validateUserWithDrawLimit(USER_ID, TIMEZONE)).willReturn(
                    user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(promptTextService.createPromptText(emotion, KEYWORD)).willReturn(prompt);
                given(dallEService.getImageAsUrl(prompt)).willReturn(image);
                given(encryptor.encrypt(NOTES)).willReturn("암호화된 노트");
                given(diaryRepository.save(any(Diary.class))).willReturn(diary);

                //when
                CreateDiaryResponse createDiaryResponse = createDiaryService.createDiary(
                    USER_ID, EMOTION_ID, KEYWORD, NOTES, DIARY_DATE, TIMEZONE);

                //then
                assertThat(createDiaryResponse.getId()).isEqualTo(diaryId);
                assertThat(user.getLastDiaryDate().isAfter(lastDateTime)).isTrue();

                verify(dallEService).getImageAsUrl(any(String.class));
                verify(promptService).createPrompt(eq(diary), eq(prompt), eq(true));
                verify(imageService).uploadAndCreateImage(eq(diary), any(byte[].class), eq(true));
            }
        }
    }

    @Nested
    @DisplayName("createTestDiary 메서드는")
    class Create_test_diary_test {

        @Test
        @DisplayName("그림을 생성하지 않는다.")
        void not_draw_image() throws Exception {
            //given
            Long userId = 1L;
            LocalDate diaryDate = LocalDate.now();
            Long emotionId = 1L;
            Long diaryId = 1L;
            String prompt = "test prompt";
            String notes = "노트";
            String keyword = "키워드";
            LocalDateTime lastDateTime = diaryDate.minusDays(1L).atTime(1, 1);
            ZoneId timezone = ZoneId.of("UTC");

            User user = TestUser.createUserWithId(userId);
            user.setLastDiaryDate(lastDateTime);
            Emotion emotion = TestEmotion.createEmotionWithId(emotionId);
            Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);

            given(validateUserService.validateUserWithDrawLimit(userId, timezone)).willReturn(user);
            given(validateEmotionService.validateEmotionById(emotionId)).willReturn(emotion);
            given(encryptor.encrypt(notes)).willReturn("암호화된 노트");
            given(diaryRepository.save(any(Diary.class))).willReturn(diary);
            given(promptTextService.createPromptText(emotion, keyword)).willReturn(prompt);

            //when
            CreateDiaryResponse response = createDiaryService.createTestDiary(
                userId, emotionId, keyword, notes, diaryDate, timezone);

            //then
            assertThat(response.getId()).isEqualTo(diaryId);
            assertThat(user.getLastDiaryDate().isAfter(lastDateTime)).isTrue();

            verify(dallEService, never()).getImageAsUrl(any(String.class));
            verify(promptService).createPrompt(eq(diary), eq(prompt), eq(true));
            verify(imageService).createImage(eq(diary), any(String.class), eq(true));
        }
    }

}