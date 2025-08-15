package com.tiktok.demo.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.PermissionRequest;
import com.tiktok.demo.dto.response.PermissionResponse;
import com.tiktok.demo.entity.Permission;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.PermissionMapper;
import com.tiktok.demo.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public PermissionResponse updatePermission(String name, PermissionRequest request){
        Permission permission = permissionRepository.findById(name)
            .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        permissionMapper.updatePermission(permission, request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getPermissions(){
        return permissionRepository.findAll().stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public PermissionResponse getPermission(String name){
        Permission permission = permissionRepository.findById(name)
            .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        return permissionMapper.toPermissionResponse(permission);
    }

    public void deletePermission(String name){
        permissionRepository.deleteById(name);
    }
}
