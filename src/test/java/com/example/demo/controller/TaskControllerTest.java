package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskRepository taskRepository;

	@Test
	@DisplayName("タスク一覧の表示")
	void index() throws Exception {
		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("タスク一覧")));
	}

	@Test
	@DisplayName("タスクの詳細画面の表示")
	void show() throws Exception {
		Task savedTask = taskRepository.save(new Task("髪を切る", "13時@新宿"));

		mockMvc.perform(get("/tasks/" + savedTask.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("髪を切る")))
				.andExpect(content().string(containsString("13時@新宿")));
	}

	@DisplayName("タスク新規作成")
	@Nested
	class NewTask {

		@Test
		@DisplayName("タスクの新規画面の表示")
		void newTask() throws Exception {
			mockMvc.perform(get("/tasks/new"))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("タスク新規作成")));
		}

		@Test
		@DisplayName("タスクの新規作成が成功する")
		void createTask() throws Exception {
			mockMvc.perform(post("/tasks/create")
					.param("name", "洗濯")
					.param("explanation", "こどもたちの服の洗濯"))
					.andExpect(status().is3xxRedirection());

			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("洗濯")))
					.andExpect(content().string(containsString("こどもたちの服の洗濯")));
		}

		@Test
		@DisplayName("名前のパラメータが無いときに、タスクの新規作成が失敗する")
		void failToCreateTaskWithNoName() throws Exception {
			mockMvc.perform(post("/tasks/create")
					.param("name", "")
					.param("explanation", "大量の服の洗濯"));

			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(not(containsString("大量の服の洗濯"))));
		}

		@Test
		@DisplayName("概要のパラメータが無いときに、タスクの新規作成が失敗する")
		void failToCreateTaskWithNoExplanation() throws Exception {
			mockMvc.perform(post("/tasks/create")
					.param("name", "洗濯")
					.param("explanation", ""));

			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(not(containsString("洗濯"))));
		}
	}

	@DisplayName("タスクの編集")
	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	@Transactional
	class EditTask {

		private Task savedTask;

		@BeforeEach
		void setupEach() {
			savedTask = taskRepository.save(new Task("掃除", "部屋の大掃除をします"));
		}

		@Test
		@DisplayName("タスクの編集画面の表示")
		void editTask() throws Exception {
			mockMvc.perform(get("/tasks/" + savedTask.getId() + "/edit"))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("掃除")))
					.andExpect(content().string(containsString("部屋の大掃除をします")));
		}

		@Test
		@DisplayName("タスクの編集が成功する")
		void updateTask() throws Exception {
			mockMvc.perform(post("/tasks/" + savedTask.getId() + "/update")
					.param("name", "料理")
					.param("explanation", "今日の晩御飯を作ります"))
					.andExpect(status().is3xxRedirection());

			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("料理")))
					.andExpect(content().string(containsString("今日の晩御飯を作ります")))
					.andExpect(content().string(not(containsString("掃除"))))
					.andExpect(content().string(not(containsString("部屋の大掃除をします"))));
		}

		@Test
		@DisplayName("名前のパラメータが無いときに、タスクの編集が失敗する")
		void failToUpdateTaskWithNoName() throws Exception {
			mockMvc.perform(post("/tasks/" + savedTask.getId() + "/update")
					.param("name", "")
					.param("explanation", "リハビリに行く"));

			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(not(containsString("リハビリに行く"))));
		}

		@Test
		@DisplayName("概要のパラメータが無いときに、タスクの編集が失敗する")
		void failToUpdateTaskWithNoExplanation() throws Exception {
			mockMvc.perform(post("/tasks/" + savedTask.getId() + "/update")
					.param("name", "料理")
					.param("explanation", ""));

			Task task = taskRepository.findById(savedTask.getId()).get();
			System.out.println(task.getName());
			mockMvc.perform(get("/tasks"))
					.andExpect(status().isOk())
					.andExpect(content().string(not(containsString("料理"))));
		}
	}

	@Test
	@DisplayName("タスクの削除")
	void destroyTask() throws Exception {
		Task savedTask = taskRepository.save(new Task("掃除", "部屋の大掃除をします"));

		mockMvc.perform(post("/tasks/" + savedTask.getId() + "/destroy"))
				.andExpect(status().is3xxRedirection()); // リダイレクトを期待する

		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("掃除"))))
				.andExpect(content().string(not(containsString("部屋の大掃除をします"))));
	}
}
