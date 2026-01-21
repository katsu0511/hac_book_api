package com.haradakatsuya190511.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
	
	private final CookieProperties cookieProperties;
	
	public TomcatConfig(CookieProperties cookieProperties) {
		this.cookieProperties = cookieProperties;
	}
	
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
		return factory -> factory.addContextCustomizers(context -> {
			Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
			cookieProcessor.setSameSiteCookies(cookieProperties.getSameSite());
			context.setCookieProcessor(cookieProcessor);
		});
	}
}
