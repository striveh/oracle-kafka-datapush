package com.striveh.pushdata.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Test {

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime.toString());
        System.out.println(localDateTime.toEpochSecond(ZoneOffset.of("+8")));


    }
}
