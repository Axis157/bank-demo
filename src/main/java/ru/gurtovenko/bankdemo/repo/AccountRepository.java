package ru.gurtovenko.bankdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.gurtovenko.bankdemo.model.Account;

public interface AccountRepository extends BasicRepository<Account, Long> {

    Account findAccountByUsername(String username);

    Account findAccountByUsernameAndPassword(String username, String password);
}
