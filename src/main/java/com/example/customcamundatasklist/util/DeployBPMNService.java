package com.example.customcamundatasklist.util;

public interface DeployBPMNService {
	public String getProcessIDFromBPMN(String bpmnStr);

	public String setBPMNExecutableTrue(String bpmnStr);
}
