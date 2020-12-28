package com.aaa.lib.map.imp.parser;

public class PathDataParceException extends Exception {
    private int errCode;
    public PathDataParceException(int errCode, String message) {
        super(message);
        this.errCode=errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
