package ru.samsung.itschool.mdev.testbed;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AndroidInputStream extends java.io.InputStream {

    private Handler hIn;
    private ByteArrayInputStream in;
    private BlockingQueue<String> readLineArrayList = new LinkedBlockingQueue<>();
    private boolean valid = false;


    AndroidInputStream(Handler hIn) {
        this.hIn = hIn;
    }

    void addString(String string) {
        try {
            readLineArrayList.put(string);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }

    @Override
    public int read() throws IOException {
        if (!valid) {
            String inStr = getString() + "\n";
            in = new ByteArrayInputStream(inStr.getBytes());
            System.out.print(inStr);
        }

        int x = in.read();
        valid = x != -1;
        return x;
    }

    private String getString() {

        Message m = hIn.obtainMessage(0, "");
        hIn.sendMessage(m);

        try {
            return readLineArrayList.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
