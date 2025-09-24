package de.jcup.basheditor;

public class HoveredTextInfo {
    public HoveredTextInfo(int offset) {
        this.functionOffsetLeft = offset;
        this.filenameOffsetLeft = offset;
    }

    int functionOffsetLeft;
    int filenameOffsetLeft;
    String functionName;
    String fileName;
}