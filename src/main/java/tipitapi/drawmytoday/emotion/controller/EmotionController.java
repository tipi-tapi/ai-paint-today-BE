package tipitapi.drawmytoday.emotion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.common.validator.CustomCollectionValidator;
import tipitapi.drawmytoday.emotion.dto.CreateEmotionRequest;
import tipitapi.drawmytoday.emotion.dto.CreateEmotionResponse;
import tipitapi.drawmytoday.emotion.dto.GetActiveEmotionsResponse;
import tipitapi.drawmytoday.emotion.service.EmotionService;

@RestController
@RequestMapping("/emotions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class EmotionController {

    private final EmotionService emotionService;
    private final CustomCollectionValidator customCollectionValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(customCollectionValidator);
    }

    @Operation(summary = "감정 목록 조회", description = "일기 생성시 노출할 감정의 목록을 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "감정 목록 정보"),
    })
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<GetActiveEmotionsResponse>>> getAllEmotions(
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo,
        @Parameter(description = "반환 언어(ko/en)", in = ParameterIn.QUERY)
        @RequestParam(value = "lan", required = false, defaultValue = "ko") String language
    ) {
        if (!language.matches("^(ko|en)$")) {
            language = "ko";
        }
        return SuccessResponse.of(
            emotionService.getActiveEmotions(tokenInfo.getUserId(), language)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "감정 등록", description = "주어진 감정 데이터 목록을 등록한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "성공적으로 감정을 등록함"),
    })
    @PostMapping()
    public ResponseEntity<SuccessResponse<List<CreateEmotionResponse>>> createEmotions(
        @RequestBody @Valid List<CreateEmotionRequest> createEmotionRequests,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ObjectError objectError = bindingResult.getAllErrors().get(0);
            throw new IllegalArgumentException(objectError.getDefaultMessage());
        }

        return SuccessResponse.of(
            emotionService.createEmotions(createEmotionRequests)
        ).asHttp(HttpStatus.CREATED);
    }
}
