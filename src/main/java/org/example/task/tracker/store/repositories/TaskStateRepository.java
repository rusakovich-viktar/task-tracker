package org.example.task.tracker.store.repositories;

import java.util.Optional;
import org.example.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Long> {

    Optional<TaskStateEntity> findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(Long projectId, String taskStateName);


}
