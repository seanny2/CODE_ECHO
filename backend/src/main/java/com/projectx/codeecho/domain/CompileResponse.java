package com.projectx.codeecho.domain;

public enum CompileResponse {
    SUCEESS("성공"),
    FAIL("실패");
    public final String message;
    CompileResponse(String message) {
        this.message = message;
    }
    public String getText() {
        return this.message;
    }
}
