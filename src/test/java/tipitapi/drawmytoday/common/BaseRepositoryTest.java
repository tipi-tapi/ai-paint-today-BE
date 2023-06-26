package tipitapi.drawmytoday.common;

import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestImage;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.diary.repository.ImageRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.repository.UserRepository;

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

}
