package tipitapi.drawmytoday.domain.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import tipitapi.drawmytoday.common.testdata.TestPrompt;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.common.utils.Encryptor;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.diary.exception.PromptNotExistException;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.service.ValidateEmotionService;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.generator.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;
import tipitapi.drawmytoday.domain.ticket.service.ValidateTicketService;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

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
    private ValidateDiaryService validateDiaryService;
    @Mock
    private ValidateTicketService validateTicketService;
    @Mock
    private ImageGeneratorService imageGeneratorService;
    @Mock
    private PromptService promptService;
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
        private final LocalTime USER_TIME = LocalTime.now();

        @Nested
        @DisplayName("dallE imageGenerator로 요청 시")
        class DallE_request {

            @ParameterizedTest
            @ValueSource(classes = {DallERequestFailException.class,
                ImageInputStreamFailException.class})
            @DisplayName("에러가 발생하면 그대로 던진다.")
            void throw_exception(Class<? extends Throwable> exceptionClass) throws Exception {
                //given
                User user = TestUser.createUserWithId(USER_ID);
                LocalDateTime lastDateTime = DIARY_DATE.minusDays(1L).atTime(1, 1);
                user.setLastDiaryDate(lastDateTime);
                Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
                String prompt = "test prompt";

                given(validateUserService.validateUserById(USER_ID)).willReturn(user);
                given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
                given(imageGeneratorService.generateImage(eq(emotion), eq(KEYWORD))).willThrow(
                    exceptionClass);

                //when
                //then
                assertThatThrownBy(
                    () -> createDiaryService.createDiary(USER_ID, EMOTION_ID, KEYWORD, NOTES,
                        DIARY_DATE, USER_TIME)).isInstanceOf(exceptionClass);
                assertThat(user.getLastDiaryDate().isEqual(lastDateTime)).isTrue();

                verify(promptService, never()).createPrompt(any(Diary.class), anyString(),
                    eq(true));
            }
        }

        @Test
        @DisplayName("일기를 생성한다.")
        void create_diary() throws Exception {
            //given
            Long diaryId = 1L;
            LocalDateTime lastDateTime = DIARY_DATE.minusDays(1L).atTime(1, 1);
            String prompt = "test prompt";
            byte[] image = new byte[1];

            User user = TestUser.createUserWithId(USER_ID);
            user.setLastDiaryDate(lastDateTime);
            Emotion emotion = TestEmotion.createEmotionWithId(EMOTION_ID);
            Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);

            given(validateUserService.validateUserById(USER_ID)).willReturn(user);
            given(validateEmotionService.validateEmotionById(EMOTION_ID)).willReturn(emotion);
            given(imageGeneratorService.generateImage(emotion, KEYWORD)).willReturn(
                new GeneratedImageAndPrompt(prompt, image));
            given(encryptor.encrypt(NOTES)).willReturn("암호화된 노트");
            given(diaryRepository.save(any(Diary.class))).willReturn(diary);

            //when
            CreateDiaryResponse createDiaryResponse = createDiaryService.createDiary(
                USER_ID, EMOTION_ID, KEYWORD, NOTES, DIARY_DATE, USER_TIME);

            //then
            assertThat(createDiaryResponse.getId()).isEqualTo(diaryId);
            assertThat(user.getLastDiaryDate().isAfter(lastDateTime)).isTrue();

            verify(imageGeneratorService).generateImage(eq(emotion), eq(KEYWORD));
            verify(promptService).createPrompt(eq(diary), eq(prompt), eq(true));
            verify(imageService).uploadAndCreateImage(eq(diary), any(byte[].class), eq(true));
        }
    }

    @Nested
    @DisplayName("createTestDiary 메서드는")
    class Create_test_diary_test {

        @DisplayName("samples 수에 따라 테스트 일기를 생성한다.")
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void create_test_diary(int samples) throws Exception {
            // given
            KarloParameter karloParameter = new KarloParameter("prompt", "negativePrompt",
                samples, 10, 10D, 50, 5D,
                "decoder_ddim_v_prediction", null);
            CreateTestDiaryRequest request = new CreateTestDiaryRequest(1L, "notes",
                LocalDate.now(), LocalTime.now(), karloParameter);
            User user = TestUser.createAdminUserWithId(1L);
            Emotion emotion = TestEmotion.createEmotionWithId(2L);
            Diary testDiary = TestDiary.createTestDiaryWithId(3L, user, emotion);
            given(validateUserService.validateAdminUserById(any(Long.class)))
                .willReturn(user);
            given(validateEmotionService.validateEmotionById(any(Long.class)))
                .willReturn(emotion);
            given(diaryRepository.save(any(Diary.class)))
                .willReturn(testDiary);
            List<byte[]> images = IntStream.rangeClosed(1, samples)
                .mapToObj(i -> new byte[1]).collect(Collectors.toList());
            given(imageGeneratorService.generateTestImage(any(CreateTestDiaryRequest.class)))
                .willReturn(images);

            // when
            CreateDiaryResponse response = createDiaryService.createTestDiary(user.getUserId(),
                request);

            // then
            assertThat(response.getId()).isEqualTo(testDiary.getDiaryId());
            verify(imageService, times(samples)).uploadAndCreateImage(any(Diary.class),
                any(byte[].class), anyBoolean());
        }
    }

    @Nested
    @DisplayName("regenerateDiaryImage 메서드 테스트")
    class RegenerateDiaryImageTest {

        @Test
        @DisplayName("일기에 해당하는 성공한 프롬프트가 없을 경우 예외를 던진다.")
        void throw_exception_when_no_success_prompt() throws Exception {
            //given
            long userId = 1L;
            long diaryId = 2L;
            User user = TestUser.createUserWithId(userId);
            Emotion emotion = TestEmotion.createEmotionWithId(1L);
            Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);

            given(validateUserService.validateUserById(any(Long.class))).willReturn(user);
            given(validateDiaryService.validateDiaryById(any(Long.class), any(User.class)))
                .willReturn(diary);
            given(promptService.getPromptByDiaryId(eq(diaryId))).willReturn(Optional.empty());

            //when
            //then
            assertThatThrownBy(() -> createDiaryService.regenerateDiaryImage(userId, diaryId,
                request))
                .isInstanceOf(PromptNotExistException.class);
        }

        @Test
        @DisplayName("이미지를 재생성한 이후 재생성한 이미지를 대표 이미지로 등록한다.")
        void regenerateDiaryImage() throws Exception {
            //given
            byte[] image = new byte[1];
            long userId = 1L;
            long diaryId = 2L;
            String promptText = "test prompt";
            User user = TestUser.createUserWithId(userId);
            Emotion emotion = TestEmotion.createEmotionWithId(1L);
            Diary diary = TestDiary.createDiaryWithId(diaryId, user, emotion);
            Prompt prompt = TestPrompt.createPromptWithId(1L, promptText);

            given(validateUserService.validateUserById(any(Long.class))).willReturn(user);
            given(validateDiaryService.validateDiaryById(any(Long.class), any(User.class)))
                .willReturn(diary);
            given(promptService.getPromptByDiaryId(eq(diaryId))).willReturn(Optional.of(prompt));
            given(imageGeneratorService.generateImage(eq(prompt)))
                .willReturn(new GeneratedImageAndPrompt(promptText, image));

            //when
            createDiaryService.regenerateDiaryImage(userId, diaryId, request);

            //then
            verify(validateTicketService).findAndUseTicket(eq(userId));
            verify(imageService).unSelectAllImage(eq(diaryId));
            verify(imageService).uploadAndCreateImage(eq(diary), eq(image), eq(true));
        }
    }

}