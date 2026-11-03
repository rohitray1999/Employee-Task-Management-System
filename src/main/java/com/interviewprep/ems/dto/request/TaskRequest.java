package com.interviewprep.ems.dto.request;

import com.interviewprep.ems.enums.TaskPriority;
import com.interviewprep.ems.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate dueDate;

    @NotNull(message = "Task must be assigned to an employee")
    private Long employeeId;
}
