package ru.samsung.itschool.mdev.testbed;

import android.os.Handler;
import android.os.Message;

import java.util.Arrays;

public class AndroidOutputStream extends java.io.OutputStream {

    private final int MAXLEN = 100; // MAXLEN OF OUTPUT TEXT
    private Handler hOut;
    private byte[] text = new byte[MAXLEN];
    private int nBytes = 0;

    public AndroidOutputStream(Handler hOut) {
        this.hOut = hOut;
    }

    @Override
    public void write(int oneByte) {
        text[nBytes++] = (byte) oneByte;
        if (oneByte == '\n' || nBytes == MAXLEN) {
            this.flush();
        }
    }

    @Override
    public void flush() {
        if (nBytes == 0) {
            return;
        }
        Message m = hOut.obtainMessage(0, new String(text, 0, nBytes));
        hOut.sendMessage(m);
        nBytes = 0;
        Arrays.fill(text, (byte) 0);
    }
}
