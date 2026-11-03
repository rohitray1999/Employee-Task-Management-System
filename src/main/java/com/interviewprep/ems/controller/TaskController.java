package com.interviewprep.ems.controller;

import com.interviewprep.ems.dto.request.TaskRequest;
import com.interviewprep.ems.dto.response.TaskResponse;
import com.interviewprep.ems.enums.TaskStatus;
import com.interviewprep.ems.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<TaskResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.getByEmployee(employeeId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getByStatus(
            @RequestParam TaskStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taskService.getByStatus(status, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
