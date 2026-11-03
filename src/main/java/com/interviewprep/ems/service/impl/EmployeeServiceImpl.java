package com.interviewprep.ems.service.impl;

import com.interviewprep.ems.dto.request.EmployeeRequest;
import com.interviewprep.ems.dto.response.EmployeeResponse;
import com.interviewprep.ems.entity.Department;
import com.interviewprep.ems.entity.Employee;
import com.interviewprep.ems.exception.DuplicateResourceException;
import com.interviewprep.ems.exception.ResourceNotFoundException;
import com.interviewprep.ems.mapper.EmployeeMapper;
import com.interviewprep.ems.repository.DepartmentRepository;
import com.interviewprep.ems.repository.EmployeeRepository;
import com.interviewprep.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper mapper;

    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Employee already exists with email: " + request.getEmail());
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .build();

        applyDepartmentAndManager(employee, request);

        return mapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> search(String keyword, Pageable pageable) {
        return employeeRepository.search(keyword, pageable).map(mapper::toResponse);
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findOrThrow(id);

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());

        applyDepartmentAndManager(employee, request);

        return mapper.toResponse(employee);
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Employee", id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Employee", id));
    }

    private void applyDepartmentAndManager(Employee employee, EmployeeRequest request) {
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Department", request.getDepartmentId()));
            employee.setDepartment(dept);
        }
        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(employee.getId())) {
                throw new IllegalArgumentException("An employee cannot be their own manager");
            }
            Employee manager = findOrThrow(request.getManagerId());
            employee.setManager(manager);
        }
    }
}
