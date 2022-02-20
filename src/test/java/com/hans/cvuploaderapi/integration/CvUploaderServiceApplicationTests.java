package com.hans.cvuploaderapi.integration;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;

import static com.hans.cvuploaderapi.controller.ResumeController.WRONG_FORMAT_ERROR_MSG;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class CvUploaderServiceApplicationTests {

  public static final String JSON_BODY_TEST =
      "{\"name\":\"Hans Melo\",\"email\":\"hansmelo@gmail.com\",\"educations\":[{\"name\":\"java\",\"institution\":\"coursera\"},{\"name\":\"python\",\"institution\":\"edx\"}],\"experiences\":[{\"role\":\"senior software engineer\",\"companyName\":\"123456789\"},{\"role\":\"jr software engineer\",\"companyName\":\"456786868\"}]}";
  public static final String HANS_CV_TXT = "hans_cv.txt";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Value("${minio.bucket.name}")
  private String bucketName;

  private MinioClient minioClient;

  @Value("${minio.access.key}")
  private String accessKey;

  @Value("${minio.access.secret}")
  private String secretKey;

  @Value("${minio.url}")
  private String minioUrl;

  @BeforeEach
  public void setUp() throws Exception {
    minioClient =
        new MinioClient.Builder().credentials(accessKey, secretKey).endpoint(minioUrl).build();
  }

  @Test
  public void testUploadShouldReturnResumeInJsonFormat() {
    var urlUpload = "http://localhost:" + port + "/upload";

    var parameters = new LinkedMultiValueMap<String, Object>();
    parameters.add("file", new org.springframework.core.io.ClassPathResource(HANS_CV_TXT));

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    var entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    var response = restTemplate.postForEntity(urlUpload, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getBody()).isEqualTo(JSON_BODY_TEST);
  }

  @Test
  public void testUploadShouldReturnBadRequestWhenUploadWrongFormatFile() {
    var urlUpload = "http://localhost:" + port + "/upload";

    var parameters = new LinkedMultiValueMap<String, Object>();
    parameters.add(
        "file", new org.springframework.core.io.ClassPathResource("wrong_format_cv.txt"));

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    var entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    var response = restTemplate.postForEntity(urlUpload, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains(WRONG_FORMAT_ERROR_MSG);
  }

  @Test
  public void testUploadShouldReturnBadRequestWhenUploadWithoutFile() {
    var urlUpload = "http://localhost:" + port + "/upload";

    var parameters = new LinkedMultiValueMap<String, Object>();

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    var entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    var response = restTemplate.postForEntity(urlUpload, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Required request part 'file' is not present");
  }

  @Test
  public void testUploadSaveShouldReturnResumeInJsonFormatAndSaveFile() {
    var urlUpload = "http://localhost:" + port + "/upload-save";

    var parameters = new LinkedMultiValueMap<String, Object>();
    parameters.add("file", new org.springframework.core.io.ClassPathResource(HANS_CV_TXT));

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    var entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    var response = restTemplate.postForEntity(urlUpload, entity, String.class);

    assertThat(getFileNameFromStorage()).isEqualTo(HANS_CV_TXT);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getBody()).isEqualTo(JSON_BODY_TEST);
  }

  private String getFileNameFromStorage() {
    try {
      return minioClient
          .getObject(GetObjectArgs.builder().bucket(bucketName).object("hans_cv.txt").build())
          .object();
    } catch (Exception ex) {
      return "";
    }
  }
}
