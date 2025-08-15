package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.tiktok.demo.dto.request.PermissionRequest;
import com.tiktok.demo.dto.response.PermissionResponse;
import com.tiktok.demo.entity.Permission;

@Mapper(componentModel="spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    void updatePermission(@MappingTarget Permission permisison, PermissionRequest request);
}
