package com.interviewprep.ems.service.impl;

import com.interviewprep.ems.dto.request.TaskRequest;
import com.interviewprep.ems.dto.response.TaskResponse;
import com.interviewprep.ems.entity.Employee;
import com.interviewprep.ems.entity.Task;
import com.interviewprep.ems.enums.TaskStatus;
import com.interviewprep.ems.exception.ResourceNotFoundException;
import com.interviewprep.ems.mapper.TaskMapper;
import com.interviewprep.ems.repository.EmployeeRepository;
import com.interviewprep.ems.repository.TaskRepository;
import com.interviewprep.ems.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskMapper mapper;

    @Override
    public TaskResponse create(TaskRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> ResourceNotFoundException.of("Employee", request.getEmployeeId()));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .assignedTo(employee)
                .build();

        return mapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Task", id));
        task.setStatus(status);
        return mapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getByEmployee(Long employeeId, Pageable pageable) {
        return taskRepository.findByAssignedToId(employeeId, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable).map(mapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Task", id);
        }
        taskRepository.deleteById(id);
    }
}
