package com.ada.sorvetada.repositories;

import com.ada.sorvetada.entities.Customer;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByActive(boolean active);

    @Query("SELECT c FROM Customer c WHERE c.name ILIKE concat('%', :name, '%') ORDER BY c.name")
    List<Customer> findByName(@Param("name") String name);

    @Modifying
    @Query("UPDATE Customer c SET c.active = :active WHERE c.id = :id")
    void activeUser(@Param("active") boolean active, @Param("id") Long id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();

    Optional<Customer> findById(Long id);
}

