package com.duoc.recetas.service;

import com.duoc.recetas.repository.UsuarioRepository;
import com.duoc.recetas.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;


@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Asignar roles por defecto
        usuario.setRoles(Set.of("USER"));
        
        return usuarioRepository.save(usuario);
    }
    
    public Optional<Usuario> obtenerUsuario(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
    }
    
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    
}
