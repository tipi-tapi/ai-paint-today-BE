package tipitapi.drawmytoday.domain.gallery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
