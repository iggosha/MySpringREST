package ru.golovkov.myrestapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "people")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Integer age;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDate registrationDate;

    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    @JsonIgnore
    List<Message> receivedMessages;
}
