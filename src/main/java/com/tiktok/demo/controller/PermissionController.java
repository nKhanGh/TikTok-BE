package com.tiktok.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.RequestMapping;

import com.tiktok.demo.service.PermissionService;

import lombok.AccessLevel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.response.PermissionResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.request.PermissionRequest;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class PermissionController {
    PermissionService permissionService;

    @GetMapping
    ApiResponse<List<PermissionResponse>> getPermissions(){
        return ApiResponse.<List<PermissionResponse>>builder()
            .result(permissionService.getPermissions())
            .build();
    }

    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
            .result(permissionService.createPermission(request))
            .build();
    }

    @GetMapping("/{permissionName}")
    ApiResponse<PermissionResponse> getPermission(@PathVariable String permissionName){
        return ApiResponse.<PermissionResponse>builder()
            .result(permissionService.getPermission(permissionName))
            .build();
    }

    @PutMapping("/{permissionName}")
    ApiResponse<PermissionResponse> updatePermission(@PathVariable String permissionName, @RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
            .result(permissionService.updatePermission(permissionName, request))
            .build();
    }

    @DeleteMapping("/{permissionName}")
    ApiResponse<String> deletePermission(@PathVariable String permissionName){
        permissionService.deletePermission(permissionName);
        return ApiResponse.<String>builder()
            .result("Permission has been deleted!")
            .build();
    }
    
    
    
}
