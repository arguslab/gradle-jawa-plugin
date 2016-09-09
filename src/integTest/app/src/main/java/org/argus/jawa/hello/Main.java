package org.argus.jawa.hello;

/**
 * Created by fgwei on 9/8/16.
 */
public class Main {
    public static void main(String[] args) {
        IHello h = new Hello();
        String greeting = h.greeting();
        System.out.print(greeting);
    }
}
