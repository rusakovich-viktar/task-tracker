package org.example.task.tracker.store.repositories;

import java.util.Optional;
import org.example.task.tracker.store.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByName(String name);
}
