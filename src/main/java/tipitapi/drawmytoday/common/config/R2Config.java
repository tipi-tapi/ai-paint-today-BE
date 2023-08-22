package tipitapi.drawmytoday.common.config;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class R2Config {

    @Value("${r2.credentials.access-key}")
    private String accessKey;

    @Value("${r2.credentials.secret-key}")
    private String secretKey;

    @Value("${r2.account-id}")
    private String accountId;

    @Bean
    public S3Client r2Client() throws URISyntaxException {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
            .credentialsProvider(() -> credentials)
            .region(Region.of("auto"))
            .endpointOverride(new URI("https://" + accountId + ".r2.cloudflarestorage.com"))
            .build();
    }

    @Bean
    public S3Presigner r2Presigner() throws URISyntaxException {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
            .credentialsProvider(() -> credentials)
            .region(Region.of("auto"))
            .endpointOverride(new URI("https://" + accountId + ".r2.cloudflarestorage.com"))
            .build();
    }
}
