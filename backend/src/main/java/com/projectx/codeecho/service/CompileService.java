package com.projectx.codeecho.service;

import java.util.Map;

public interface CompileService {
    Object compileCode(String body);
    Map<String, Object> runObject(Object obj);
}
