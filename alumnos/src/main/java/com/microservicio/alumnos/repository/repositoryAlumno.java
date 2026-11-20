package com.microservicio.alumnos.repository;

import com.microservicio.alumnos.entity.EntityAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface repositoryAlumno extends JpaRepository<EntityAlumno, Long> {}
