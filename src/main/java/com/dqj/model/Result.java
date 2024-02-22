package com.dqj.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {

    private String code;
    private String msg;
    private T body;

    public Result(String code) {
        this.code = code;
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(String code, T body) {
        this.code = code;
        this.body = body;
    }

    public static <T> Result<T> success(T body) {
        return new Result<T>("200", body);
    }

}
