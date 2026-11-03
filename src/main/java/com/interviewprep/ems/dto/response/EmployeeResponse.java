package com.interviewprep.ems.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String designation;
    private Double salary;
    private LocalDate joiningDate;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerName;
}
