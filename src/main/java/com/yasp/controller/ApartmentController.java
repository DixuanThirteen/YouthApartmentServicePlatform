package com.yasp.controller;

import com.yasp.dto.ApartmentSearchRequest;
import com.yasp.dto.Response;
import com.yasp.entity.Apartment;
import com.yasp.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;


    @GetMapping//获取所有公寓
    public ResponseEntity<List<Apartment>> apartmentView(Authentication authentication){
        List<Apartment> resp = apartmentService.getAllApartments();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/search")//搜索公寓
    public ResponseEntity<List<Apartment>> searchApartments(@RequestBody ApartmentSearchRequest apartment){
        List<Apartment> resp = apartmentService.searchApartments(apartment);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")//根据id获取公寓详情
    public ResponseEntity<Apartment> oneApartment(@PathVariable Long id){
        Apartment apartment = apartmentService.getApartmentById(id);
        if(apartment == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(apartment);
    }

    @PostMapping//创建公寓
    public ResponseEntity<Response> addApartment(
            @RequestBody Apartment apartment,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response resp = apartmentService.addApartment(apartment, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return  ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")//更新公寓信息
    public ResponseEntity<Response<Apartment>> updateApartment(
            @PathVariable Long id,
            @RequestBody Apartment apartment,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response<Apartment> resp = apartmentService.updateApartment(id, apartment, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
