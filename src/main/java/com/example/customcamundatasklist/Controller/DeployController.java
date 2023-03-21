package com.example.customcamundatasklist.Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.camunda.zeebe.client.ZeebeClient;

@Controller
@RequestMapping("DeployBPMN")
public class DeployController {

	@Autowired
	private ZeebeClient zeebeClient;

	@GetMapping("")
	public String showBPMNpage() {

		return "start-process";
	}

	@PostMapping("/test")
	public String test() {
		return "home";
	}

	@PostMapping("/start")
	public String startBPMN(@RequestParam("bpmn-model") String str) throws IOException {

		System.out.println(str);

		// Save str as .bpmn file
		ClassPathResource classpath = new ClassPathResource("BPMN/tempt.bpmn");
		Files.writeString(classpath.getFile().toPath(), str, StandardCharsets.UTF_8);

		System.out.println("=========== Successfully Write to file ============");

		// get bpmn process id from str
		int startOfHead = str.indexOf("<bpmn:process id="); // 18
		int startIndex = startOfHead + 18; // starting index of process id in str
		int endIndex = str.indexOf("\" isExecutable="); // ending index of process id in str
		String processID = str.substring(startIndex, endIndex);

		zeebeClient.newDeployResourceCommand().addResourceFromClasspath("BPMN/tempt.bpmn").send().join();

		System.out.println("=========== Successfully Deploy Process ============");

		zeebeClient.newCreateInstanceCommand().bpmnProcessId(processID).latestVersion().send().join();

		System.out.println("============ Successfully Create new Process Instance ==============");

		return "redirect:/";
	}
}
