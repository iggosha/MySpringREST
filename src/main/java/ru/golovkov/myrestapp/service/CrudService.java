package ru.golovkov.myrestapp.service;

import java.util.List;

public interface CrudService<R1, R2> {

    R2 create(R1 requestDto);

    R2 getById(Long id);

    List<R2> getAll();

    R2 updateById(R1 requestDto, Long id);

    void deleteById(Long id);
}
