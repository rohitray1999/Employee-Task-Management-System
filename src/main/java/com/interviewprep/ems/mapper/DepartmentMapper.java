package com.interviewprep.ems.mapper;

import com.interviewprep.ems.dto.response.DepartmentResponse;
import com.interviewprep.ems.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department dept) {
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .employeeCount(dept.getEmployees() == null ? 0 : dept.getEmployees().size())
                .build();
    }
}
