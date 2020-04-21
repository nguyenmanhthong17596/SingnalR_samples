package com.example.singnalr_samples;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;

public class SignalRListener {

    private static SignalRListener instance;

    private HubConnection hubConnection;

    private SignalRListener(ListView listView) {
        hubConnection = HubConnectionBuilder.create("https://fitnesstest.azurewebsites.net/tfgym").build();

        ArrayList<String> listMess = new ArrayList<String>();
        ArrayAdapter<String> messAdapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, listMess);

        hubConnection.stop();
        listView.setAdapter(messAdapter);
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED){
            hubConnection.start();
            Log.i("Thong","Connection Status: "+ hubConnection.getConnectionState());
        }

        hubConnection.on("NotifyGroup", (message) -> {
            messAdapter.add(message);
            messAdapter.notifyDataSetChanged();
            Log.i("Thong","message: "+message);
        }, String.class);
    }

    public static SignalRListener getInstance(ListView view) {
        if (instance == null)
            instance = new SignalRListener(view);
        return instance;
    }

    public boolean startConnection() {

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start();
            return true;
        } else return false;
    }

    public boolean stopConnection() {

        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.stop();
            return true;
        } else return false;
    }

    public void sendToServer(String message) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("NotifyGroup", "aaa", message);
        }
    }

    public void addToGroup(Context context) {
        Toast.makeText(context,"Connection Status: "+ hubConnection.getConnectionState(),Toast.LENGTH_LONG).show();
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("AddToGroup", "aaa");
            Toast.makeText(context, "Send success", Toast.LENGTH_LONG).show();
        }
    }
}
