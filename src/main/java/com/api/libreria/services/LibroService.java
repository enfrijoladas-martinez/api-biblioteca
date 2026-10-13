package com.api.libreria.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.libreria.exceptions.BusinessException;
import com.api.libreria.exceptions.NotFoundException;
import com.api.libreria.models.LibroModel;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class LibroService {
    @Autowired
    ILibroRepository libroRepository;

    @Autowired
    IPrestamoRepository prestamoRepository;

    //modificador de acceso, tipo de retorno, nombre del método y parámetros
    public ArrayList<LibroModel> obtenerLibros(){
        return (ArrayList<LibroModel>) libroRepository.findAll();
    }

    public LibroModel guardarLibro(LibroModel libro){
        validar(libro);
        libro.setIsbn(null);
        return libroRepository.save(libro);
    }

    public LibroModel obtenerPorId(Long isbn){
        return libroRepository.findById(isbn)
                .orElseThrow(()-> new NotFoundException("No existe un libro ISBN: "+ isbn));
    }

    public LibroModel actualizarLibro(LibroModel request, Long isbn){
        validar(request);
        LibroModel libro = obtenerPorId(isbn);
        long prestados = prestamoRepository.countByLibroIsbnAndFechaDevolucionIsNull(isbn);
        if (request.getNumeroEjemplares() < prestados) {
            throw new BusinessException("No puede haber menos ejemplares (" + request.getNumeroEjemplares()
                    + ") que préstamos activos (" + prestados + ")");
        }
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setAñoPublicacion(request.getAñoPublicacion());
        libro.setNumeroEjemplares(request.getNumeroEjemplares());

        return libroRepository.save(libro);
    }

    public void eliminarLibro(Long isbn){
        obtenerPorId(isbn);
        if (prestamoRepository.existsByLibroIsbn(isbn)) {
            throw new BusinessException("No se puede eliminar el libro ISBN " + isbn + " porque tiene préstamos registrados");
        }
        libroRepository.deleteById(isbn);
    }

    // Ejemplares que quedan en la biblioteca = total - préstamos activos
    public long ejemplaresDisponibles(Long isbn){
        LibroModel libro = obtenerPorId(isbn);
        return libro.getNumeroEjemplares() - prestamoRepository.countByLibroIsbnAndFechaDevolucionIsNull(isbn);
    }

     // metodo para buscar libros por autor
     public ArrayList<LibroModel>buscarPorAutor(String autor){
        return libroRepository.findByAutor(autor);
    }

     public ArrayList<LibroModel>buscarPorTitulo(String titulo){
        return libroRepository.findByTitulo(titulo);
    }

    private void validar(LibroModel l) {
        if (l.getTitulo() == null || l.getTitulo().isBlank() || l.getAutor() == null || l.getAutor().isBlank()) {
            throw new BusinessException("titulo y autor son obligatorios");
        }
        if (l.getNumeroEjemplares() < 0) {
            throw new BusinessException("numeroEjemplares no puede ser negativo");
        }
    }

}
