package ru.golovkov.myrestapp.service;

import java.util.List;

public interface CrudService<R1, R2> {

    void create(R1 requestDto);

    R2 getById(Long id);

    List<R2> getAll();

    void updateById(R1 reqDto, Long id);

    void deleteById(Long id);
}
