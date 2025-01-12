package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageApiResponse<T> extends ApiResponse<List<T>> {
    private PageableResponse page = new PageableResponse();

    public PageApiResponse(List<T> data, int pageIndex, int pageSize, long total) {
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(total);
        this.data = data;
        success();
    }

    public PageApiResponse() {}

    public static <T> PageApiResponse<T> of(List<T> data, int pageIndex, int pageSize, long total) {
        return new PageApiResponse<>(data, pageIndex, pageSize, total);
    }

    public static <T> PageApiResponse<T> failPaging() {
        PageApiResponse<T> response = new PageApiResponse<>();
        response.setSuccess(false);
        return response;
    }

    @Data
    public static class PageableResponse implements Serializable {
        private int pageIndex;
        private int pageSize;
        private long total;
    }
}
