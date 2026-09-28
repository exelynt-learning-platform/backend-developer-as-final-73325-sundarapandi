package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.entity.Resource;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository repository;

    public List<ResourceResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ResourceResponse findById(Long id) {
        return toResponse(get(id));
    }

    public ResourceResponse create(ResourceRequest request) {
        Resource r = Resource.builder()
                .name(request.name()).description(request.description()).type(request.type())
                .price(request.price()).available(request.available()).build();
        return toResponse(repository.save(r));
    }

    public ResourceResponse update(Long id, ResourceRequest request) {
        Resource r = get(id);
        r.setName(request.name());
        r.setDescription(request.description());
        r.setType(request.type());
        r.setPrice(request.price());
        r.setAvailable(request.available());
        return toResponse(repository.save(r));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    public Resource get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
    }

    private ResourceResponse toResponse(Resource r) {
        return new ResourceResponse(r.getId(), r.getName(), r.getDescription(),
                r.getType(), r.getPrice(), r.isAvailable());
    }
}
