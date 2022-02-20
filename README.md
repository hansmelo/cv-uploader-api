## CV UPLOADER API

cv-uploader-api cover several topics:

- Java17
- Spring boot 2.6
- Lombok
- Testcontainers
- Docker Compose
- Minio (Multi-Cloud Object Storage)
- Swagger

## usage
Consider it is running from the root path(cv-uploader-api)

## usage

To run tests and build:

```shell
$ mvn clean install
```

To run this service:

```shell
$ mvn spring-boot:run
```

To Run the Minio locally:

```shell
$ sudo docker-compose pull & sudo docker-compose up -d
```


## Sample requests

Upload file and transform to json
```shell
//there is a file for testing 
$ cd src/test/resources
$ curl -X 'POST' 'http://localhost:8080/upload' -H 'accept: application/json' -H 'Content-Type: multipart/form-data' -F 'file=@hans_cv.txt;type=text/plain'
```
```json
{
  "name": "Hans Melo",
  "email": "hansmelo@gmail.com",
  "educations": [
    {
      "name": "HTML5 and CSS Fundamentals",
      "institution": "Coursera"
    },
    {
      "name": "Blockchain and FinTech: Basics, Applications, and Limitations",
      "institution": "Edx"
    }
  ],
  "experiences": [
    {
      "role": "senior software engineer",
      "companyName": "Meta"
    },
    {
      "role": "jr software engineer",
      "companyName": "Google"
    }
  ]
}
```

Upload file, transform to json and save in the storage
```shell
//there is a file for testing
$ cd src/test/resources
$ curl -X 'POST' 'http://localhost:8080/upload-save' -H 'accept: application/json' -H 'Content-Type: multipart/form-data' -F 'file=@hans_cv.txt;type=text/plain'
```
```json
{
  "name": "Hans Melo",
  "email": "hansmelo@gmail.com",
  "educations": [
    {
      "name": "HTML5 and CSS Fundamentals",
      "institution": "Coursera"
    },
    {
      "name": "Blockchain and FinTech: Basics, Applications, and Limitations",
      "institution": "Edx"
    }
  ],
  "experiences": [
    {
      "role": "senior software engineer",
      "companyName": "Meta"
    },
    {
      "role": "jr software engineer",
      "companyName": "Google"
    }
  ]
}
```
To check the file, access this link:

Minio: http://127.0.0.1:9001/buckets/resume-bucket/browse

Username: minio
Password: minio123

## Swagger
OpenAPI definition: http://localhost:8080/swagger-ui/index.html

## Project Structure
- [cv-uploader-api]
    - [main/java/com/hans/cvuploaderapi]
        - [/config] : Configuration of the app.
        - [/controller] : Controller.
        - [/service] : Services for the business logic needed by controller.
        - [/domain] : Domain classes.
    - [test/java/com/hans/cvuploaderapi]
        - [/integration] : Integration tests.

## Future Improvements
- Add more endpoints, for example to retriever a file or a list of them;
- Add more tests, mainly unit tests;
- Validate the format and size of the file.
