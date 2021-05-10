package ru.gurtovenko.bankdemo.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class OLPageRequest implements Pageable {

    long offset;
    int limit;

    Sort sort;

    public OLPageRequest(long offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public OLPageRequest(long offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        if (limit == 0) return 1;
        return (int) (offset / limit) + 1;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OLPageRequest(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new OLPageRequest( Long.max (offset - limit, 0), limit, sort);
    }

    @Override
    public Pageable first() {
        return new OLPageRequest(0, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public boolean isPaged() {
        return limit > 0;
    }

    @Override
    public boolean isUnpaged() {
        return !isPaged();
    }
}
