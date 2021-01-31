package com.bbte.styoudent.repository;

import com.bbte.styoudent.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);
    boolean existsByEmail(String email);
}
