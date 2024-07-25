package com.GHTK.Social_Network.common.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
  @Value("${application.GHTK.SocialNetworkApplication.cloudName}")
  private String cloudName;

  @Value("${application.GHTK.SocialNetworkApplication.apiKey}")
  private String apiKey;

  @Value("${application.GHTK.SocialNetworkApplication.apiSecret}")
  private String apiSecret;


  @Bean(name = "myCloudinaryConfig")
  public Cloudinary cloudinaryConfig() {
    Cloudinary cloudinary = null;
    Map config = new HashMap();
    config.put("cloud_name", cloudName);
    config.put("api_key", apiKey);
    config.put("api_secret", apiSecret);
    cloudinary = new Cloudinary(config);
    return cloudinary;
  }

}
