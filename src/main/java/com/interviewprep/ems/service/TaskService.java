package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.TaskRequest;
import com.interviewprep.ems.dto.response.TaskResponse;
import com.interviewprep.ems.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse create(TaskRequest request);
    TaskResponse updateStatus(Long id, TaskStatus status);
    Page<TaskResponse> getByEmployee(Long employeeId, Pageable pageable);
    Page<TaskResponse> getByStatus(TaskStatus status, Pageable pageable);
    void delete(Long id);
}
