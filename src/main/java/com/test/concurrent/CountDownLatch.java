package com.test.concurrent;

/**
 * @Author: liugang
 * @Date: 2018/6/13 16:17
 */
public class CountDownLatch extends Thread {

    private String name;

    private java.util.concurrent.CountDownLatch latch;

    public CountDownLatch(String name, java.util.concurrent.CountDownLatch latch) {
        this.name = name;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + "第一阶段");
            latch.countDown();
            Thread.sleep(5000);
            System.out.println(name + "第二阶段");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);
            CountDownLatch countDownLatch1 = new CountDownLatch("thread1", latch);
            CountDownLatch countDownLatch2 = new CountDownLatch("thread2", latch);
            CountDownLatch countDownLatch3 = new CountDownLatch("thread3", latch);

            countDownLatch1.start();
            countDownLatch2.start();
            latch.await();
            countDownLatch3.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
