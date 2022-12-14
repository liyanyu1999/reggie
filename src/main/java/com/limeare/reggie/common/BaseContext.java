package com.limeare.reggie.common;

/*
* 用于保存和获取当前用户的id
* */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    //设置值
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //获取值
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
