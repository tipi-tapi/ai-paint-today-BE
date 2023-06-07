package tipitapi.drawmytoday.dalle.dto;

import java.util.List;
import lombok.Getter;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;

@Getter
public class DallEUrlResponse {

    private List<DallEUrl> data;

    public String getUrl(int index) throws DallERequestFailException {
        if (this.data == null || this.data.isEmpty()) {
            throw new DallERequestFailException();
        }
        return this.data.get(index).getUrl();
    }

    @Getter
    public static class DallEUrl {

        private String url;
    }
}
