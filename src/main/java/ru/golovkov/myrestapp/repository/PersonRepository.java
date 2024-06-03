package ru.golovkov.myrestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.golovkov.myrestapp.model.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
