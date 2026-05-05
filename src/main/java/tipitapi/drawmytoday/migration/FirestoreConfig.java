package tipitapi.drawmytoday.migration;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Optional;

@Configuration
@Profile("bulk-load")
@Slf4j
public class FirestoreConfig {

    @Bean
    public Firestore firestore() throws IOException {
        String emulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
        String projectId = Optional.ofNullable(System.getenv("FIRESTORE_PROJECT_ID"))
            .orElse("demo-migration");

        GoogleCredentials credentials = resolveCredentials(emulatorHost);

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .setProjectId(projectId)
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        if (emulatorHost != null) {
            log.info("Firestore target: emulator at {} (project={})", emulatorHost, projectId);
        } else {
            log.warn("Firestore target: PRODUCTION (project={})", projectId);
        }

        return FirestoreClient.getFirestore();
    }

    private GoogleCredentials resolveCredentials(String emulatorHost) {
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            if (emulatorHost != null) {
                // Emulator doesn't validate credentials — fall back to a stub token
                log.warn("ADC not available, using stub credentials for emulator: {}", e.getMessage());
                return GoogleCredentials.create(new AccessToken("owner", null));
            }
            throw new IllegalStateException(
                "Failed to load Application Default Credentials for production Firestore. " +
                "Set GOOGLE_APPLICATION_CREDENTIALS or run 'gcloud auth application-default login'.", e);
        }
    }
}
