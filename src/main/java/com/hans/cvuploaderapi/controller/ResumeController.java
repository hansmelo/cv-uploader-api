package com.hans.cvuploaderapi.controller;

import com.hans.cvuploaderapi.domain.Resume;
import com.hans.cvuploaderapi.service.FileStorageService;
import com.hans.cvuploaderapi.service.ResumeConverterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
@RestController
public class ResumeController {

  public static final String WRONG_FORMAT_ERROR_MSG = "The file is in the wrong format.";
  private final ResumeConverterService converterService;
  private final FileStorageService storageService;

  @PostMapping(value = "/upload", consumes = "multipart/form-data", produces = "application/json")
  public ResponseEntity<Resume> upload(@RequestParam("file") MultipartFile file) {
    log.info("Upload: {}", file.getOriginalFilename());
    return handleCv(file);
  }

  @PostMapping(value = "/upload-save", consumes = "multipart/form-data", produces = "application/json")
  public ResponseEntity<Resume> uploadSave(@RequestParam("file") MultipartFile file) {
    log.info("Upload and Save: {}", file.getOriginalFilename());
    var response = handleCv(file);
    storageService.uploadFile(file);

    return response;
  }

  private ResponseEntity<Resume> handleCv(MultipartFile file) {
    try {
      return ResponseEntity.accepted().body(converterService.toResume(file));
    } catch (YAMLException | IOException ex) {
      log.error("Problem in the processing of the file: {}", file.getOriginalFilename());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WRONG_FORMAT_ERROR_MSG);
    }
  }
}
