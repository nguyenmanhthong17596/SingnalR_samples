package com.example.singnalr_samples

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.postDelayed
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    companion object {

        lateinit var hubConnection: HubConnection
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listMess = ArrayList<String>()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listMess)
        listView_chat.adapter = adapter

        hubConnection =
            HubConnectionBuilder.create("your-url-here").build()
        try {
            hubConnection.start().subscribeOn(Schedulers.single())
                .doOnComplete {
                    if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
                        Log.i("LOG", "connection Success")
                    }
                }
                .blockingAwait()
        } catch (e: Exception) {
            Log.i("LOG", " Start Error: $e")
        }
        hubConnection.on("NotifyGroup", { message ->
            runOnUiThread {
                Log.i("LOG", "message from server: $message")
                adapter.add(message)
                adapter.notifyDataSetChanged()
            }
        }, String::class.java)


        button_send_message.setOnClickListener {
            val message = edittext_content_chat.text.toString()
            if (message != "") {
                runOnUiThread {
                    try {
                        if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                            hubConnection.start()
                        }
                        hubConnection.send("Method_Name", "group_Name", message)
                        Log.i("LOG", "sending message to server")
                    } catch (e: Exception) {
                        Log.i("LOG", "send message error: $e")
                    }

                }
            }
        }

        button_add_to_Group.setOnClickListener {
            if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                Log.i("LOG", "start connection")
                hubConnection.start().blockingAwait()
            } else {
                hubConnection.send("AddToGroup", "aaa")
                Log.i(
                    "LOG",
                    "Connection Id: ${hubConnection.connectionId} - Status: ${hubConnection.connectionState}"
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
            text_status_connection.text = "Connected"

        } else {
            hubConnection.start()
            text_status_connection.text = "Disconnected"
        }

    }

}
