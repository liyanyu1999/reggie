package com.limeare.reggie.common;

//自定义业务异常
public class CustomerException extends RuntimeException {
    public CustomerException(String msg){
        super(msg);
    }
}
