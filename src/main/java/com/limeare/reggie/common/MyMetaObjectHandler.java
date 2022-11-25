package com.limeare.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    //公告字段自动填充

    @Override
    public void insertFill(MetaObject metaObject) {
        //创建时自动填充
        metaObject.setValue("createUser", BaseContext.getCurrentId() );
        metaObject.setValue("createTime", LocalDateTime.now());

        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新时自动填充
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
