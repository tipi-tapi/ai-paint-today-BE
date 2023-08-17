package tipitapi.drawmytoday.r2.service;

import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import tipitapi.drawmytoday.s3.exception.S3FailedException;

@Service
public class R2Service {

    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucketName;

    public R2Service(@Value("${r2.credentials.access-key}") String accessKey,
        @Value("${r2.credentials.secret-key}") String secretKey,
        @Value("${r2.account-id}") String accountId) throws URISyntaxException {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
            .credentialsProvider(() -> credentials)
            .region(Region.of("auto"))
            .endpointOverride(new URI("https://" + accountId + ".r2.cloudflarestorage.com"))
            .build();
    }

    public void uploadImage(byte[] imageBytes, String filePath) {
        try {
            PutObjectRequest putObjectRequest = buildPutObjectRequest(filePath);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
        } catch (SdkClientException | S3Exception e) {
            throw e;
        } catch (Exception e) {
            throw new S3FailedException(e);
        }
    }

    private PutObjectRequest buildPutObjectRequest(String filePath) {
        return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .contentType("image/png")
            .acl(PRIVATE).build();
    }
}
