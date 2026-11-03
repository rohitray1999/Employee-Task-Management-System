package com.interviewprep.ems.mapper;

import com.interviewprep.ems.dto.response.EmployeeResponse;
import com.interviewprep.ems.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .designation(e.getDesignation())
                .salary(e.getSalary())
                .joiningDate(e.getJoiningDate())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .managerId(e.getManager() != null ? e.getManager().getId() : null)
                .managerName(e.getManager() != null ? e.getManager().getFirstName() + " " + e.getManager().getLastName() : null)
                .build();
    }
}
