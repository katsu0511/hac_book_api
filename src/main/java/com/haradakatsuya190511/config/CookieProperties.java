package com.haradakatsuya190511.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
	
	private String sameSite;
	private boolean secure;
	
	public String getSameSite() {
		return sameSite;
	}
	
	public void setSameSite(String sameSite) {
		this.sameSite = sameSite;
	}
	
	public boolean isSecure() {
		return secure;
	}
	
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
}
