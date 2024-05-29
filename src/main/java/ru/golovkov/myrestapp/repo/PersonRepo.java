package ru.golovkov.myrestapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.golovkov.myrestapp.model.Person;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {
}
