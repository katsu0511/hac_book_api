package com.haradakatsuya190511.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthCookieManager {
	
	private static final String COOKIE_NAME = "token";
	private static final boolean SECURE = false;
//	private static final boolean SECURE = true;
	private static final String PATH = "/";
	private static final int DEFAULT_MAX_AGE = 3600;
	
	public void setToken(HttpServletResponse response, String jwt) {
		Cookie cookie = new Cookie(COOKIE_NAME, jwt);
		cookie.setHttpOnly(true);
		cookie.setSecure(SECURE);
		cookie.setPath(PATH);
		cookie.setMaxAge(DEFAULT_MAX_AGE);
		response.addCookie(cookie);
	}
	
	public void clearToken(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, "");
		cookie.setHttpOnly(true);
		cookie.setSecure(SECURE);
		cookie.setPath(PATH);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
