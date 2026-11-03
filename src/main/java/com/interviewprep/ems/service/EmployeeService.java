package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.EmployeeRequest;
import com.interviewprep.ems.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponse create(EmployeeRequest request);
    EmployeeResponse getById(Long id);
    Page<EmployeeResponse> search(String keyword, Pageable pageable);
    EmployeeResponse update(Long id, EmployeeRequest request);
    void delete(Long id);
}
