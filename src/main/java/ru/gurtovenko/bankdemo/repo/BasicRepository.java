package ru.gurtovenko.bankdemo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BasicRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    ID getId(T entity);
}
