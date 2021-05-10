package ru.gurtovenko.bankdemo.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gurtovenko.bankdemo.mapper.AccountMapper;
import ru.gurtovenko.bankdemo.model.Account;
import ru.gurtovenko.bankdemo.repo.AccountRepository;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractCrudController<Account, Long, AccountRepository> {

    @Autowired
    public AccountController(AccountRepository repository, AccountMapper accountMapper) {
        super(repository, accountMapper);
    }
}
