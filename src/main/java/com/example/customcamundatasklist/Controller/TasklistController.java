package com.example.customcamundatasklist.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.customcamundatasklist.service.AuthRoleService;
import com.example.customcamundatasklist.service.FormJsonToHtml;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.auth.SelfManagedAuthentication;
import io.camunda.tasklist.dto.Form;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskList;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;

@Controller
@RequestMapping("Tasklist")
public class TasklistController {

	@Autowired
	FormJsonToHtml formJsonToHtml;
	@Autowired
	AuthRoleService authRoleService;

	@GetMapping("")
	public String listAllTasks(Model model) throws TaskListException {
		// auth with tasklist
		SelfManagedAuthentication sma = new SelfManagedAuthentication().clientId("tasklist")
				.clientSecret("XALaRPl5qwTEItdwCMiPS62nVpKs7dL7").keycloakUrl("http://localhost:18080")
				.keycloakRealm("camunda-platform");

		CamundaTaskListClient client = new CamundaTaskListClient.Builder().shouldReturnVariables()
				.taskListUrl("http://localhost:8082/").authentication(sma).build();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String userRole = auth.getAuthorities().iterator().next().getAuthority();
		ArrayList<String> userRoleGroups = authRoleService.getUserRoleGroups(auth);

		System.out.println("00000000000000  -  " + userRoleGroups);

		// get list of all review leave request task from tasklist
		TaskList tasks = client.getTasks(false, TaskState.CREATED, 50);
		TaskList assignedTasks = client.getAssigneeTasks(auth.getName(), TaskState.CREATED, 50);
		TaskList groupTasks = client.getGroupTasks(userRole, TaskState.CREATED, 50);
		TaskList completedAssignedTasks = client.getAssigneeTasks(auth.getName(), TaskState.COMPLETED, 50);
		// TaskList groupTask = client.getGroupTasks(null, null, null);

		model.addAttribute("tasks", tasks);
		model.addAttribute("assignedTasks", assignedTasks);
		model.addAttribute("groupTasks", groupTasks);
		model.addAttribute("completedAssignedTasks", completedAssignedTasks);
		model.addAttribute("userRole", userRole);

		return "tasklist-page";
	}

	@GetMapping("/GetTask")
	public String getTask(@RequestParam("taskID") String taskID, Model model)
			throws JSONException, IOException, TaskListException {

//		String json = Files.readString(Paths.get("src/main/resources/Test-Form1.json"));

//		JsonToHtml jsonToHtml = new JsonToHtml();
//		String htmlSTR = jsonToHtml.getHtml(json);

		// auth with tasklist
		SelfManagedAuthentication sma = new SelfManagedAuthentication().clientId("tasklist")
				.clientSecret("XALaRPl5qwTEItdwCMiPS62nVpKs7dL7").keycloakUrl("http://localhost:18080")
				.keycloakRealm("camunda-platform");

		CamundaTaskListClient client = new CamundaTaskListClient.Builder().shouldReturnVariables()
				.taskListUrl("http://localhost:8082/").authentication(sma).build();

		// get task
		Task task = client.getTask(taskID);

		// get form schema from task
		String htmlSTR = "";
		String formKey = task.getFormKey();
		if (formKey != null) {
			String formId = formKey.substring(formKey.lastIndexOf(":") + 1);
			String processDefinitionId = task.getProcessDefinitionId();

			Form form = client.getForm(formId, processDefinitionId);
			String schema = form.getSchema();

			htmlSTR = formJsonToHtml.getHtml(schema);
		}

		model.addAttribute("taskID", taskID);
		model.addAttribute("formSchema", htmlSTR);

		return "form-template";
		// return "<form action=\"/Tasklist/CompleteTask?taskID=" + taskID + "\"
		// method=\"post\">" + htmlSTR;
	}

	@PostMapping("/test")
	public String test() {
		return "home";
	}

	@PostMapping("/CompleteTask")
	public String handleCompleteTask(@RequestParam Map<String, Object> map) throws JSONException, TaskListException {
		// get taskID and remove from map
		String taskID = (String) map.remove("taskID");

		// remove crsf
		map.remove("_csrf");

		// Auth
		SelfManagedAuthentication sma = new SelfManagedAuthentication().clientId("tasklist")
				.clientSecret("XALaRPl5qwTEItdwCMiPS62nVpKs7dL7").keycloakUrl("http://localhost:18080")
				.keycloakRealm("camunda-platform");

		CamundaTaskListClient client = new CamundaTaskListClient.Builder().shouldReturnVariables()
				.taskListUrl("http://localhost:8082/").authentication(sma).build();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// Claim Task
		client.claim(taskID, auth.getName());

		System.out.println("============ Successfully Create new Process Instance ==============");

		// Finish Task
		client.completeTask(taskID, map);

		System.out.println("============ Successfully Completed Task ==============");

		return "redirect:/Tasklist";
	}
}
