package com.roommate.admin.service;

import com.roommate.admin.dto.AdminCategoryResponse;
import com.roommate.common.exception.ApiException;
import com.roommate.common.exception.ErrorCode;
import com.roommate.domain.member.entity.HobbyEntity;
import com.roommate.domain.member.entity.PetEntity;
import com.roommate.domain.member.entity.PreferenceEntity;
import com.roommate.domain.member.entity.WorkTypeEntity;
import com.roommate.domain.member.repository.HobbyRepository;
import com.roommate.domain.member.repository.PetRepository;
import com.roommate.domain.member.repository.PreferenceRepository;
import com.roommate.domain.member.repository.WorkTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final WorkTypeRepository workTypeRepository;
    private final HobbyRepository hobbyRepository;
    private final PreferenceRepository preferenceRepository;
    private final PetRepository petRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminCategoryResponse> getCategories(String type) {
        return switch (normalizeType(type)) {
            case "WORK_TYPE" -> workTypeRepository.findAll().stream().map(this::toResponse).toList();
            case "HOBBY" -> hobbyRepository.findAll().stream().map(this::toResponse).toList();
            case "PREFERENCE" -> preferenceRepository.findAll().stream().map(this::toResponse).toList();
            case "PET" -> petRepository.findAll().stream().map(this::toResponse).toList();
            default -> throw new ApiException(ErrorCode.INVALID_REQUEST);
        };
    }

    @Override
    @Transactional
    public AdminCategoryResponse createCategory(String type, String name) {
        String safeType = normalizeType(type);
        String safeName = normalizeName(name);
        return switch (safeType) {
            case "WORK_TYPE" -> {
                workTypeRepository.insert(safeName);
                yield toResponse(workTypeRepository.findByName(safeName).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "HOBBY" -> {
                hobbyRepository.insert(safeName);
                yield toResponse(hobbyRepository.findByName(safeName).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "PREFERENCE" -> {
                preferenceRepository.insert(safeName);
                yield toResponse(preferenceRepository.findByName(safeName).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "PET" -> {
                petRepository.insert(safeName);
                yield toResponse(petRepository.findByName(safeName).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            default -> throw new ApiException(ErrorCode.INVALID_REQUEST);
        };
    }

    @Override
    @Transactional
    public AdminCategoryResponse updateCategory(String type, Long id, String name) {
        String safeType = normalizeType(type);
        String safeName = normalizeName(name);
        if (id == null || id <= 0) throw new ApiException(ErrorCode.INVALID_REQUEST);
        return switch (safeType) {
            case "WORK_TYPE" -> {
                if (workTypeRepository.update(id, safeName) != 1) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
                yield toResponse(workTypeRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "HOBBY" -> {
                if (hobbyRepository.update(id, safeName) != 1) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
                yield toResponse(hobbyRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "PREFERENCE" -> {
                if (preferenceRepository.update(id, safeName) != 1) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
                yield toResponse(preferenceRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            case "PET" -> {
                if (petRepository.update(id, safeName) != 1) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
                yield toResponse(petRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND)));
            }
            default -> throw new ApiException(ErrorCode.INVALID_REQUEST);
        };
    }

    @Override
    @Transactional
    public void deleteCategory(String type, Long id) {
        String safeType = normalizeType(type);
        if (id == null || id <= 0) throw new ApiException(ErrorCode.INVALID_REQUEST);
        int updated = switch (safeType) {
            case "WORK_TYPE" -> workTypeRepository.delete(id);
            case "HOBBY" -> hobbyRepository.delete(id);
            case "PREFERENCE" -> preferenceRepository.delete(id);
            case "PET" -> petRepository.delete(id);
            default -> throw new ApiException(ErrorCode.INVALID_REQUEST);
        };
        if (updated != 1) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
    }

    private String normalizeType(String type) {
        if (type == null) throw new ApiException(ErrorCode.INVALID_REQUEST);
        String normalized = type.trim().toUpperCase();
        if (!List.of("WORK_TYPE", "HOBBY", "PREFERENCE", "PET").contains(normalized)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        return normalized;
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) throw new ApiException(ErrorCode.MISSING_REQUIRED_VALUE);
        return name.trim();
    }

    private AdminCategoryResponse toResponse(WorkTypeEntity entity) {
        return new AdminCategoryResponse("WORK_TYPE", entity.getWorkTypeId(), entity.getWorkTypeName(), entity.getCreatedAt());
    }

    private AdminCategoryResponse toResponse(HobbyEntity entity) {
        return new AdminCategoryResponse("HOBBY", entity.getHobbyId(), entity.getHobbyName(), entity.getCreatedAt());
    }

    private AdminCategoryResponse toResponse(PreferenceEntity entity) {
        return new AdminCategoryResponse("PREFERENCE", entity.getPreferenceId(), entity.getPreferenceName(), entity.getCreatedAt());
    }

    private AdminCategoryResponse toResponse(PetEntity entity) {
        return new AdminCategoryResponse("PET", entity.getPetId(), entity.getPetName(), entity.getCreatedAt());
    }
}
