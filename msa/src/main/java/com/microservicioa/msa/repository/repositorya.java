package com.microservicioa.msa.repository;

import com.microservicioa.msa.entity.entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface repositorya extends JpaRepository<entidad, Long> {
}