package com.gob.etisbewcommunicator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CreateSensor extends Fragment {
    private EditText id;
    private EditText description;
    private EditText latitude;
    private EditText longitude;
    private EditText temperature;
    private EditText relativeHumidity;
    private Button create;
    private TextWatcher textWatcher;
    private Socket socket;
    private Boolean isConnected = true;

    public CreateSensor() {
        super();
        initializeTextWatcher();
    }

    private void initializeTextWatcher() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                create.setEnabled(ifAllTheTextBoxesContainAtLeastOneCharacter());
            }
        };
    }

    private boolean ifAllTheTextBoxesContainAtLeastOneCharacter() {
        return id.getText().toString().length() > 0 &&
                description.getText().toString().length() > 0 &&
                latitude.getText().toString().length() > 0 &&
                longitude.getText().toString().length() > 0 &&
                temperature.getText().toString().length() > 0 &&
                relativeHumidity.getText().toString().length() > 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Io io = (Io) getActivity().getApplication();
        socket = io.getSocket();
        socket.on(Socket.EVENT_CONNECT, onConnect);

//        Io.getSocket().on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("me", "on socket connection");
//                    }
//                });
//            }
//        });
        socket.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_sensor, container, false);

        initializeAllViews(rootView);
        setTextChangedListenerToAllTextViews();
        setCreateButtonClickedListener();
        create.setEnabled(false);

        return rootView;
    }

    private void initializeAllViews(View view) {
        id = (EditText) view.findViewById(R.id.id);
        description = (EditText) view.findViewById(R.id.description);
        latitude = (EditText) view.findViewById(R.id.latitude);
        longitude = (EditText) view.findViewById(R.id.longitude);
        temperature = (EditText) view.findViewById(R.id.temperature);
        relativeHumidity  = (EditText) view.findViewById(R.id.relativeHumidity);
        create = (Button) view.findViewById(R.id.create);
    }

    private void setTextChangedListenerToAllTextViews() {
        id.addTextChangedListener(textWatcher);
        description.addTextChangedListener(textWatcher);
        latitude.addTextChangedListener(textWatcher);
        longitude.addTextChangedListener(textWatcher);
        temperature.addTextChangedListener(textWatcher);
        relativeHumidity.addTextChangedListener(textWatcher);
    }

    private void setCreateButtonClickedListener() {
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    JSONObject sensorData = new JSONObject();
                    sensorData.put("id", id.getText().toString());
                    sensorData.put("description", description.getText().toString());
                    sensorData.put("latitude", latitude.getText().toString());
                    sensorData.put("longitude", longitude.getText().toString());
                    sensorData.put("temperature", temperature.getText().toString());
                    sensorData.put("relativeHumidity", relativeHumidity.getText().toString());

                    socket.emit("newSensorCreated", sensorData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        Log.d("me", "connected with socket.io");
                        isConnected = true;
                    }
                }
            });
        }
    };
}
