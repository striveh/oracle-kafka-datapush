package com.striveh.pushdata.vo;

import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private Long totalRecord;

    public PageResult() {
    }

    public PageResult(List<T> list, Long totalRecord) {
        this.list = list;
        this.totalRecord = totalRecord;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotalRecord() {
        return this.totalRecord;
    }

    public void setTotalRecord(Long totalRecord) {
        this.totalRecord = totalRecord;
    }
}