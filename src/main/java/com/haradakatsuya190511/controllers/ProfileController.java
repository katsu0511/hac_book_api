package com.haradakatsuya190511.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.profile.ProfileResponseDto;
import com.haradakatsuya190511.dtos.profile.UpdateEmailRequestDto;
import com.haradakatsuya190511.dtos.profile.UpdateNameRequestDto;
import com.haradakatsuya190511.dtos.profile.UpdatePasswordRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.ProfileService;
import com.haradakatsuya190511.utils.AuthCookieManager;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class ProfileController {
	
	private final ProfileService profileService;
	private final AuthCookieManager cookieManager;
	
	public ProfileController(ProfileService profileService, AuthCookieManager cookieManager) {
		this.profileService = profileService;
		this.cookieManager = cookieManager;
	}
	
	@GetMapping("/profile")
	public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(new ProfileResponseDto(profileService.getUser(user), profileService.getSetting(user)));
	}
	
	@PutMapping("/profile/name")
	public ResponseEntity<Void> updateName(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateNameRequestDto request) {
		profileService.updateName(user.getId(), request.getName());
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/profile/email")
	public ResponseEntity<Void> updateEmail(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateEmailRequestDto request, HttpServletResponse response) {
		profileService.updateEmail(user.getId(), request.getEmail());
		cookieManager.clearToken(response);
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/profile/password")
	public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal User user, @Valid @RequestBody UpdatePasswordRequestDto request, HttpServletResponse response) {
		profileService.updatePassword(user.getId(), request.getPassword());
		cookieManager.clearToken(response);
		return ResponseEntity.noContent().build();
	}
}
