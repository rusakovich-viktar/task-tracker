package org.example.task.tracker.api.factories;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.task.tracker.api.dto.TaskStateDto;
import org.example.task.tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskStateDtoFactory {

    private final TaskDtoFactory taskDtoFactory;

    public TaskStateDto makeTaskStateDto(TaskStateEntity entity) {

        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .leftTaskStateId(entity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(entity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .tasks(
                        entity
                                .getTasks()
                                .stream()
                                .map(taskDtoFactory::makeTaskDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
