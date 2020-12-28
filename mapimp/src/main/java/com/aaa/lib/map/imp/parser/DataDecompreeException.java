package com.aaa.lib.map.imp.parser;

public class DataDecompreeException extends Exception {
    int errCode;
    public DataDecompreeException(int errCode, String message) {
        super(message);
        this.errCode=errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
