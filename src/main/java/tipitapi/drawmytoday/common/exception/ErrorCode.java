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

    // Security
    AUTHORITY_NOT_FOUND(404, "S001", "유저 권한이 없습니다."),
    INVALID_TOKEN(400, "S002", "유효하지 않은 토큰입니다."),
    JWT_ACCESS_TOKEN_NOT_FOUND(404, "S003", "jwt access token이 없습니다."),
    JWT_REFRESH_TOKEN_NOT_FOUND(404, "S004", "jwt refresh token이 없습니다."),
    EXPIRED_JWT_ACCESS_TOKEN(400, "S005", "jwt access token이 만료되었습니다."),
    EXPIRED_JWT_REFRESH_TOKEN(400, "S006", "jwt refresh token이 만료되었습니다."),
    AUTH_CODE_NOT_FOUND(404, "S007", "authorization header가 비었습니다."),


    // User
    USER_NOT_FOUND(404, "U001", "회원을 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(409, "U002", "이미 존재하는 유저입니다."),
    DUPLICATE_USER(400, "U003", "유저가 중복되었습니다."),

    // Diary
    DIARY_NOT_FOUND(404, "D001", "일기를 찾을 수 없습니다."),
    DIARY_NOT_OWNER(403, "D002", "자신의 일기에만 접근할 수 있습니다."),

    // Image
    IMAGE_NOT_FOUND(404, "I001", "선택된 이미지를 찾을 수 없습니다."),

    // Emotion
    EMOTION_NOT_FOUND(404, "E001", "감정을 찾을 수 없습니다."),

    // S3
    S3_SERVICE_ERROR(500, "S3001", "S3Exception 에러가 발생하였습니다."),
    S3_SDK_ERROR(500, "S3002", "SdkClientException 에러가 발생하였습니다."),
    S3_FAILED(500, "S3003", "S3 처리에 실패하였습니다."),

    // DALL-E
    DALLE_REQUEST_FAIL(500, "DE001", "DALL-E 요청에 실패하였습니다."),

    // Image InputStream
    IMAGE_INPUT_STREAM_FAIL(500, "IIS001", "이미지 스트림을 가져오는데 실패하였습니다."),

    // OAuth
    OAUTH_NOT_FOUND(404, "O001", "OAuth를 찾을 수 없습니다."),
    OAUTH_SERVER_FAILED(500, "O002", "OAuth 서버와의 통신 중 에러가 발생하였습니다.");


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}