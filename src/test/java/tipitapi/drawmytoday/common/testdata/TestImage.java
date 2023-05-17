package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;

public class TestImage {

    public static Image createImage(Diary diary) {
        return Image.create(diary, "https://example.com/image.jpg", true);
    }

    public static Image createImageWithId(Long imageId, Diary diary) {
        Image image = createImage(diary);
        ReflectionTestUtils.setField(image, "imageId", imageId);
        return image;
    }
}
