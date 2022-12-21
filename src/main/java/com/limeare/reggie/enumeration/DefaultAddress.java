package com.limeare.reggie.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;
@Getter
@AllArgsConstructor
public enum DefaultAddress {

    //默认地址
    STATUS_0(0,"否"),
    STATUS_1(1,"是"),

    ;
    private final int value;
    private final String decs;
    public static DefaultAddress toType(int value) {
        return Stream.of(DefaultAddress.values())
                .filter(p -> p.value == value)
                .findAny()
                .orElse(null);
    }
}
