package com.hans.cvuploaderapi.service;

import com.hans.cvuploaderapi.domain.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.IOException;

@Slf4j
@Service
public class ResumeConverterService {

  public Resume toResume(MultipartFile file) throws IOException {
    var yaml = new Yaml(new Constructor(Resume.class));
    Resume resume = yaml.load(file.getInputStream());

    log.info("Processed file: {}. It was transformed into JSON format.", file.getOriginalFilename());

    return resume;
  }
}
