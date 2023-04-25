package tipitapi.drawmytoday.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

  // Common
  INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
  METHOD_NOT_ALLOWED(405, "C002", "허용하지 않는 HTTP 메서드입니다."),
  ENTITY_NOT_FOUND(400, "C003", "엔티티를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(500, "C004", "서버 오류"),
  INVALID_TYPE_VALUE(400, "C005", "잘못된 타입의 값입니다."),
  HANDLE_ACCESS_DENIED(403, "C006", "접근이 거부됐습니다."),

  // Authentication

  // User
  USER_NOT_FOUND(404, "U001", "회원을 찾을 수 없습니다."),
  USER_ALREADY_EXISTS(409, "U002", "이미 존재하는 유저입니다."),

  // Diary

  // Emotion

  // S3
  S3_SERVICE_ERROR(500, "S3001", "AmazonServiceException 에러가 발생하였습니다."),
  S3_SDK_ERROR(500, "S3002", "SdkClientException 에러가 발생하였습니다.");


  private final int status;
  private final String code;
  private final String message;

  ErrorCode(final int status, final String code, final String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}