package ru.gurtovenko.bankdemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.gurtovenko.bankdemo.dto.AccountDto;
import ru.gurtovenko.bankdemo.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper extends ru.gurtovenko.bankdemo.mapper.Mapper<Account, AccountDto> {

    AccountDto toDto(Account entity);
}
