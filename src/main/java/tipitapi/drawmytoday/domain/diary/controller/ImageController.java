package tipitapi.drawmytoday.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.diary.dto.ReviewDiaryRequest;
import tipitapi.drawmytoday.domain.diary.service.ImageService;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "일기 이미지 삭제", description = "주어진 ID의 일기 이미지를 삭제(Soft Delete)한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 일기 이미지를 삭제함"),
        @ApiResponse(
            responseCode = "403",
            description = "D002 : 자신의 일기에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "403",
            description = "I002 : 대표 이미지는 삭제할 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "403",
            description = "I003 : 일기는 최소 한 장의 이미지가 필요합니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "D001 : 일기를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "I001 : 선택된 이미지를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(description = "일기 이미지 id", in = ParameterIn.PATH) @PathVariable("id") Long imageId
    ) {
        imageService.deleteImage(tokenInfo.getUserId(), imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이미지 평가", description = "주어진 ID의 이미지를 평가한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 이미지를 평가함"),
        @ApiResponse(
            responseCode = "403",
            description = "I004 : 자신의 이미지에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "I001 : 이미지를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/{id}/review")
    public ResponseEntity<Void> reviewImage(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(description = "이미지 id", in = ParameterIn.PATH) @PathVariable("id") Long imageId,
        @RequestBody @Valid ReviewDiaryRequest reviewDiaryRequest
    ) {
        imageService.reviewImage(tokenInfo.getUserId(), imageId, reviewDiaryRequest.getReview());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "대표 이미지 설정", description = "주어진 ID의 이미지를 일기의 대표 이미지로 설정한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 이미지를 평가함"),
        @ApiResponse(
            responseCode = "403",
            description = "I004 : 자신의 이미지에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "D001 : 일기를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "I001 : 이미지를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> setSelectedImage(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(description = "이미지 ID", in = ParameterIn.PATH) @PathVariable("id") Long imageId
    ) {
        imageService.setSelectedImage(tokenInfo.getUserId(), imageId);
        return ResponseEntity.noContent().build();
    }
}
