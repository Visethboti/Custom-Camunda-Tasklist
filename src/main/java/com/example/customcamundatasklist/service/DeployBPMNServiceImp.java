package com.example.customcamundatasklist.service;

import org.springframework.stereotype.Service;

@Service
public class DeployBPMNServiceImp implements DeployBPMNService {

	@Override
	public String getProcessIDFromBPMN(String bpmnStr) {
		// get bpmn process id from str
		int startOfHead = bpmnStr.indexOf("<bpmn:process id="); // 18
		int startIndex = startOfHead + 18; // starting index of process id in str
		if (startOfHead == -1) {
			startOfHead = bpmnStr.indexOf("<bpmn2:process id=");
			startIndex = startOfHead + 19;
		}

		// System.out.println("***************************" + startIndex);
		// int endIndex = str.indexOf("\" isExecutable="); // ending index of process id
		// in str
		int endIndex = bpmnStr.indexOf("\"", startIndex);

		// System.out.println("***************************" + endIndex);
		String processID = bpmnStr.substring(startIndex, endIndex);
		return processID;
	}

	@Override
	public String setBPMNExecutableTrue(String bpmnStr) {

		String result = bpmnStr.replaceFirst("isExecutable=\"false\">", "isExecutable=\"true\">");
		return result;
	}

}
