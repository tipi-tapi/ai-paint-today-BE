package tipitapi.drawmytoday.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Operation(summary = "서버 생존 여부 체크용", description = "서버가 살아있는지 체크합니다.")
    @ApiResponse(responseCode = "204", description = "서버 생존")
    @GetMapping("/server")
    public ResponseEntity<Void> verifyServerAlive() {
        return ResponseEntity.noContent().build();
    }
}
