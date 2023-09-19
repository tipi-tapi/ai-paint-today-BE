package tipitapi.drawmytoday.domain.r2.service;

import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PRIVATE;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class R2Service {

    private final S3Client s3Client;
    @Value("${r2.bucket}")
    private String bucketName;

    public void uploadImage(byte[] imageBytes, String filePath) {
        // mocking 이므로 주석 처리
//        try {
//            PutObjectRequest putObjectRequest = buildPutObjectRequest(filePath);
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
//        } catch (SdkClientException | S3Exception e) {
//            throw e;
//        } catch (Exception e) {
//            throw new R2FailedException(e);
//        }
    }

    private PutObjectRequest buildPutObjectRequest(String filePath) {
        return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .contentType("image/png")
            .acl(PRIVATE).build();
    }
}
