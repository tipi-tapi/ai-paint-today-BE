package tipitapi.drawmytoday.domain.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthCheckController {

    private final Environment env;
    private final String[] availableProfiles = {"loc", "test", "prod"};

    @Operation(summary = "서버 생존 여부 체크용", description = "서버가 살아있는지 체크합니다.")
    @ApiResponse(responseCode = "204", description = "서버 생존")
    @RequestMapping(value = "/server", method = RequestMethod.HEAD)
    public ResponseEntity<Void> verifyServerAlive() {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "서버 프로필 체크용", description = "서버가 어떤 프로필로 동작중인지 체크합니다.")
    @ApiResponse(responseCode = "200", description = "서버 프로필 정상 반환")
    @GetMapping("/profile")
    public String getActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        for (String activeProfile : availableProfiles) {
            for (String profile : profiles) {
                if (profile.contains(activeProfile)) {
                    return profile;
                }
            }
        }
        throw new IllegalStateException("프로필이 존재하지 않습니다. profiles: " + Arrays.toString(profiles));
    }
}
