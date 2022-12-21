package com.limeare.reggie.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    STATUS_1(1,"待付款"),
    STATUS_2(2,"代派送"),
    STATUS_3(3,"已派送"),
    STATUS_4(4,"已派送"),
    STATUS_5(5,"已取消"),
    ;
    private final int value;
    private final String decs;
    public static OrderStatusEnum toType(int value) {
        return Stream.of(OrderStatusEnum.values())
                .filter(p -> p.value == value)
                .findAny()
                .orElse(null);
    }
}
