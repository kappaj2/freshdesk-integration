package com.sk.crm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud", ignoreUnknownFields = true)
public class AWSProperties {

     public final Aws aws = new Aws();

     public Aws getAws() {
          return aws;
     }

     public class Aws {

          public final Stack stack = new Stack();
          public final EndPoint endPoint = new EndPoint();
          public final Loader loader = new Loader();

          public final Sqs sqs = new Sqs();

          public Stack getStack() {
               return stack;
          }

          public EndPoint getEndPoint() {
               return endPoint;
          }

          public Loader getLoader() {
               return loader;
          }

          public Sqs getSqs() {
               return sqs;
          }

          public class Stack {
               private boolean auto;

               public boolean getAuto() {
                    return auto;
               }

               public void setAuto(boolean auto) {
                    this.auto = auto;
               }
          }

          public class EndPoint {
               private String uri;

               public String getUri() {
                    return uri;
               }

               public void setUri(String uri) {
                    this.uri = uri;
               }
          }

          public class Loader {
               private Integer queueCapacity;

               public Integer getQueueCapacity() {
                    return queueCapacity;
               }

               public void setQueueCapacity(Integer queueCapacity) {
                    this.queueCapacity = queueCapacity;
               }
          }

          public class Sqs {
               private Integer maxRetryCount;
               private Integer maxDelaySeconds;
               private Integer delayStartSeconds;
               private Integer retryDelayIncrement;
               private String queueName;

               public Integer getMaxRetryCount() {
                    return maxRetryCount;
               }

               public void setMaxRetryCount(Integer maxRetryCount) {
                    this.maxRetryCount = maxRetryCount;
               }

               public Integer getMaxDelaySeconds() {
                    return maxDelaySeconds;
               }

               public void setMaxDelaySeconds(Integer maxDelaySeconds) {
                    this.maxDelaySeconds = maxDelaySeconds;
               }

               public Integer getDelayStartSeconds() {
                    return delayStartSeconds;
               }

               public void setDelayStartSeconds(Integer delayStartSeconds) {
                    this.delayStartSeconds = delayStartSeconds;
               }

               public Integer getRetryDelayIncrement() {
                    return retryDelayIncrement;
               }

               public void setRetryDelayIncrement(Integer retryDelayIncrement) {
                    this.retryDelayIncrement = retryDelayIncrement;
               }

               public String getQueueName() {
                    return queueName;
               }

               public void setQueueName(String queueName) {
                    this.queueName = queueName;
               }
          }
     }
}
