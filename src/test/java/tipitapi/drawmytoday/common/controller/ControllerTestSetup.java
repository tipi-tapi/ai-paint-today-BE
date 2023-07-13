package tipitapi.drawmytoday.common.controller;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tipitapi.drawmytoday.common.validator.CustomCollectionValidator;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.ReviewType;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

@Import({CustomCollectionValidator.class})
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSetup {

    protected static final long REQUEST_USER_ID = 1L;
    protected MockMvc mockMvc;
    protected MockMvc noSecurityMockMvc;

    // "yyyy-MM-dd'T'HH:mm:ss.SSS" 형식으로 변환
    protected static String parseLocalDateTime(LocalDateTime input) {
        String inputString = input.toString();
        if (inputString.length() <= 23) {
            return inputString;
        }
        return inputString.substring(0, 23);
    }

    @BeforeEach
    void setUp(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
        this.noSecurityMockMvc = MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
    }

    protected User getUser(SocialCode socialCode) {
        User user = User.builder()
            .email("email")
            .socialCode(socialCode)
            .build();
        ReflectionTestUtils.setField(user, "userId", REQUEST_USER_ID);
        return user;
    }

    protected Diary getDiary(User user, Emotion emotion) {
        Diary diary = Diary.builder()
            .user(user)
            .emotion(emotion)
            .diaryDate(LocalDateTime.now())
            .notes("notes")
            .isAi(true)
            .review(ReviewType.GOOD)
            .weather("weather")
            .build();
        ReflectionTestUtils.setField(diary, "diaryId", 1L);
        ReflectionTestUtils.setField(diary, "createdAt", LocalDateTime.now());
        return diary;
    }

    protected Emotion getEmotion() {
        Emotion emotion = Emotion.create("파랑", "blue", true, "blue", "blue");
        ReflectionTestUtils.setField(emotion, "emotionId", 1L);
        return emotion;
    }

}
