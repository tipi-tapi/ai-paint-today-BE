package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;

public class TestImage {

    public static Image createImage(Diary diary) {
        return Image.create(diary, null, "https://example.com/image.jpg", true);
    }

    public static Image createImageWithId(Long imageId, Diary diary) {
        return Image.restore(imageId, diary, null, "https://example.com/image.jpg",
            true, null, null, null);
    }
}
