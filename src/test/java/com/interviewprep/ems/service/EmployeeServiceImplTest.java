package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.EmployeeRequest;
import com.interviewprep.ems.dto.response.EmployeeResponse;
import com.interviewprep.ems.entity.Employee;
import com.interviewprep.ems.exception.DuplicateResourceException;
import com.interviewprep.ems.exception.ResourceNotFoundException;
import com.interviewprep.ems.mapper.EmployeeMapper;
import com.interviewprep.ems.repository.DepartmentRepository;
import com.interviewprep.ems.repository.EmployeeRepository;
import com.interviewprep.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * Classic service-layer unit test: repository & mapper are mocked so we're
 * testing only EmployeeServiceImpl's own logic (validation, orchestration),
 * not JPA or the database.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeRequest request;
    private Employee employeeEntity;

    @BeforeEach
    void setUp() {
        request = new EmployeeRequest();
        request.setFirstName("Asha");
        request.setLastName("Verma");
        request.setEmail("asha.verma@example.com");
        request.setDesignation("Software Engineer");
        request.setSalary(85000.0);

        employeeEntity = Employee.builder()
                .id(1L)
                .firstName("Asha")
                .lastName("Verma")
                .email("asha.verma@example.com")
                .build();
    }

    @Test
    void create_savesEmployee_whenEmailNotTaken() {
        when(employeeRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeEntity);
        when(mapper.toResponse(employeeEntity)).thenReturn(
                EmployeeResponse.builder().id(1L).firstName("Asha").lastName("Verma").build());

        EmployeeResponse response = employeeService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Asha");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void create_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(employeeRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(request.getEmail());

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getById_throwsResourceNotFoundException_whenEmployeeMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_rejectsSelfAsManager() {
        request.setManagerId(1L);
        employeeEntity.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity));

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("own manager");
    }
}
