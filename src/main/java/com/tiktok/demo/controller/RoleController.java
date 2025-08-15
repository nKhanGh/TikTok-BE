package com.tiktok.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.RequestMapping;

import com.tiktok.demo.service.RoleService;

import lombok.AccessLevel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.response.RoleResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.request.RoleRequest;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    ApiResponse<List<RoleResponse>> getRoles(){
        return ApiResponse.<List<RoleResponse>>builder()
            .result(roleService.getRoles())
            .build();
    }

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
            .result(roleService.createRole(request))
            .build();
    }

    @GetMapping("/{roleName}")
    ApiResponse<RoleResponse> getRole(@PathVariable String roleName){
        return ApiResponse.<RoleResponse>builder()
            .result(roleService.getRole(roleName))
            .build();
    }

    @PutMapping("/{roleName}")
    ApiResponse<RoleResponse> updateRole(@PathVariable String roleName, @RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
            .result(roleService.updateRole(roleName, request))
            .build();
    }

    @DeleteMapping("/{roleName}")
    ApiResponse<String> deleteRole(@PathVariable String roleName){
        roleService.deleteRole(roleName);
        return ApiResponse.<String>builder()
            .result("Role has been deleted!")
            .build();
    }
    
    
    
}
