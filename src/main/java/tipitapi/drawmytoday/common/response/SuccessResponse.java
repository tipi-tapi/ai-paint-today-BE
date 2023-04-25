package tipitapi.drawmytoday.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class SuccessResponse<T> {
  private final boolean status = true;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private T data;

  public static <T> SuccessResponse<T> of(T data) {
    SuccessResponse<T> SuccessResponse = new SuccessResponse<>();

    SuccessResponse.data = data;

    return SuccessResponse;
  }

  public ResponseEntity<SuccessResponse<T>> asHttp(HttpStatus httpStatus) {
    return ResponseEntity.status(httpStatus).body(this);
  }
}
