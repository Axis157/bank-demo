package ru.gurtovenko.bankdemo.mapper;

import org.mapstruct.Mapper;
import ru.gurtovenko.bankdemo.dto.AccountDto;
import ru.gurtovenko.bankdemo.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper extends ru.gurtovenko.bankdemo.mapper.Mapper<Account, AccountDto> {

}
