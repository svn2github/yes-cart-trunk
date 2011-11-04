package org.yes.cart.service.order.impl;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DefaultOrderNumberGeneratorImplTest {

    @Test
    public void testGetNextOrderNumber() {
        DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl();
        for (int i = 1; i <= 100000; i = i + 1) {
            final String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
            final String expected = "-" + i;
            assertEquals(
                    expected,
                    orderNum.substring(orderNum.indexOf("-")));
        }


        //Multithread.
        int THREADGROUPSIZE = 1000;
        defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl();
        Set<String> rez = Collections.synchronizedSet(new HashSet<String>());
        Thread[] th = new Thread[THREADGROUPSIZE];
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            th[i] = new MyOrderCreatorThread(defaultOrderNumberGenerator, rez);
            th[i].start();
        }
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            try {
                th[i].join();
            } catch (InterruptedException e) {
                System.out.print("Join interrupted\n");
            }
        }

        assertEquals(THREADGROUPSIZE, rez.size());
    }

    class MyOrderCreatorThread extends Thread {

        private final DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator;

        private final Set<String> generated;

        MyOrderCreatorThread(final DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator, final Set<String> generated) {
            super();
            this.defaultOrderNumberGenerator = defaultOrderNumberGenerator;
            this.generated = generated;
        }

        @Override
        public void run() {
            String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
            assertFalse(generated.contains(orderNum));
            generated.add(orderNum);
        }
    }
}
