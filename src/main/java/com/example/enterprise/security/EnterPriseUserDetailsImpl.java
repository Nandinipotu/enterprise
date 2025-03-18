package com.example.enterprise.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.enterprise.entity.EnterPriseUser;

public class EnterPriseUserDetailsImpl implements UserDetails {
    private String userEmail;
    private String userName;
    private String password;
    private String orgId;
    private String role;
    private List<String> permissions;

    private final Collection<? extends GrantedAuthority> authorities;

    public EnterPriseUserDetailsImpl(String userEmail, String userName, String password,
                           String orgId, String role, List<String> permissions) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
        this.orgId = orgId;
        this.role = role;
        this.permissions = permissions;

        this.authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        ((List<GrantedAuthority>) this.authorities).add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    public static EnterPriseUserDetailsImpl build(EnterPriseUser user) {
        return new EnterPriseUserDetailsImpl(
                user.getUserEmail(),
                user.getUserName(),
                user.getPassword(),
                user.getOrgId(),
                user.getRole(),
                user.getPermissions()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getRole() {
        return role;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
