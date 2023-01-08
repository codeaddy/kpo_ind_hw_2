package org.concatenator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Enter rootpath: ");
        Scanner scanner = new Scanner(System.in);
        String rootPath = scanner.nextLine();
        scanner.close();
        Concatenator c = new Concatenator(rootPath);
        System.out.println(c.collectResultString());
//        System.out.println(System.getProperty("user.dir"));
    }
}