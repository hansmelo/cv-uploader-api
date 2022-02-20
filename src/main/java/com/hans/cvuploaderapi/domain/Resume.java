package com.hans.cvuploaderapi.domain;

import lombok.Data;

import java.util.List;

@Data
public class Resume {
  private String name;
  private String email;
  private List<Education> educations;
  private List<Experience> experiences;
}
