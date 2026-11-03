package com.interviewprep.ems.dto.response;

import com.interviewprep.ems.enums.TaskPriority;
import com.interviewprep.ems.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long employeeId;
    private String employeeName;
}
