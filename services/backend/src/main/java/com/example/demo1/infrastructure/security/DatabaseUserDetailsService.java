package com.example.demo1.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo1.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.UserJpaRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    public DatabaseUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (user.getPassword() == null || user.getRole() == null) {
            throw new UsernameNotFoundException("La cuenta no está configurada para autenticación");
        }
        return new AuthenticatedUser(user.getUsername(), user.getPassword(), user.getRole());
    }
}
