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

import com.example.customcamundatasklist.util.DeployBPMNService;

import io.camunda.zeebe.client.ZeebeClient;

@Controller
@RequestMapping("DeployBPMN")
public class DeployController {

	@Autowired
	private ZeebeClient zeebeClient;
	@Autowired
	private DeployBPMNService deployBPMNService;

	@GetMapping("")
	public String showBPMNpage() {

		return "start-process";
	}

	@PostMapping("/test")
	public String test() {
		return "home";
	}

	@PostMapping("/start")
	public String startBPMN(@RequestParam("bpmn-model") String bpmnStr) throws IOException {

		// set isExecutable to true
		bpmnStr = deployBPMNService.setBPMNExecutableTrue(bpmnStr);
		// get processID from bpmn string
		String processID = deployBPMNService.getProcessIDFromBPMN(bpmnStr);

		// Save str as .bpmn file
		ClassPathResource classpath = new ClassPathResource("BPMN/tempt.bpmn");
		Files.writeString(classpath.getFile().toPath(), bpmnStr, StandardCharsets.UTF_8);

		System.out.println("=========== Successfully Write to file ============");

		zeebeClient.newDeployResourceCommand().addResourceFromClasspath("BPMN/tempt.bpmn").send().join();

		System.out.println("=========== Successfully Deploy Process ============");

		zeebeClient.newCreateInstanceCommand().bpmnProcessId(processID).latestVersion().send().join();

		System.out.println("============ Successfully Create new Process Instance ==============");

		return "redirect:/";
	}
}
