package com.project2.loginsystem.repository;

import com.project2.loginsystem.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRespository extends JpaRepository<Roles, Long> {

    Roles findByRoleNames(String roleNames);
}

