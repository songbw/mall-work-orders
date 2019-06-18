package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PageInfo<T> {
    private int total;
    private int pageSize;
    private int pageIndex;
    private List<T> rows;

    public PageInfo(int total, int pageSize, int pageIndex,List<T> rows) {
        this.total = total;
        this.rows = rows;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "total=" + total +
                "pageIndex=" + pageIndex +
                "pageSize=" + pageSize +
                ", rows=" + rows +
                '}';
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public List<T> getRows() {
        return rows;
    }
}
