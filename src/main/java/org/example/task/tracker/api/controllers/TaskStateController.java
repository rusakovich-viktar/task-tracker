package org.example.task.tracker.api.controllers;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task.tracker.api.controllers.helpers.ControllerHelper;
import org.example.task.tracker.api.dto.TaskStateDto;
import org.example.task.tracker.api.factories.TaskStateDtoFactory;
import org.example.task.tracker.exceptions.BadRequestExceptions;
import org.example.task.tracker.store.entities.ProjectEntity;
import org.example.task.tracker.store.entities.TaskStateEntity;
import org.example.task.tracker.store.repositories.TaskStateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
@RestController
public class TaskStateController {

    TaskStateRepository taskStateRepository;
    TaskStateDtoFactory taskStateDtoFactory;
    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/api/projects/{project_id}/tasks-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{project_id}/task-states";
    public static final String DELETE_PROJECT = "/api/projects/{project_Id}";

    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());

    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.isBlank()) {
            throw new BadRequestExceptions("Task state name can't be empty.");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        //Проверяем чтобы не было такого же названия в рамках проекта
        project
                .getTaskStates()
                .stream()
                .map(TaskStateEntity::getName)
                .filter(anotherTaskStateName -> anotherTaskStateName.equalsIgnoreCase(taskStateName))
                .findAny()
                .ifPresent(it -> {
                    throw new BadRequestExceptions(String.format("Task state name \"%s\" already exist.", taskStateName));
                });

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity
                        .builder()
                        .name(taskStateName)
                        .build()
        );

        return new TaskStateDto();
    }


}
