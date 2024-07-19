package ru.golovkov.myrestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.golovkov.myrestapp.model.entity.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);

    List<Person> findAllByNameContainingIgnoreCase(String name);

    void deleteByName(String name);

    boolean existsByName(String name);
}
