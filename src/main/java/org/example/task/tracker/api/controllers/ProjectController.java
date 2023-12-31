package org.example.task.tracker.api.controllers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task.tracker.api.controllers.helpers.ControllerHelper;
import org.example.task.tracker.api.dto.AskDto;
import org.example.task.tracker.api.dto.ProjectDto;
import org.example.task.tracker.api.factories.ProjectDtoFactory;
import org.example.task.tracker.exceptions.BadRequestExceptions;
import org.example.task.tracker.exceptions.NotFoundExceptions;
import org.example.task.tracker.store.entities.ProjectEntity;
import org.example.task.tracker.store.repositories.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Класс ProjectController представляет собой контроллер Spring MVC, который обрабатывает HTTP-запросы,
 * связанные с операциями CRUD (создание, чтение, обновление и удаление) для проектов в приложении.
 * Этот контроллер предоставляет API для управления проектами и взаимодействует с базой данных для
 * выполнения операций с проектами.
 *
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.example.task.tracker.store.repositories.ProjectRepository
 * @see org.example.task.tracker.api.dto.ProjectDto
 * @see org.example.task.tracker.exceptions.BadRequestExceptions
 * @see org.example.task.tracker.exceptions.NotFoundExceptions
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    ControllerHelper controllerHelper;

    public static final String FETCH_PROJECT = "/api/projects";
    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";
    public static final String DELETE_PROJECT = "/api/projects/{project_Id}";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_Id}";

    /**
     * Метод для получения списка проектов с возможностью фильтрации по префиксу имени.
     *
     * @param optionalPrefixName Необязательный параметр для фильтрации по префиксу имени.
     * @return Список проектов в формате ProjectDto.
     */
    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());

    }

    /**
     * Метод для создания нового проекта.
     *
     * @param projectName Имя нового проекта.
     * @return Созданный проект в формате ProjectDto.
     * @throws BadRequestExceptions если имя проекта пустое или проект с таким именем уже существует.
     */
    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam("project_name") String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestExceptions("Name can't be empty");
        }

        projectRepository
                .findByName(projectName)
                .ifPresent(project -> {
                    throw new BadRequestExceptions(String.format("Project \"%s\" alredy exists.", projectName));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(projectName)
                        .build()
        );

        return projectDtoFactory.makeProjectDto(project);

    }

    /**
     * Метод для создания или обновления проекта.
     *
     * @param optionalProjectId   Необязательный параметр для идентификации проекта.
     * @param optionalProjectName Необязательный параметр для нового имени проекта.
     * @return Созданный или обновленный проект в формате ProjectDto.
     * @throws BadRequestExceptions если имя проекта пустое или имя уже занято при создании, или если проект
     *                              с указанным идентификатором не найден при обновлении.
     */
    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
            //Another params...
    ) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = optionalProjectId.isEmpty();

        if (isCreate && optionalProjectName.isEmpty()) {
            throw new BadRequestExceptions("Project name can't be empty.");
        }

        final ProjectEntity project = optionalProjectId
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName
                .ifPresent(projectName -> {
                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestExceptions(String.format("Project \"%s\" alredy exists.", projectName)
                                );
                            });
                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(savedProject);
    }

    /**
     * Метод для редактирования имени проекта.
     *
     * @param projectId   Идентификатор проекта, который нужно отредактировать.
     * @param projectName Новое имя проекта.
     * @return Отредактированный проект в формате ProjectDto.
     * @throws BadRequestExceptions если имя проекта пустое или имя уже занято другим проектом.
     * @throws NotFoundExceptions   если проект с указанным идентификатором не найден.
     */
    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_Id") Long projectId,
            @RequestParam("project_name") String projectName
    ) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestExceptions("Name can't be empty");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        projectRepository
                .findByName(projectName)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestExceptions(String.format("Project \"%s\" alredy exists.", projectName));
                });

        project.setName(projectName);


        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);

    }


    /**
     * Метод для удаления проекта по идентификатору.
     *
     * @param projectId Идентификатор проекта, который нужно удалить.
     * @return Объект AskDto с информацией о результате удаления.
     * @throws NotFoundExceptions если проект с указанным идентификатором не найден.
     */
    @DeleteMapping(DELETE_PROJECT)
    public AskDto deleteProject(@PathVariable("project_Id") Long projectId) {

        controllerHelper.getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AskDto.makeDefault(true);
    }
}
