package tipitapi.drawmytoday.migration;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class FirestoreConfig {

    @Value("${firestore.emulator-host:#{null}}")
    private String emulatorHost;

    @Value("${firestore.project-id:draw-my-today}")
    private String projectId;

    @Value("${firestore.database-id:(default)}")
    private String databaseId;

    @Bean
    public Firestore firestore() throws IOException {
        GoogleCredentials credentials = resolveCredentials();

        FirestoreOptions.Builder builder = FirestoreOptions.newBuilder()
            .setProjectId(projectId)
            .setDatabaseId(databaseId)
            .setCredentials(credentials);

        Firestore firestore = builder.build().getService();

        if (emulatorHost != null) {
            log.info("Firestore target: emulator at {} (project={}, db={})", emulatorHost, projectId, databaseId);
        } else {
            log.warn("Firestore target: PRODUCTION (project={}, db={})", projectId, databaseId);
        }

        return firestore;
    }

    private GoogleCredentials resolveCredentials() {
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            if (emulatorHost != null) {
                log.warn("ADC not available, using stub credentials for emulator: {}", e.getMessage());
                return GoogleCredentials.create(new AccessToken("owner", null));
            }
            throw new IllegalStateException(
                "Failed to load Application Default Credentials for production Firestore. " +
                "Set GOOGLE_APPLICATION_CREDENTIALS or run 'gcloud auth application-default login'.", e);
        }
    }
}
