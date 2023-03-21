package com.example.customcamundatasklist.Controller;

import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("/start")
	public String startBPMN(@RequestParam("bpmn-model") String str) {

		// System.out.println(str);

		zeebeClient.newDeployResourceCommand().addResourceFromClasspath("BPMN/Test-Custom-Tasklist-Process.bpmn").send()
				.join();

		System.out.println("=========== Successfully Deploy Process ============");

		zeebeClient.newCreateInstanceCommand().bpmnProcessId("Test-Custom-Tasklist-Process").latestVersion().send()
				.join();

		System.out.println("============ Successfully Create new Process Instance ==============");

		return "redirect:/";
	}
}
