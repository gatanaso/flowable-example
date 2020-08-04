package com.riimu.flowable.controller;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicWorkflowController {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@GetMapping("/dynamic/start/{key}")
	public String startProcess(@PathVariable String key) {
		runtimeService.startProcessInstanceByKey(key);

		return "Custom process started. Number of currently running process instances = " + runtimeService.createProcessInstanceQuery()
				.count();
	}

	@GetMapping("/dynamic/complete/task")
	public void completeRandomTask() {
		// get a random task
		Task task = taskService.createTaskQuery().active().singleResult();
		taskService.complete(task.getId());
	}

	@GetMapping("/dynamic/process/{id}")
	public String createProcess(@PathVariable String id) {
		BpmnModel model = new BpmnModel();
		Process process = new Process();
		model.addProcess(process);
		process.setId("my-process-" + id);

		process.addFlowElement(createStartEvent());
		process.addFlowElement(createUserTask("task1", "First task"));
		process.addFlowElement(createEndEvent());

		process.addFlowElement(createSequenceFlow("start", "task1"));
		process.addFlowElement(createSequenceFlow("task1", "end"));

		// deploy the process deployment
		Deployment deployment = repositoryService.createDeployment()
				.addBpmnModel("dynamic-model.bpmn20.xml", model)
				.name("Dynamic process deployment")
				.deploy();

		return "Deployed " + id + " with ID: " + deployment.getId();
	}

	protected UserTask createUserTask(String id, String name) {
		UserTask userTask = new UserTask();
		userTask.setName(name);
		userTask.setId(id);
		return userTask;
	}

	protected SequenceFlow createSequenceFlow(String from, String to) {
		SequenceFlow flow = new SequenceFlow();
		flow.setSourceRef(from);
		flow.setTargetRef(to);
		return flow;
	}

	protected StartEvent createStartEvent() {
		StartEvent startEvent = new StartEvent();
		startEvent.setId("start");
		return startEvent;
	}

	protected EndEvent createEndEvent() {
		EndEvent endEvent = new EndEvent();
		endEvent.setId("end");
		return endEvent;
	}
}
