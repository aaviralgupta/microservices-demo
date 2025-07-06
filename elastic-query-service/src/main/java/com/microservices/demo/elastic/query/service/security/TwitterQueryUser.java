package com.microservices.demo.elastic.query.service.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.microservices.demo.elastic.query.service.Constants.NA;

@Builder
@Getter
public class TwitterQueryUser implements UserDetails {

    private String username;
    @Setter
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return NA;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
