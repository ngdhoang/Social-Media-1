package com.GHTK.Social_Network;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class SocialNetworkApplication {
	@Value("${application.GHTK.SocialNetworkApplication.cloudName}")
	private String cloudName;

	@Value("${application.GHTK.SocialNetworkApplication.apiKey}")
	private String apiKey;

	@Value("${application.GHTK.SocialNetworkApplication.apiSecret}")
	private String apiSecret;

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	@Bean
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
