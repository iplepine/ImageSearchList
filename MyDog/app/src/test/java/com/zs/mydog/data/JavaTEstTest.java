package com.zs.mydog.data;

import org.junit.Test;

public class JavaTEstTest {

    @Test
    public void test() {
        String str = "asdf";
        Class<? extends String> vv = str.getClass();

        AA<Iaaaa> aaaa = new AA<>();
        aaaa.ppp(new Izzz());
    }


    class AA<T> {
        T obj;

        public void ppp(T o) {
            obj = o;

            Class<? extends T> gg = (Class<T>) o.getClass();

            System.out.println(gg.toString());
        }
    }

    interface Iaaaa {

    }

    class Izzz implements Iaaaa {
    }
}