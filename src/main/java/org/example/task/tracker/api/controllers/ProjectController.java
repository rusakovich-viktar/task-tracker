package org.example.task.tracker.api.controllers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    public static final String FETCH_PROJECT = "/api/projects";
    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";
    public static final String DELETE_PROJECT = "/api/projects/{project_Id}";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_Id}";

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
                .map(this::getProjectOrThrowException)
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


    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_Id") Long projectId,
            @RequestParam("project_name") String projectName
    ) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestExceptions("Name can't be empty");
        }

        ProjectEntity project = getProjectOrThrowException(projectId);

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

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundExceptions
                                (String.format(
                                        "Project with id \"%s\" doesn't exists.",
                                        projectId
                                )

                                )
                );
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDto deleteProject(@PathVariable("project_Id") Long projectId) {

        getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AskDto.makeDefault(true);
    }
}
