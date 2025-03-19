package com.example.enterprise.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.enterprise.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private String userName;
    private String email;
    private String userId;

    @JsonIgnore
    private String password;
    private boolean isActive;
    private boolean isUserLoggedIn;
    private List<String> roles;

    @JsonIgnore
    private List<String> permission;

    public UserDetailsImpl(String userName, String email, String userId, String password,
                               boolean isActive, boolean isUserLoggedIn, List<String> roles, List<String> permission) {
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.password = password;
        this.isActive = isActive;
        this.isUserLoggedIn = isUserLoggedIn;
        this.roles = roles;
        this.permission = permission;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUserName(),
                user.getEmail(),
                user.getUserId(),
                user.getPassword(),
                user.isActive(),
                user.isUserLoggedIn(),
                user.getRoles(),
                user.getPermission()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        List<GrantedAuthority> permissionsAuthorities = permission.stream()
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
        return userName;
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
        return isActive;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }


    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermission() {
        return permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(email, user.email);
    }
}

