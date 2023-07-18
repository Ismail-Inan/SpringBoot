package com.meineAngebote.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@SuperBuilder
@JsonInclude(NON_DEFAULT)
public class HttpResponse {

  protected String timeStamp;
  protected int statusCode;
  protected HttpStatus status;
  protected String message;
  protected Map<?, ?> data;
}