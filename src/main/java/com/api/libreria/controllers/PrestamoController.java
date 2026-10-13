package com.api.libreria.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.libreria.models.PrestamoModel;
import com.api.libreria.services.PrestamoService;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {
    @Autowired
    private PrestamoService prestamoService;

    @GetMapping
    public ArrayList<PrestamoModel> obtenerPrestamos() {
        return this.prestamoService.obtenerPrestamos();
    }

    @GetMapping(path = "/{id}")
    public PrestamoModel obtenerPorId(@PathVariable("id") Long id) {
        return this.prestamoService.obtenerPorId(id);
    }

    // Presta un libro a un usuario (fecha de préstamo = hoy)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoModel prestarLibro(@RequestBody PrestamoModel prestamo) {
        return this.prestamoService.prestarLibro(prestamo);
    }

    // Registra la devolución (fecha de devolución = hoy)
    @PutMapping(path = "/{id}/devolver")
    public PrestamoModel devolverLibro(@PathVariable("id") Long id) {
        return this.prestamoService.devolverLibro(id);
    }

    @DeleteMapping(path = "/{id}")
    public String eliminarPorId(@PathVariable("id") Long id) {
        this.prestamoService.eliminarPrestamo(id);
        return "Se eliminó el préstamo con id: " + id;
    }

    @GetMapping(path = "/activos")
    public ArrayList<PrestamoModel> obtenerActivos() {
        return this.prestamoService.obtenerActivos();
    }

    @GetMapping(path = "/usuario/{usuarioId}")
    public ArrayList<PrestamoModel> buscarPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        return this.prestamoService.buscarPorUsuario(usuarioId);
    }

    @GetMapping(path = "/libro/{isbn}")
    public ArrayList<PrestamoModel> buscarPorLibro(@PathVariable("isbn") Long isbn) {
        return this.prestamoService.buscarPorLibro(isbn);
    }
}
