package com.haradakatsuya190511.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.profile.ProfileResponseDto;
import com.haradakatsuya190511.dtos.profile.UpdateNameRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.ProfileService;

import jakarta.validation.Valid;

@RestController
public class ProfileController {
	
	private final ProfileService profileService;
	
	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
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
}
