package org.example.task.tracker.api.controllers.helpers;

import lombok.RequiredArgsConstructor;
import org.example.task.tracker.exceptions.NotFoundExceptions;
import org.example.task.tracker.store.entities.ProjectEntity;
import org.example.task.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {

    private final ProjectRepository projectRepository;

    public ProjectEntity getProjectOrThrowException(Long projectId) {
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

}
