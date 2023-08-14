package tipitapi.drawmytoday.common.exception;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import tipitapi.drawmytoday.common.response.ErrorResponse;
import tipitapi.drawmytoday.common.response.ErrorResponse.ValidationError;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.dalle.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.s3.exception.S3FailedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException e) {
        log.warn("handleBusinessException", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    // @Valid 예외처리
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return handleExceptionInternal(e, errorCode);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException e,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        log.warn("handleHttpMessageNotReadable", e);
        return handleExceptionInternal(ErrorCode.INVALID_INPUT_VALUE);
    }

    // 이 외의 500 에러 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        log.warn("handleAllException", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<Object> handleS3SdkClientException(SdkClientException e) {
        log.error("S3SdkClientException", e);
        return handleExceptionInternal(ErrorCode.S3_SDK_ERROR);
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<Object> handleS3Exception(S3Exception e) {
        log.error("S3Exception", e);
        return handleExceptionInternal(ErrorCode.S3_SERVICE_ERROR);
    }

    @ExceptionHandler(S3FailedException.class)
    public ResponseEntity<Object> handleS3FailedException(S3FailedException e) {
        log.error("S3FailedException", e);
        return handleExceptionInternal(ErrorCode.S3_FAILED);
    }

    @ExceptionHandler(DallERequestFailException.class)
    public ResponseEntity<Object> handleDallERequestFailException(DallERequestFailException e) {
        log.error("DallERequestFailException", e);
        return handleExceptionInternal(ErrorCode.DALLE_REQUEST_FAIL);
    }

    @ExceptionHandler(ImageInputStreamFailException.class)
    public ResponseEntity<Object> handleImageInputStreamFailException(
        ImageInputStreamFailException e) {
        log.error("ImageInputStreamFailException", e);
        return handleExceptionInternal(ErrorCode.IMAGE_INPUT_STREAM_FAIL);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClientException(RestClientException e) {
        log.error("RestClientException", e);
        return handleExceptionInternal(ErrorCode.REST_CLIENT_FAILED);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
            .body(makeErrorResponse(errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
            .body(makeErrorResponse(errorCode, message));
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
            .body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(message)
            .build();
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ValidationError> validationErrorList = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(ErrorResponse.ValidationError::of)
            .collect(Collectors.toList());

        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .errors(validationErrorList)
            .build();
    }

}
