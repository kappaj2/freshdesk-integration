package com.sk.fresh.service.restclient.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreshContact implements FreshDeskRequestPayload {

     @JsonProperty("address")
     private String address;

     @JsonProperty("company_id")
     private Integer companyId;

     @JsonProperty("description")
     private String description;

     @JsonProperty("email")
     private String email;

     @JsonProperty("id")
     private BigDecimal id;

     @JsonProperty("job_title")
     private String jobTitle;

     @JsonProperty("language")
     private String language;

     @JsonProperty("mobile")
     private String mobile;

     @JsonProperty("name")
     private String name;

     @JsonProperty("phone")
     private String phone;

     @JsonProperty("time_zone")
     private String timeZone;

     @JsonProperty("twitter_id")
     private String twitterId;

     @JsonProperty("facebook_id")
     private String facebookId;

     @JsonProperty("created_at")
     private String createAt;

     @JsonProperty("updated_at")
     private String updatedAt;

     @JsonProperty("csat_rating")
     private String csatRating;

     @JsonProperty("preferred_source")
     private String preferredSource;

     @JsonProperty("unique_external_id")
     private String uniqueExternalId;

     @JsonProperty("twitter_profile_status")
     private String twitterProfileStatus;

     @JsonProperty("twitter_followers_count")
     private Integer twitterFollowerCount;

     @JsonProperty("custom_fields")
     private Map<String, Object> customFields = new HashMap<>();

     @JsonProperty("tags")
     private String[] tags;
}
