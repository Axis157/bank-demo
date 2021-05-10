package ru.gurtovenko.bankdemo.service.response;

import java.util.List;

public class PagingListResponse<T> {
    private Long offset;
    private Long totalLength;
    private List<T> data;

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(Long totalLength) {
        this.totalLength = totalLength;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
