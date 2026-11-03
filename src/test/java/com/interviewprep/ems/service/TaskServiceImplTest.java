package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.TaskRequest;
import com.interviewprep.ems.dto.response.TaskResponse;
import com.interviewprep.ems.entity.Employee;
import com.interviewprep.ems.entity.Task;
import com.interviewprep.ems.enums.TaskStatus;
import com.interviewprep.ems.exception.ResourceNotFoundException;
import com.interviewprep.ems.mapper.TaskMapper;
import com.interviewprep.ems.repository.EmployeeRepository;
import com.interviewprep.ems.repository.TaskRepository;
import com.interviewprep.ems.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private TaskMapper mapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void create_defaultsStatusToTodo_whenNotProvided() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Fix login bug");
        request.setEmployeeId(1L);
        // status intentionally left null

        Employee employee = Employee.builder().id(1L).firstName("Asha").build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            return TaskResponse.builder().title(t.getTitle()).status(t.getStatus()).build();
        });

        TaskResponse response = taskService.create(request);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void create_throwsResourceNotFoundException_whenEmployeeMissing() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Orphan task");
        request.setEmployeeId(404L);

        when(employeeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateStatus_changesStatus_whenTaskExists() {
        Task task = Task.builder().id(5L).title("Review PR").status(TaskStatus.TODO).build();
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));
        when(mapper.toResponse(task)).thenReturn(
                TaskResponse.builder().id(5L).status(TaskStatus.IN_PROGRESS).build());

        TaskResponse response = taskService.updateStatus(5L, TaskStatus.IN_PROGRESS);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }
}
