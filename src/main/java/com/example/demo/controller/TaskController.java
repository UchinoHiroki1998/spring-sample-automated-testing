package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Controller
public class TaskController {

	@Autowired
	TaskRepository taskRepository;

	@GetMapping("/tasks")
	public String index(Model model) {
		model.addAttribute("tasks", taskRepository.findAll());
		return "tasks/index";
	}

	@GetMapping("/tasks/{taskId}")
	public String show(
			@PathVariable("taskId") Integer taskId,
			Model model) {
		model.addAttribute("task", taskRepository.findById(taskId).get());
		return "tasks/show";
	}

	@GetMapping("/tasks/new")
	public String newForm(Model model) {
		model.addAttribute("task", new Task());
		return "tasks/new";
	}

	@PostMapping("/tasks/create")
	public String create(
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "explanation", defaultValue = "") String explanation,
			Model model,
			RedirectAttributes redirectAttributes) {

		Task task = new Task(name, explanation);
		model.addAttribute("task", task);

		List<String> errorMessages = new ArrayList<String>();
		if (name.equals("")) {
			errorMessages.add("名前を入力してください");
		}
		if (explanation.equals("")) {
			errorMessages.add("概要を入力してください");
		}
		if (errorMessages.size() > 0) {
			model.addAttribute("errorMessages", errorMessages);
			return "tasks/new";
		}

		taskRepository.save(task);
		redirectAttributes.addFlashAttribute("successMessage", "タスクを作成しました");
		return "redirect:/tasks";
	}

	@GetMapping("/tasks/{taskId}/edit")
	public String editForm(
			@PathVariable("taskId") Integer taskId,
			Model model) {
		model.addAttribute("task", taskRepository.findById(taskId).get());
		return "tasks/edit";
	}

	@PostMapping("/tasks/{taskId}/update")
	public String update(
			@PathVariable("taskId") Integer taskId,
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "explanation", defaultValue = "") String explanation,
			Model model,
			RedirectAttributes redirectAttributes) {

		List<String> errorMessages = new ArrayList<String>();
		if (name.equals("")) {
			errorMessages.add("名前を入力してください");
		}
		if (explanation.equals("")) {
			errorMessages.add("概要を入力してください");
		}
		if (errorMessages.size() > 0) {
			model.addAttribute("errorMessages", errorMessages);
			model.addAttribute("task", new Task(name, explanation));
			return "tasks/edit";
		}

		Task task = taskRepository.findById(taskId).get();
		task.setName(name);
		task.setExplanation(explanation);
		taskRepository.save(task);

		redirectAttributes.addFlashAttribute("successMessage", "タスクを編集しました");
		return "redirect:/tasks";
	}

	@PostMapping("/tasks/{taskId}/destroy")
	public String destroy(
			@PathVariable("taskId") Integer taskId,
			RedirectAttributes redirectAttributes) {
		taskRepository.deleteById(taskId);
		redirectAttributes.addFlashAttribute("successMessage", "タスクを削除しました");
		return "redirect:/tasks";
	}

}
