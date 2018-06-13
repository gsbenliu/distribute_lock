package com.test.concurrent;

/**
 * @Author: liugang
 * @Date: 2018/6/13 16:16
 */
public class ThreadJoin extends Thread {


    private String name;

    public ThreadJoin(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + "第一阶段");
            Thread.sleep(3000);
            System.out.println(name + "第二阶段");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadJoin threadJoin01 = new ThreadJoin("Thread1");
        ThreadJoin threadJoin02 = new ThreadJoin("Thread2");
        ThreadJoin threadJoin03 = new ThreadJoin("Thread3");

        threadJoin01.start();
        threadJoin02.start();

        try {
            threadJoin01.join();
            threadJoin02.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        threadJoin03.start();

    }
}
