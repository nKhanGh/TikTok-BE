package com.tiktok.demo.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.RoleRequest;
import com.tiktok.demo.dto.response.RoleResponse;
import com.tiktok.demo.entity.Role;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.RoleMapper;
import com.tiktok.demo.repository.PermissionRepository;
import com.tiktok.demo.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest request){
        Role role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public RoleResponse updateRole(String roleName, RoleRequest request){
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleMapper.updateRole(role, request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getRoles(){
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public RoleResponse getRole(String roleName){
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        return roleMapper.toRoleResponse(role);
    }

    public void deleteRole(String name){
        roleRepository.deleteById(name);
    }
}
