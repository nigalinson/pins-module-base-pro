package com.sloth.pinsplatform;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/07/11
 *     desc  :
 * </pre>
 */
public class BusUtilsTest extends com.blankj.utilcode.util.BaseTest {

    private static final String TAG_NO_PARAM         = "TagNoParam";
    private static final String TAG_ONE_PARAM        = "TagOneParam";
    private static final String TAG_NO_PARAM_STICKY  = "TagNoParamSticky";
    private static final String TAG_ONE_PARAM_STICKY = "TagOneParamSticky";

    private static final String TAG_IO     = "TAG_IO";
    private static final String TAG_CPU    = "TAG_CPU";
    private static final String TAG_CACHED = "TAG_CACHED";
    private static final String TAG_SINGLE = "TAG_SINGLE";

    @BusUtils.Bus(tag = TAG_NO_PARAM)
    public void noParamFun() {
        System.out.println("noParam");
    }

    @BusUtils.Bus(tag = TAG_NO_PARAM)
    public void noParamSameTagFun() {
        System.out.println("sameTag: noParam");
    }

    @BusUtils.Bus(tag = TAG_ONE_PARAM)
    public void oneParamFun(String param) {
        System.out.println(param);
    }

    @BusUtils.Bus(tag = TAG_NO_PARAM_STICKY, sticky = true)
    public void foo() {
        System.out.println("foo");
    }

    @BusUtils.Bus(tag = TAG_ONE_PARAM_STICKY, sticky = true)
    public void oneParamStickyFun(Callback callback) {
        if (callback != null) {
            System.out.println(callback.call());
        }
    }

    @BusUtils.Bus(tag = TAG_IO, threadMode = BusUtils.ThreadMode.IO)
    public void ioFun(CountDownLatch latch) {
        System.out.println("Thread.currentThread() = " + Thread.currentThread());
        latch.countDown();
    }

    @BusUtils.Bus(tag = TAG_CPU, threadMode = BusUtils.ThreadMode.CPU)
    public void cpuFun(CountDownLatch latch) {
        System.out.println("Thread.currentThread() = " + Thread.currentThread());
        latch.countDown();
    }

    @BusUtils.Bus(tag = TAG_CACHED, threadMode = BusUtils.ThreadMode.CACHED)
    public void cachedFun(CountDownLatch latch) {
        System.out.println("Thread.currentThread() = " + Thread.currentThread());
        latch.countDown();
    }

    @BusUtils.Bus(tag = TAG_SINGLE, threadMode = BusUtils.ThreadMode.SINGLE)
    public void singleFun(CountDownLatch latch) {
        System.out.println("Thread.currentThread() = " + Thread.currentThread());
        latch.countDown();
    }

    @Before
    public void setUp() throws Exception {
        BusUtils.registerBus4Test(TAG_NO_PARAM, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "noParamFun", "", "", false, "POSTING", 0);
        BusUtils.registerBus4Test(TAG_ONE_PARAM, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "oneParamFun", String.class.getName(), "param", false, "POSTING", 0);
        BusUtils.registerBus4Test(TAG_NO_PARAM_STICKY, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "noParamStickyFun", "", "", true, "POSTING", 0);
        BusUtils.registerBus4Test(TAG_NO_PARAM_STICKY, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "foo", "", "", true, "POSTING", 0);
        BusUtils.registerBus4Test(TAG_ONE_PARAM_STICKY, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "oneParamStickyFun", Callback.class.getName(), "callback", true, "POSTING", 0);

        BusUtils.registerBus4Test(TAG_IO, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "ioFun", CountDownLatch.class.getName(), "latch", false, "IO", 0);
        BusUtils.registerBus4Test(TAG_CPU, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "cpuFun", CountDownLatch.class.getName(), "latch", false, "CPU", 0);
        BusUtils.registerBus4Test(TAG_CACHED, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "cachedFun", CountDownLatch.class.getName(), "latch", false, "CACHED", 0);
        BusUtils.registerBus4Test(TAG_SINGLE, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "singleFun", CountDownLatch.class.getName(), "latch", false, "SINGLE", 0);
    }

    @BusUtils.Bus(tag = TAG_NO_PARAM_STICKY, sticky = true)
    public void noParamStickyFun() {
//        BusUtils.removeSticky(TAG_NO_PARAM_STICKY);
        System.out.println("noParamSticky");
    }

    @Subscribe(sticky = true)
    public void eventBusFun(String param) {
        System.out.println(param);
    }

    @Subscribe(sticky = true)
    public void eventBusFun1(String param) {
        System.out.println("foo");
    }

    @Test
    public void testEventBusSticky() {
        EventBus.getDefault().postSticky("test");
        System.out.println("----");

        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        EventBus.getDefault().register(new com.blankj.utilcode.util.BusUtilsTest());
        EventBus.getDefault().register(new com.blankj.utilcode.util.BusUtilsTest());
        EventBus.getDefault().register(new com.blankj.utilcode.util.BusUtilsTest());

        System.out.println("----");

        EventBus.getDefault().postSticky("test");
        EventBus.getDefault().postSticky("test");
    }

    @Test
    public void testSticky() {
        BusUtils.postSticky(TAG_NO_PARAM_STICKY);
        System.out.println("----");

        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        BusUtils.register(new com.blankj.utilcode.util.BusUtilsTest());
        BusUtils.register(new com.blankj.utilcode.util.BusUtilsTest());
        BusUtils.register(new com.blankj.utilcode.util.BusUtilsTest());

        System.out.println("----");

        BusUtils.post(TAG_NO_PARAM_STICKY);
//        BusUtils.post(TAG_NO_PARAM_STICKY);
    }

    @Test
    public void testMultiThread() {
        final com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
//        for (int i = 0; i < 100; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    BusUtils.register(test);
//                }
//            }).start();
//        }
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        BusUtils.register(test);
//        for (int i = 0; i < 100; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    BusUtils.post(TAG_NO_PARAM);
//                }
//            }).start();
//        }
//        try {
//            countDownLatch.await(1, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        BusUtils.unregister(test);
//        for (int i = 0; i < 100; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    BusUtils.unregister(test);
//                }
//            }).start();
//        }
//        for (int i = 0; i < 100; i++) {
//            final int finalI = i;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    BusUtils.register(test);
//                    BusUtils.post(TAG_ONE_PARAM, "" + finalI);
//                    BusUtils.unregister(test);
//                }
//            }).start();
//        }
    }

    @Test
    public void registerAndUnregister() {
        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();

        BusUtils.post(TAG_NO_PARAM);
        BusUtils.post(TAG_ONE_PARAM, "post to one param fun.");

        BusUtils.register(test);

        BusUtils.post(TAG_NO_PARAM);
        BusUtils.post(TAG_ONE_PARAM, "Post to one param fun.");

        BusUtils.unregister(test);

        BusUtils.post(TAG_NO_PARAM);
        BusUtils.post(TAG_ONE_PARAM, "Post to one param fun.");
    }

    @Test
    public void post() {
        com.blankj.utilcode.util.BusUtilsTest test0 = new com.blankj.utilcode.util.BusUtilsTest();
        com.blankj.utilcode.util.BusUtilsTest test1 = new com.blankj.utilcode.util.BusUtilsTest();

        BusUtils.register(test0);
        BusUtils.register(test1);

        BusUtils.post(TAG_NO_PARAM);
        BusUtils.post(TAG_ONE_PARAM, "post to one param fun.");

        BusUtils.unregister(test0);
        BusUtils.unregister(test1);
    }

    @Test
    public void postSticky() {
        System.out.println("-----not sticky bus postSticky will be failed.-----");
        BusUtils.postSticky(TAG_NO_PARAM);
        BusUtils.postSticky(TAG_ONE_PARAM, "post to one param fun.");

        System.out.println("\n-----sticky bus postSticky will be successful.-----");
        BusUtils.postSticky(TAG_NO_PARAM_STICKY);
        BusUtils.postSticky(TAG_ONE_PARAM_STICKY, new Callback() {
            @Override
            public String call() {
                return "post to one param sticky fun.";
            }
        });
        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        System.out.println("\n-----register.-----");
        BusUtils.register(test);

        System.out.println("\n-----sticky post.-----");
        BusUtils.postSticky(TAG_NO_PARAM);
        BusUtils.postSticky(TAG_ONE_PARAM, "post to one param fun.");
        BusUtils.postSticky(TAG_NO_PARAM_STICKY);
        BusUtils.postSticky(TAG_ONE_PARAM_STICKY, new Callback() {
            @Override
            public String call() {
                return "post to one param sticky fun.";
            }
        });

        BusUtils.removeSticky(TAG_NO_PARAM_STICKY);
        BusUtils.removeSticky(TAG_ONE_PARAM_STICKY);
        BusUtils.unregister(test);
    }

    @Test
    public void removeSticky() {
        BusUtils.postSticky(TAG_NO_PARAM_STICKY);
        BusUtils.postSticky(TAG_ONE_PARAM_STICKY, new Callback() {
            @Override
            public String call() {
                return "post to one param sticky fun.";
            }
        });
        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        System.out.println("-----register.-----");
        BusUtils.register(test);

        BusUtils.unregister(test);
        System.out.println("\n-----register.-----");
        BusUtils.register(test);

        System.out.println("\n-----remove sticky bus.-----");
        BusUtils.removeSticky(TAG_NO_PARAM_STICKY);
        BusUtils.removeSticky(TAG_ONE_PARAM_STICKY);
        BusUtils.unregister(test);

        System.out.println("\n-----register.-----");
        BusUtils.register(test);
        BusUtils.unregister(test);
    }

    @Test
    public void testThreadMode() throws InterruptedException {
        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        CountDownLatch latch = new CountDownLatch(4);
        BusUtils.register(test);

        BusUtils.post(TAG_IO, latch);
        BusUtils.post(TAG_CPU, latch);
        BusUtils.post(TAG_CACHED, latch);
        BusUtils.post(TAG_SINGLE, latch);

        latch.await();
        BusUtils.unregister(test);
    }

    @Test
    public void toString_() {
        System.out.println("BusUtils.toString_() = " + BusUtils.toString_());
    }

    @Test
    public void testBase() {
        BusUtils.registerBus4Test("base", com.blankj.utilcode.util.BaseTest.class.getName(), "noParamFun", "int", "i", false, "POSTING", 0);

        com.blankj.utilcode.util.BaseTest t = new com.blankj.utilcode.util.BusUtilsTest();
        BusUtils.register(t);
        BusUtils.post("base", 1);
        BusUtils.unregister(t);
    }

    @Test
    public void testSameTag() {
        BusUtils.registerBus4Test(TAG_NO_PARAM, com.blankj.utilcode.util.BusUtilsTest.class.getName(), "noParamSameTagFun", "", "", false, "POSTING", 2);

        com.blankj.utilcode.util.BusUtilsTest test = new com.blankj.utilcode.util.BusUtilsTest();
        BusUtils.register(test);
        BusUtils.post(TAG_NO_PARAM);
        BusUtils.unregister(test);
    }

    public interface Callback {
        String call();
    }
}