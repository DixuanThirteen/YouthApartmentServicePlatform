package com.yasp.exception;

// 继承 RuntimeException，这样可以不用强制 try-catch
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
    // 你还可以自定义错误码等属性，需要可自行扩展
}