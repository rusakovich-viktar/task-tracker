package org.example.task.tracker.api.controllers;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task.tracker.api.dto.ProjectDto;
import org.example.task.tracker.api.factories.ProjectDtoFactory;
import org.example.task.tracker.exceptions.BadRequestExceptions;
import org.example.task.tracker.exceptions.NotFoundExceptions;
import org.example.task.tracker.store.entities.ProjectEntity;
import org.example.task.tracker.store.repositories.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String name) {

        if (name.trim().isEmpty()) {
            throw new BadRequestExceptions("Name can't be empty");
        }

        projectRepository
                .findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestExceptions(String.format("Project \"%s\" alredy exists.", name));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                        .build()
        );

        return projectDtoFactory.makeProjectDto(project);

    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editPatch(
            @PathVariable("project_Id") Long projectId,
            @RequestParam String name
    ) {

        if (name.trim().isEmpty()) {
            throw new BadRequestExceptions("Name can't be empty");
        }

        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundExceptions
                                (String.format(
                                        "Project with id \"%s\" doesn't exists.",
                                        projectId
                                )

                                )
                );

        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestExceptions(String.format("Project \"%s\" alredy exists.", name));
                });

        project.setName(name);


        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);

    }

}
