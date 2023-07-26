package tipitapi.drawmytoday.common.converter;


import java.util.Objects;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToLanguageConverter implements Converter<String, Language> {

    @Override
    public Language convert(String source) {
        return Objects.equals(source, "en") ? Language.en : Language.ko;
    }
}
