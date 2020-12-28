package com.aaa.lib.map.imp.parser;

public class ParseResult<T> {
    public static final int MSG_PARSE_SUCCESS = 0;
    public static final int MSG_PARSE_FAIL_DATA_ERROR = -1;
    public static final int MSG_PARSE_FAIL_DATA_NOT_MATCH = -2;
    int code;
    T result;
    public ParseResult(int code){
        this.code=code;
    }

    public ParseResult(int code, T result) {
        this.code = code;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}