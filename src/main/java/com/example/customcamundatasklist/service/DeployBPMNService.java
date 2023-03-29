package com.example.customcamundatasklist.service;

public interface DeployBPMNService {
	public String getProcessIDFromBPMN(String bpmnStr);

	public String setBPMNExecutableTrue(String bpmnStr);
}
