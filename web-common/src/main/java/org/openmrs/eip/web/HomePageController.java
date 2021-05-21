package org.openmrs.eip.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
	
	@GetMapping("/")
	public String home() {
		return "redirect:index.html";
	}
	
}
