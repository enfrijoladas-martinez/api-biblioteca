package com.api.libreria.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.libreria.models.PrestamoModel;

@Repository
public interface IPrestamoRepository extends JpaRepository<PrestamoModel, Long> {

    ArrayList<PrestamoModel> findByFechaDevolucionIsNull();

    ArrayList<PrestamoModel> findByUsuarioId(Long usuarioId);

    ArrayList<PrestamoModel> findByLibroIsbn(Long isbn);

    // Préstamos activos de un libro (para saber cuántos ejemplares quedan)
    long countByLibroIsbnAndFechaDevolucionIsNull(Long isbn);

    boolean existsByLibroIsbnAndUsuarioIdAndFechaDevolucionIsNull(Long isbn, Long usuarioId);

    boolean existsByLibroIsbn(Long isbn);

    boolean existsByUsuarioId(Long usuarioId);
}
