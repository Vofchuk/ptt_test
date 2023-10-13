package com.example.ptt_test

import io.flutter.embedding.android.FlutterActivity
import android.os.Bundle
import androidx.annotation.NonNull
import com.zello.sdk.BluetoothAccessoryState
import com.zello.sdk.BluetoothAccessoryType
import com.zello.sdk.ContactType
import com.zello.sdk.Events
import com.zello.sdk.Tab
import io.flutter.plugin.common.MethodChannel
import com.zello.sdk.Zello
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel


class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.ptt_test/zello"
    private val EVENT_CHANNEL = "com.example.your_project_name/zello_events"

    private val zelloEvents = object : Events {
        override fun onSelectedContactChanged() {
            TODO("Not yet implemented")
        }

        override fun onMessageStateChanged() {
            TODO("Not yet implemented")
        }

        override fun onAppStateChanged() {
            // Send this event to Flutter
        }

        override fun onLastContactsTabChanged(p0: Tab) {
            TODO("Not yet implemented")
        }

        override fun onContactsChanged() {
            TODO("Not yet implemented")
        }

        override fun onAudioStateChanged() {
            TODO("Not yet implemented")
        }

        override fun onMicrophonePermissionNotGranted() {
            TODO("Not yet implemented")
        }

        override fun onBluetoothAccessoryStateChanged(
            p0: BluetoothAccessoryType,
            p1: BluetoothAccessoryState,
            p2: String?,
            p3: String?
        ) {
            TODO("Not yet implemented")
        }
        // Implement other event methods
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Zello.getInstance().configure(this)

    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)


        EventChannel(flutterEngine!!.dartExecutor.binaryMessenger, EVENT_CHANNEL).setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    Zello.getInstance().subscribeToEvents(object : com.zello.sdk.Events {
                        override fun onSelectedContactChanged() {
                            TODO("Not yet implemented")
                        }

                        override fun onMessageStateChanged() {
                            events?.success("onMessageStateChanged")
                        }

                        override fun onAppStateChanged() {
                            events?.success("App state changed")
                        }

                        override fun onLastContactsTabChanged(p0: Tab) {
                            TODO("Not yet implemented")
                        }

                        override fun onContactsChanged() {
                            TODO("Not yet implemented")
                        }

                        override fun onAudioStateChanged() {
                            TODO("Not yet implemented")
                        }

                        override fun onMicrophonePermissionNotGranted() {
                            TODO("Not yet implemented")
                        }

                        override fun onBluetoothAccessoryStateChanged(
                            p0: BluetoothAccessoryType,
                            p1: BluetoothAccessoryState,
                            p2: String?,
                            p3: String?
                        ) {
                            TODO("Not yet implemented")
                        }
                        // Implement other event methods and send them to Flutter
                    })
                }

                override fun onCancel(arguments: Any?) {
//                    Zello.getInstance().unsubscribeFromEvents(this) //todo!
                }
            }
        )



        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            when (call.method) {
                "connectChannel" -> {
                    val channelName: String? = call.argument("channelName")
                    connectChannel(channelName)
                    result.success("Connecting to channel $channelName")
                }
                "startTalking" -> {
                    startTalking()
                    result.success("Started talking")
                }
                "stopTalking" -> {
                    stopTalking()
                    result.success("Stopped talking")
                }
                "getUserChannels" -> {
                    val channels =   getuserChannels()
                    result.success(channels)
                }
                "getContacts" -> {
                    val contacts = getContacts()
                    result.success(contacts)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun getContacts(): List<Map<String, Any?>> {
        val channelList = mutableListOf<Map<String, Any?>>()
        val contacts = Zello.getInstance().contacts
        val count = contacts!!.count
        for (i in 0 until count) {
            val contact = contacts.getItem(i)
            if (true) {//contact!!.type == ContactType.CHANNEL
                val map = mapOf(
                    "name" to contact!!.name,
                    "displayName" to contact.displayName,
                    "status" to contact.status.name,
                    "muted" to contact.muted,
                    // Add other properties here
                )
                channelList.add(map)
            }
        }
        return channelList
    }

    private fun getuserChannels(): MutableList<String> {
        val contactNames = mutableListOf<String>()
        val contacts = Zello.getInstance().contacts
        val count = contacts?.count
        for (i in 0 until count!!) {
            val contact = contacts?.getItem(i)
            contactNames.add(contact?.name!!)
        }
        return contactNames
    }

    override fun onDestroy() {
        super.onDestroy()
//        Zello.getInstance().unsubscribeFromEvents(this)
    }



    private fun stopTalking() {
        Zello.getInstance().endMessage()
    }

    private fun startTalking() {
        Zello.getInstance().beginMessage()
    }

    private fun connectChannel(channelName: String?) {
        Zello.getInstance().connectChannel("test")
    }


    override fun onPause() {
        super.onPause()
        Zello.getInstance().enterPowerSavingMode()
    }

    override fun onResume() {
        super.onResume()
        Zello.getInstance().leavePowerSavingMode()
    }
}
