package com.riimu.flowable.controller;

import java.util.List;

import com.riimu.flowable.domain.Approval;
import com.riimu.flowable.domain.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.riimu.flowable.service.ArticleWorkflowService;

@RestController
public class ArticleWorkflowController {
	@Autowired
	private ArticleWorkflowService service;

	@PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void submit(@RequestBody Article article) {
		service.startProcess(article);
	}

	@GetMapping("/tasks")
	public List<Article> getTasks() {
		return service.getTasks();
	}

	@PostMapping(value = "/review", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void review(@RequestBody Approval approval) {
		service.submitReview(approval);
	}
}
