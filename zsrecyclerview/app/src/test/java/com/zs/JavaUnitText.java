package com.zs;

import org.junit.Test;

import java.lang.ref.WeakReference;

public class JavaUnitText {
    public class A {
        private B b = new B();

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }

        public final class B {
            public String text = "아직 살아있다.";
        }
    }

    @Test
    public void asss() {
        A a = new A();
        /*HashSet<WeakReference<A>> abc = new HashSet<>();

        abc.add(new WeakReference<>(a));*/

        WeakReference weak = new WeakReference(a);

        System.gc();
        System.out.println(weak.get());
        a = null;
        System.gc();

        /*for (WeakReference<A> b : abc) {
            System.out.println(b.get());
        }*/

        System.out.println(weak.get());
    }

}
