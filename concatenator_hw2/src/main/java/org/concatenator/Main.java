package org.concatenator;

public class Main {
    public static void main(String[] args) {
        Concatenator c = new Concatenator("test");
        System.out.println(c.collectResultString());
//        System.out.println(System.getProperty("user.dir"));
    }
}