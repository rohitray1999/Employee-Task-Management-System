package com.interviewprep.ems.mapper;

import com.interviewprep.ems.dto.response.TaskResponse;
import com.interviewprep.ems.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .employeeId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .employeeName(t.getAssignedTo() != null ? t.getAssignedTo().getFirstName() + " " + t.getAssignedTo().getLastName() : null)
                .build();
    }
}
