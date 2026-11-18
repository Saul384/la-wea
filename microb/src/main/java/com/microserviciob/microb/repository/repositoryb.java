package com.microserviciob.microb.repository;

import com.microserviciob.microb.entity.EntityB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface repositoryb extends JpaRepository<EntityB, Long> {
}