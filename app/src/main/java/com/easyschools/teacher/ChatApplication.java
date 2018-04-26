package com.easyschools.teacher;

import android.app.Application;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatApplication extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://crm.easyschools.org:7000");
        } catch (URISyntaxException e) {
            Log.d("TEST" , String.valueOf(e));
            throw new RuntimeException(e);

        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
