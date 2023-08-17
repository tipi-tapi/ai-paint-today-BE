package tipitapi.drawmytoday.r2.service;

import static java.time.Duration.ofMinutes;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import tipitapi.drawmytoday.s3.exception.S3FailedException;

@Service
public class R2PreSignedService {

    private final S3Presigner s3Presigner;

    @Value("${r2.bucket}")
    private String bucketName;

    public R2PreSignedService(@Value("${r2.credentials.access-key}") String accessKey,
        @Value("${r2.credentials.secret-key}") String secretKey,
        @Value("${r2.account-id}") String accountId) throws URISyntaxException {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Presigner = S3Presigner.builder()
            .credentialsProvider(() -> credentials)
            .region(Region.of("auto"))
            .endpointOverride(new URI("https://" + accountId + ".r2.cloudflarestorage.com"))
            .build();
    }

    /*
     * S3 이미지 조회용 pre-signed URL 발급 메서드
     */
    public String getPreSignedUrlForShare(String objectKey, long expirationMin) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(ofMinutes(expirationMin))
                .getObjectRequest(getObjectRequest).build();

            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        } catch (SdkClientException | S3Exception e) {
            throw e;
        } catch (Exception e) {
            throw new S3FailedException(e);
        }
    }
}
