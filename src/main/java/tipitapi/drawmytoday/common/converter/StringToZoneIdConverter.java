package tipitapi.drawmytoday.common.converter;

import java.time.DateTimeException;
import java.time.ZoneId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToZoneIdConverter implements Converter<String, ZoneId> {

    @Override
    public ZoneId convert(String source) {
        try {
            return ZoneId.of(source);
        } catch (DateTimeException e) {
            return ZoneId.of("Asia/Seoul");
        }
    }
}
