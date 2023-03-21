package com.example.customcamundatasklist.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("DeployBPMN")
public class DeployController {

	@GetMapping("")
	public String showBPMNpage() {

		return "start-process";
	}

	@PostMapping("/start")
	public String startBPMN(@RequestParam("bpmn-model") String str) {

		System.out.println(str);
		System.out.println("===============================");

		return "redirect:/";
	}
}
