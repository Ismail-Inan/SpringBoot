package com.meineAngebote.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
public class S3Buckets {

  private String appBucket;

  public String getAppBucket() {
    return appBucket;
  }

  public void setAppBucket(String name) {
    this.appBucket = name;
  }

}