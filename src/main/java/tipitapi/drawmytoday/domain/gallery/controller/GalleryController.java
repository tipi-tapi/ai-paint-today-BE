package tipitapi.drawmytoday.domain.gallery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.converter.GallerySort;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.gallery.dto.GetPaintingResponse;
import tipitapi.drawmytoday.domain.gallery.service.GalleryService;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class GalleryController {

    private final GalleryService galleryService;

    @Operation(summary = "모든 작품 조회", description = "인기순 or 최신순으로 모든 작품을 조회하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "일기 상세 정보")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<GetPaintingResponse>>> getGallery(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(name = "size", description = "페이지네이션의 페이지당 데이터 수", in = ParameterIn.QUERY)
        @RequestParam(value = "size", required = false, defaultValue = "10") int size,
        @Parameter(name = "page", description = "페이지네이션의 페이지 넘버. 0부터 시작함", in = ParameterIn.QUERY)
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @Parameter(name = "sort", description = "POPULARITY=인기순, LATEST=최신순", in = ParameterIn.QUERY)
        @RequestParam(name = "sort", required = false, defaultValue = "POPULARITY") GallerySort sort
    ) {
        return SuccessResponse.of(
            galleryService.getGallery(tokenInfo.getUserId(), size, page, sort)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "작품 공감", description = "작품에 공감 버튼을 누르면 공감되거나 공감이 취소되는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "작품 공감이나 공감 취소 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "PH001: 작품의 주인은 본인 작품에 공감을 하거나 공감 취소할 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/heart")
    public ResponseEntity<Void> PaintingLike(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(name = "id", description = "공감을 누를 작품의 id(painting_id)", in = ParameterIn.PATH)
        @PathVariable("id") Long paintingId
    ) {
        galleryService.togglePaintingLike(tokenInfo.getUserId(), paintingId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "작품 삭제", description = "작품을 삭제하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "작품 삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "PH002: 작품의 주인이 아니면 작품을 삭제할 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePainting(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(name = "id", description = "삭제할 작품의 id(painting_id)", in = ParameterIn.PATH)
        @PathVariable("id") Long paintingId
    ) {
        galleryService.deletePainting(tokenInfo.getUserId(), paintingId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "작품 신고", description = "작품을 신고하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "작품 신고 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "PH001: 작품의 주인은 본인 작품을 신고할 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "409",
            description = "PH003: 이미 신고된 작품입니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> reportPainting(
        @AuthUser JwtTokenInfo tokenInfo,
        @PathVariable("id") Long paintingId
    ) {
        galleryService.reportPainting(tokenInfo.getUserId(), paintingId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
