package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.service.ResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources")
public class ResourceController {
    private final ResourceService service;

    @GetMapping
    public List<ResourceResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResourceResponse findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(@Valid @RequestBody ResourceRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
