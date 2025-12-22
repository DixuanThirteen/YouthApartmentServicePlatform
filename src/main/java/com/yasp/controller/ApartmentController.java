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
    public ResponseEntity<Response<List<Apartment>>> apartmentView(Authentication authentication){
        Response<List<Apartment>> resp = apartmentService.getAllApartments();

        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/search")//搜索公寓
    public ResponseEntity<Response<List<Apartment>>> searchApartments(@RequestBody ApartmentSearchRequest apartment){
        Response<List<Apartment>> resp = apartmentService.searchApartments(apartment);
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        }else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")//根据id获取公寓详情
    public ResponseEntity<Response<Apartment>> oneApartment(@PathVariable Long id){
        Response<Apartment> resp = apartmentService.getApartmentById(id);
        if(resp == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping//创建公寓
    public ResponseEntity<Response<Apartment>> addApartment(
            @RequestBody Apartment apartment,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response<Apartment> resp = apartmentService.addApartment(apartment, username, role);
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

    @GetMapping("/provider/{provider}")
    public ResponseEntity<Response<List<Apartment>>> getApartmentsByProvider(@PathVariable String provider){
        Response<List<Apartment>> resp = apartmentService.getApartmentByProvider(provider);
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        }else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
