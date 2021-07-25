package ru.samsung.itschool.mdev.testbed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Handler hOut;
    private Handler hIn;
    private TextView consoleWrite;
    private EditText valuePrompt;
    private AndroidOutputStream out;
    private AndroidInputStream in;
    private Method main;
    private Class UserClass;
    private Handler handler;
    private MyThread myThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String message = (String) msg.obj;
                valuePrompt.setVisibility(View.VISIBLE);
                valuePrompt.setText(message);
                valuePrompt.setEnabled(false);
            }
        };

        hOut = new PrintoutHandler();
        hIn = new ScanInHandler();
        out = new AndroidOutputStream(hOut);
        System.setOut(new PrintStream(out));
        in = new AndroidInputStream(hIn);
        System.setIn(in);

        consoleWrite = findViewById(R.id.consoleWrite);
        valuePrompt = findViewById(R.id.valuePrompt);
        valuePrompt.setVisibility(View.GONE);

        myThread = new MyThread();
        myThread.setRunning(true);
        myThread.start();

        valuePrompt.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String value = valuePrompt.getText().toString();
                in.addString(value);
                valuePrompt.setText("");
                valuePrompt.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        });
    }

    private class ScanInHandler extends Handler {
        public void handleMessage(Message msg) {
            valuePrompt.setVisibility(View.VISIBLE);
        }
    }

    private class PrintoutHandler extends Handler {
        public void handleMessage(Message msg) {
            // update TextView
            String readText = consoleWrite.getText().toString();
            consoleWrite.setText(readText + msg.obj);
        }
    }

    public String doSlow() {
        try {
            UserClass = Class.forName("ru.samsung.itschool.mdev.lib.MyProgram");
        } catch (ClassNotFoundException e) {
            Log.e("Error",getString(R.string.user_program_not_found));
        }
        try {
            main = UserClass.getDeclaredMethod("main", new String[0].getClass());
        } catch (NoSuchMethodException e) {
            Log.e("Error",getString(R.string.main_not_found));
        }
        try {
            // USER PROGRAM START
            main.invoke(null, new Object[]{new String[0]});
        } catch (Throwable error) {
            return getString(R.string.program_stopped);
        }
        return getString(R.string.program_finished);
    }


    class MyThread extends Thread {
        private boolean running;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            if(running) {
                String result = doSlow();
                Message msg = Message.obtain();
                msg.obj = result;
                msg.setTarget(handler);
                msg.sendToTarget();
            }
        }
    }
}