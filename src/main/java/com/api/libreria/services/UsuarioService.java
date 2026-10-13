package com.api.libreria.services;

import com.api.libreria.exceptions.BusinessException;
import com.api.libreria.exceptions.NotFoundException;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.repositories.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final IPrestamoRepository prestamoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, IPrestamoRepository prestamoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
    }


    public List<UsuarioModel> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }


    public UsuarioModel obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id: " + id));
    }


    public UsuarioModel crearUsuario(UsuarioModel usuario) {
        validar(usuario);
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new BusinessException("Ya existe un usuario con el correo " + usuario.getCorreo());
        }
        usuario.setId(null);
        // Valores por defecto que asigna el servidor
        if (usuario.getFechaCreacion() == null || usuario.getFechaCreacion().isBlank()) {
            usuario.setFechaCreacion(LocalDate.now().toString());
        }
        if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
            usuario.setEstado("Activo");
        }
        return usuarioRepository.save(usuario);
    }


    public UsuarioModel actualizarUsuario(Long id, UsuarioModel request) {
        validar(request);
        UsuarioModel usuario = obtenerUsuarioPorId(id);
        if (usuarioRepository.existsByCorreoAndIdNot(request.getCorreo(), id)) {
            throw new BusinessException("Ya existe otro usuario con el correo " + request.getCorreo());
        }
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(request.getContrasena());
        usuario.setRol(request.getRol());
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            usuario.setEstado(request.getEstado());
        }
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        return usuarioRepository.save(usuario);
    }


    public void eliminarUsuario(Long id) {
        obtenerUsuarioPorId(id);
        if (prestamoRepository.existsByUsuarioId(id)) {
            throw new BusinessException("No se puede eliminar el usuario " + id + " porque tiene préstamos registrados");
        }
        usuarioRepository.deleteById(id);
    }

    private void validar(UsuarioModel u) {
        if (u.getNombre() == null || u.getNombre().isBlank() || u.getApellidos() == null || u.getApellidos().isBlank()) {
            throw new BusinessException("nombre y apellidos son obligatorios");
        }
        if (u.getCorreo() == null || !u.getCorreo().contains("@")) {
            throw new BusinessException("correo no válido");
        }
        if (u.getContrasena() == null || u.getContrasena().length() < 6) {
            throw new BusinessException("contrasena debe tener al menos 6 caracteres");
        }
    }
}
