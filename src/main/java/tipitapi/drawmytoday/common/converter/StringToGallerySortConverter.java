package tipitapi.drawmytoday.common.converter;

import java.util.Objects;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToGallerySortConverter implements Converter<String, GallerySort> {

    @Override
    public GallerySort convert(String source) {
        return Objects.equals(source, "LATEST") ? GallerySort.LATEST : GallerySort.POPULARITY;
    }
}
