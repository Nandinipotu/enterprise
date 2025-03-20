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
    private String userId;
    private List<String> role;
    private List<String> permissions;

    public EnterPriseUserDetailsImpl(String userEmail, String userName, String userId,String password,
            String orgId, List<String> role, List<String> permissions) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userId = userId;
        this.password = password;
        this.orgId = orgId;
        this.role = role;
        this.permissions = permissions;
    }

    public static EnterPriseUserDetailsImpl build(EnterPriseUser user) {
        return new EnterPriseUserDetailsImpl(
                user.getUserEmail(),
                user.getUserName(),
                user.getUserId(),
                user.getPassword(),
                user.getOrgId(),
                user.getRole(),
                user.getPermissions());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = role.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .collect(Collectors.toList());
                
        List<GrantedAuthority> permissionsAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.addAll(permissionsAuthorities);

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
    
    public String getUserId() {
        return userId;
    }
    
    public String getOrgId() {
        return orgId;
    }

    public List<String> getRole() {
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
