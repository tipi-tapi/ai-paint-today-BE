package tipitapi.drawmytoday.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;
}