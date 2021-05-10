package ru.gurtovenko.bankdemo.mapper;

import ru.gurtovenko.bankdemo.dto.EntityDto;

public interface Mapper<T, DTO extends EntityDto> {

    DTO toDto(T entity);
}
