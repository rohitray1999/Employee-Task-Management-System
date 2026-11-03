package com.interviewprep.ems.service.impl;

import com.interviewprep.ems.dto.request.DepartmentRequest;
import com.interviewprep.ems.dto.response.DepartmentResponse;
import com.interviewprep.ems.entity.Department;
import com.interviewprep.ems.exception.DuplicateResourceException;
import com.interviewprep.ems.exception.ResourceNotFoundException;
import com.interviewprep.ems.mapper.DepartmentMapper;
import com.interviewprep.ems.repository.DepartmentRepository;
import com.interviewprep.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper mapper;

    @Override
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Department already exists: " + request.getName());
        }
        Department dept = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapper.toResponse(departmentRepository.save(dept));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "departments", key = "#id")
    public DepartmentResponse getById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Department", id));
        return mapper.toResponse(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "departments", key = "#id")
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Department", id));
        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        return mapper.toResponse(dept); // dirty checking flushes changes on commit - no explicit save() needed
    }

    @Override
    @CacheEvict(value = "departments", key = "#id")
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Department", id);
        }
        departmentRepository.deleteById(id);
    }
}
