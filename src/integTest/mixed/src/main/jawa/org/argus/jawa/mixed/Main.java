package org.argus.jawa.mixed;

public class Main {
    public static void main(String[] args) {
        IHello h = new Hello();
        String greeting = h.greeting();
        System.out.print(greeting);
    }
}
