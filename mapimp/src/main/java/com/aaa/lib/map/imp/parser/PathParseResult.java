package com.aaa.lib.map.imp.parser;

public class PathParseResult{
    int posFrom;
    int posTo;

    public PathParseResult(int lackFrom, int lackEnd) {
        this.posFrom = lackFrom;
        this.posTo = lackEnd;
    }

    public int getPosFrom() {
        return posFrom;
    }

    public void setPosFrom(int posFrom) {
        this.posFrom = posFrom;
    }

    public int getPosTo() {
        return posTo;
    }

    public void setPosTo(int posTo) {
        this.posTo = posTo;
    }
}