package com.api.libreria.controllers;

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

import com.api.libreria.models.LibroModel;
import com.api.libreria.services.LibroService;

import java.util.ArrayList;



@RestController
@RequestMapping("/libros")
public class LibroController {
    @Autowired
    private LibroService libroService;

    @GetMapping
    public ArrayList<LibroModel> obtenerLibros(){
        return this.libroService.obtenerLibros();
    }

    @GetMapping(path = "/{isbn}")
    public LibroModel obtenerPorId(@PathVariable("isbn") Long isbn){
        return this.libroService.obtenerPorId(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroModel guardarLibro(@RequestBody LibroModel libro){
        return this.libroService.guardarLibro(libro);
    }

    @PutMapping(path = "/{isbn}")
    public LibroModel actualizarLibro(@RequestBody LibroModel request, @PathVariable("isbn") Long isnb){
        return this.libroService.actualizarLibro(request, isnb);

    }


    @DeleteMapping(path= "/{isbn}")
    public String eliminarPorId(@PathVariable("isbn") Long isbn){
        this.libroService.eliminarLibro(isbn);
        return "se elimino el  libro ISBN: "+ isbn;
    }

    @GetMapping(path = "/{isbn}/disponibles")
    public long ejemplaresDisponibles(@PathVariable("isbn") Long isbn){
        return this.libroService.ejemplaresDisponibles(isbn);
    }

    @GetMapping(path = "/autor/{autor}")
    public ArrayList<LibroModel>buscarPorAutor(@PathVariable("autor") String autor){
        return this.libroService.buscarPorAutor(autor);
    }

    @GetMapping(path = "/titulo/{titulo}")
    public ArrayList<LibroModel> buscarPorTitulo(@PathVariable("titulo") String titulo){
        return this.libroService.buscarPorTitulo(titulo);
    }

}
