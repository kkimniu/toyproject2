package com.roommate.admin.service;

import com.roommate.admin.dto.AdminCategoryResponse;

import java.util.List;

public interface AdminCategoryService {
    List<AdminCategoryResponse> getCategories(String type);
    AdminCategoryResponse createCategory(String type, String name);
    AdminCategoryResponse updateCategory(String type, Long id, String name);
    void deleteCategory(String type, Long id);
}
