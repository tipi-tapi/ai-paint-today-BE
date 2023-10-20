package tipitapi.drawmytoday.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.common.testdata.TestImage;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.diary.repository.ImageRepository;
import tipitapi.drawmytoday.domain.diary.repository.PromptRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class BaseRepositoryTest {

    @Autowired
    protected DiaryRepository diaryRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected EmotionRepository emotionRepository;
    @Autowired
    protected ImageRepository imageRepository;
    @Autowired
    protected PromptRepository promptRepository;

    protected User createUser() {
        return userRepository.save(TestUser.createUser());
    }

    protected Diary createDiaryWithId(Long diaryId, User user) {
        Emotion emotion = emotionRepository.save(createEmotion());
        return diaryRepository.save(TestDiary.createDiaryWithId(diaryId, user, emotion));
    }

    protected Diary createDiary(User user, Emotion emotion) {
        return diaryRepository.save(TestDiary.createDiary(user, emotion));
    }

    protected Image createImage(Long imageId, Diary diary) {
        return imageRepository.save(TestImage.createImageWithId(imageId, diary));
    }

    protected Emotion createEmotion() {
        return emotionRepository.save(TestEmotion.createEmotion());
    }

    protected Prompt createPrompt(Diary diary, String promptText, boolean isSuccess) {
        return promptRepository.save(Prompt.create(diary, promptText, isSuccess));
    }

}
