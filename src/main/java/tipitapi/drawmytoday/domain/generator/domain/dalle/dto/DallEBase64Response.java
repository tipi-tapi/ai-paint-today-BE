package tipitapi.drawmytoday.domain.generator.domain.dalle.dto;

import java.util.List;
import lombok.Getter;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallERequestFailException;

@Getter
public class DallEBase64Response {

    private List<DallEBase64> data;

    public String getBase64(int index) throws DallERequestFailException {
        if (this.getData() == null || this.getData().isEmpty()) {
            throw new DallERequestFailException();
        }
        return this.data.get(index).getB64_json();
    }

    @Getter
    public static class DallEBase64 {

        private String b64_json;
    }
}
