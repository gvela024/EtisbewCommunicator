package com.gob.etisbewcommunicator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SensorReadingGenerator extends Fragment {
    private EditText id;
    private Button start;
    private Button stop;
    private Socket socket;
    private Boolean isConnected = true;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener accelerometerListener;
    private Handler sendDataHandler;

    private float xAxisAcceleration;
    private float yAxisAcceleration;

    public SensorReadingGenerator() {
        super();

        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                xAxisAcceleration = event.values[0];
                yAxisAcceleration = event.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // do nothing
            }
        };

        sendDataHandler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Io io = (Io) getActivity().getApplication();
        socket = io.getSocket();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.connect();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generate_reading, container, false);

        initializeAllViews(rootView);
        setButtonClickedListeners();

        return rootView;
    }

    private void initializeAllViews(View view) {
        id = (EditText) view.findViewById(R.id.id);
        start = (Button) view.findViewById(R.id.startGenerating);
        stop = (Button) view.findViewById(R.id.stopGenerating);
    }

    private void setButtonClickedListeners() {
        setStartOnClickListener();
        setStopOnClickListener();
    }

    private void setStartOnClickListener() {
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendDataHandler.postDelayed(new SendData(), TimeUnit.SECONDS.toMillis(1));
            }
        });
    }

    private void setStopOnClickListener() {
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendDataHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if(!isConnected) {
                isConnected = true;
            }
            }
        });
        }
    };

    private class SendData implements Runnable {
        @Override
        public void run() {
            try {
                JSONObject reading = new JSONObject();
                reading.put("temperature", Float.toString(xAxisAcceleration));
                reading.put("relativeHumidity", Float.toString(yAxisAcceleration));

                socket.emit("newReadingFromSensor", id.getText().toString(), reading);
            } catch (JSONException e ) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            sendDataHandler.postDelayed(new SendData(), TimeUnit.SECONDS.toMillis(1));
        }
    }
}
