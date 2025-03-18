package com.example.enterprise.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.repository.EnterPriseUserRepository;

@Component
public class EnterpriseUserDetailsService implements UserDetailsService {
    @Autowired
    private EnterPriseUserRepository enterpriseUserRepository; 

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EnterPriseUser enterpriseUser = enterpriseUserRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Enterprise user not found with email: " + email));

        return EnterPriseUserDetailsImpl.build(enterpriseUser);
    }
}
