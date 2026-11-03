package com.interviewprep.ems.repository;

import com.interviewprep.ems.entity.Task;
import com.interviewprep.ems.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAssignedToId(Long employeeId, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    long countByAssignedToIdAndStatus(Long employeeId, TaskStatus status);
}
