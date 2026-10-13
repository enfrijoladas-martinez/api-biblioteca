package com.api.libreria.services;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.libreria.exceptions.BusinessException;
import com.api.libreria.exceptions.NotFoundException;
import com.api.libreria.models.LibroModel;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class PrestamoService {
    @Autowired
    IPrestamoRepository prestamoRepository;

    @Autowired
    LibroService libroService;

    @Autowired
    UsuarioService usuarioService;

    public ArrayList<PrestamoModel> obtenerPrestamos() {
        return (ArrayList<PrestamoModel>) prestamoRepository.findAll();
    }

    public PrestamoModel obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el préstamo con id: " + id));
    }

    // Presta un libro: la fecha de préstamo es la actual y la de devolución queda vacía
    public PrestamoModel prestarLibro(PrestamoModel request) {
        if (request.getLibro() == null || request.getLibro().getIsbn() == null
                || request.getUsuario() == null || request.getUsuario().getId() == null) {
            throw new BusinessException("Envía \"libro\": {\"isbn\": X} y \"usuario\": {\"id\": Y}");
        }
        LibroModel libro = libroService.obtenerPorId(request.getLibro().getIsbn());
        UsuarioModel usuario = usuarioService.obtenerUsuarioPorId(request.getUsuario().getId());

        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new BusinessException("El usuario " + usuario.getId() + " no está activo (estado: " + usuario.getEstado() + ")");
        }
        if (libroService.ejemplaresDisponibles(libro.getIsbn()) <= 0) {
            throw new BusinessException("No quedan ejemplares disponibles de \"" + libro.getTitulo() + "\"");
        }
        if (prestamoRepository.existsByLibroIsbnAndUsuarioIdAndFechaDevolucionIsNull(libro.getIsbn(), usuario.getId())) {
            throw new BusinessException("El usuario " + usuario.getId() + " ya tiene prestado este libro");
        }

        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);
        return prestamoRepository.save(prestamo);
    }

    // Devuelve el libro: se registra la fecha de devolución actual
    public PrestamoModel devolverLibro(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (!prestamo.isActivo()) {
            throw new BusinessException("El préstamo " + id + " ya fue devuelto el " + prestamo.getFechaDevolucion());
        }
        prestamo.setFechaDevolucion(LocalDate.now());
        return prestamoRepository.save(prestamo);
    }

    public void eliminarPrestamo(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (prestamo.isActivo()) {
            throw new BusinessException("No se puede eliminar un préstamo activo; primero registra la devolución");
        }
        prestamoRepository.deleteById(id);
    }

    public ArrayList<PrestamoModel> obtenerActivos() {
        return prestamoRepository.findByFechaDevolucionIsNull();
    }

    public ArrayList<PrestamoModel> buscarPorUsuario(Long usuarioId) {
        usuarioService.obtenerUsuarioPorId(usuarioId);
        return prestamoRepository.findByUsuarioId(usuarioId);
    }

    public ArrayList<PrestamoModel> buscarPorLibro(Long isbn) {
        libroService.obtenerPorId(isbn);
        return prestamoRepository.findByLibroIsbn(isbn);
    }
}
