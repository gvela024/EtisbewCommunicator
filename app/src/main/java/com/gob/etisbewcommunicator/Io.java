package com.gob.etisbewcommunicator;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class Io extends Application {

    private Socket socket;
    {
        try {
            socket = IO.socket("http://etisbew.herokuapp.com/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
