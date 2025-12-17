package com.yasp.service;

import com.yasp.dto.ApartmentSearchRequest;
import com.yasp.dto.Response;
import com.yasp.entity.Apartment;
import com.yasp.entity.Provider;
import com.yasp.entity.ProviderAccount;
import com.yasp.mapper.ApartmentMapper;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.ProviderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yasp.dto.LoginResponse.Role.PROVIDER_Admin;
import static com.yasp.dto.LoginResponse.Role.PROVIDER_Staff;

@Service
public class ApartmentService {
    @Autowired
    ApartmentMapper apartmentMapper;
    @Autowired
    private ProviderAccountMapper providerAccountMapper;
    @Autowired
    private ProviderMapper providerMapper;

    public Response addApartment(Apartment apartment, String username, String role) {
        Response resp = new Response();
        Response.userProfile profile = new Response.userProfile();

        ProviderAccount user = providerAccountMapper.selectProviderAccountByUsername(username);

        Provider provider = providerMapper.selectProviderById(user.getProviderId());
        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");
            profile.setUsername(username);
            profile.setRole(role);
            resp.setProfile(profile);

            return resp;
        }

        apartment.setProviderId(provider.getId());

        apartmentMapper.insert(apartment);

        resp.setCode(200);
        resp.setMessage("success");

        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());

        resp.setProfile(profile);

        return  resp;

    }

    public List<Apartment> getAllApartments() {
        List<Apartment> apartments = apartmentMapper.selectAll();
        return apartments;
    }

    public List<Apartment> searchApartments(ApartmentSearchRequest apartment) {
        return apartmentMapper.searchApartments(apartment);
    }

}
