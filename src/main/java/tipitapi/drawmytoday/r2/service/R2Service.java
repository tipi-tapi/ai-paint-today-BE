package tipitapi.drawmytoday.r2.service;

import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import tipitapi.drawmytoday.r2.exception.R2FailedException;

@Service
public class R2Service {

    private final S3Client s3Client;


    private final String bucketName;

    public R2Service(@Qualifier("r2Client") S3Client s3Client,
        @Value("${r2.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void uploadImage(byte[] imageBytes, String filePath) {
        try {
            PutObjectRequest putObjectRequest = buildPutObjectRequest(filePath);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
        } catch (SdkClientException | S3Exception e) {
            throw e;
        } catch (Exception e) {
            throw new R2FailedException(e);
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
