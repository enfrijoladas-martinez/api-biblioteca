package com.api.libreria.controllers;

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.services.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public List<UsuarioModel> obtenerUsuarios() {
        return usuarioService.obtenerUsuarios();
    }


    @GetMapping("/{id}")
    public UsuarioModel obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioModel crearUsuario(@RequestBody UsuarioModel usuario) {
        return usuarioService.crearUsuario(usuario);
    }


    @PutMapping("/{id}")
    public UsuarioModel actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioModel usuario) {

        return usuarioService.actualizarUsuario(id, usuario);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}
