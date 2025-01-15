package com.niutucode.practice;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Snowflake snowflake = new Snowflake(0);
        System.out.println("分布式ID：" + snowflake.nextId());
    }
}