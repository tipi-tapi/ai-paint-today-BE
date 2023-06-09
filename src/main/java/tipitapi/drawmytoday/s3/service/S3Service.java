package tipitapi.drawmytoday.s3.service;

import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import tipitapi.drawmytoday.s3.exception.S3FailedException;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

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
