package com.yasp.controller;

import com.yasp.dto.Response;
import com.yasp.entity.LeaseContract;
import com.yasp.service.LeaseContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lease-contract")
public class LeaseContractController {

    @Autowired
    private LeaseContractService leaseContractService;


    @PostMapping("/{bookingId}")
    public ResponseEntity<Response<LeaseContract>> createLeaseContract(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String username = authentication.getName();
        Response<LeaseContract> resp = leaseContractService.createLeaseContract(bookingId, username);
        if(resp.getCode() == 400){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
