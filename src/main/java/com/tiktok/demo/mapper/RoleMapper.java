package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tiktok.demo.dto.request.RoleRequest;
import com.tiktok.demo.dto.response.RoleResponse;
import com.tiktok.demo.entity.Role;

@Mapper(componentModel="spring")
public interface RoleMapper {
    @Mapping(target= "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target= "permissions", ignore= true)
    void updateRole(@MappingTarget Role role, RoleRequest request);
}
