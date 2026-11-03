package com.interviewprep.ems.service;

import com.interviewprep.ems.dto.request.DepartmentRequest;
import com.interviewprep.ems.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse getById(Long id);
    List<DepartmentResponse> getAll();
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
}
