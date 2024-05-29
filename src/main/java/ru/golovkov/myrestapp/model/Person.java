package ru.golovkov.myrestapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "people")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int age;

    private String email;
}
