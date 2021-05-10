package ru.gurtovenko.bankdemo.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gurtovenko.bankdemo.repo.BasicRepository;
import ru.gurtovenko.bankdemo.service.response.PagingListResponse;
import ru.gurtovenko.bankdemo.util.OLPageRequest;
import ru.gurtovenko.bankdemo.util.SortParser;
import ru.gurtovenko.bankdemo.util.SpecificationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class AbstractCrudController<T, ID, R extends BasicRepository<T, ID>> {
    private final static Logger logger = LogManager.getLogger(AbstractCrudController.class);

    protected final Class<T> entityClass;
    protected final R repository;

    @Autowired
    public AbstractCrudController(Class<T> entityClass,
                                  R repository) {
        this.entityClass = entityClass;
        this.repository = repository;
    }

    @GetMapping(value = "")
    public PagingListResponse<T> list(@RequestParam(name = "offset", required = false) Long offset,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "sort", required = false) List<String> sorters,
                                      @RequestParam(name = "filter", required = false) List<String> filters) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return findInRepository(offset,
                limit,
                createSpecification(filters, authentication),
                SortParser.parse(sorters));
    }

    protected Specification<T> createSpecification(List<String> filters, Authentication authentication) {
        return SpecificationUtil.createSpecification(filters, entityClass, authentication);
    }

    protected PagingListResponse<T> findInRepository(Long offset,
                                                     Integer limit,
                                                     Specification<T> specification,
                                                     Sort sort) {
        Page<T> pageResult;

        if (offset == null || offset < 0) {
            offset = (long) 0;
        }

        if (limit == null || limit < 0) {
            limit = 25;
        }

        PagingListResponse<T> ret = new PagingListResponse<>();

        pageResult = repository.findAll(specification, new OLPageRequest(offset, limit, sort));

        List<T> pageResultContent = pageResult.getContent().stream()
                .distinct()
                .collect(Collectors.toList());

        ret.setOffset(offset);
        ret.setTotalLength(pageResult.getTotalElements());
        ret.setData(pageResultContent);

        return ret;
    }
}
