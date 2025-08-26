package com.haradakatsuya190511.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@RequestMapping("/")
	public String index() {
		return "Hello World!";
	}
}
