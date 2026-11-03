package com.interviewprep.ems.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email(message = "Email must be valid")
    private String email;

    private String designation;

    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary;

    private Long departmentId;

    private Long managerId;
}
