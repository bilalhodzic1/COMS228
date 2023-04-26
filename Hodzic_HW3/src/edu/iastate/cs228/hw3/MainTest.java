package edu.iastate.cs228.hw3;

import java.util.ListIterator;

public class MainTest {
    public static void main(String[] args) {
        MultiList<Integer> list = new MultiList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(10);
        list.add(11);
        list.add(2, 12);
        list.remove(5);
        list.remove(2);
        list.remove(5);
        list.add(5,77);
        list.add(6,8);
        list.add(5,45);
        ListIterator<Integer> iter = list.listIterator();
        System.out.println(list.toStringInternal());
        iter.next();
        iter.remove();
        System.out.println(list.toStringInternal(iter));
        iter.next();
        iter.previous();
        System.out.println(list.toStringInternal(iter));
        iter.remove();
        System.out.println(list.toStringInternal(iter));
        iter.next();
        iter.previous();
        iter.set(9);
        System.out.println(list.toStringInternal(iter));

    }
}
