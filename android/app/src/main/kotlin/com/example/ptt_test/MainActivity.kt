package com.example.ptt_test

import android.os.Bundle
import androidx.annotation.NonNull
import com.zello.sdk.BluetoothAccessoryState
import com.zello.sdk.BluetoothAccessoryType
import com.zello.sdk.Contact
import com.zello.sdk.Events
import com.zello.sdk.MessageIn
import com.zello.sdk.MessageOut
import com.zello.sdk.Tab
import com.zello.sdk.Zello
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.ptt_test/zello"
    private val EVENT_CHANNEL = "com.example.your_project_name/zello_events"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Zello.getInstance().configure(this)
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        EventChannel(flutterEngine!!.dartExecutor.binaryMessenger, EVENT_CHANNEL)
                .setStreamHandler(
                        object : EventChannel.StreamHandler {
                            override fun onListen(
                                    arguments: Any?,
                                    events: EventChannel.EventSink?
                            ) {
                                Zello.getInstance()
                                        .subscribeToEvents(
                                                object : com.zello.sdk.Events {
                                                    override fun onSelectedContactChanged() {
                                                        events?.success("onSelectedContactChanged")
                                                    }

                                                    override fun onMessageStateChanged() {
                                                        // Initialize an empty Contact object

                                                        val messageIn = getMessageIn()

                                                        if (messageIn!!.isActive) {
                                                            //
                                                            // events?.success("Message IN!!!
                                                            // ${messageIn.from.name} ||
                                                            // ${messageIn.author.name}")

                                                            events?.success(
                                                                    mapOf(
                                                                            "event" to "messageIn",
                                                                            "data" to
                                                                                    messageInToMap(
                                                                                            messageIn
                                                                                    ),
                                                                    )
                                                            )
                                                        }

                                                        val messageOut = getMessageOut()

                                                        if (messageOut!!.isActive) {
                                                            events?.success(
                                                                    mapOf(
                                                                            "event" to "messageOut",
                                                                            "data" to
                                                                                    messageOutToMap(
                                                                                            messageOut
                                                                                    ),
                                                                    )
                                                            )
                                                        }

                                                        val selectedContact = getCurrentContact()
                                                        events?.success(
                                                                "Message state changed for contact: ${selectedContact!!.name}"
                                                        )
                                                    }

                                                    override fun onAppStateChanged() {
                                                        events?.success("App state changed")
                                                    }

                                                    override fun onLastContactsTabChanged(p0: Tab) {
                                                        events?.success("onLastContactsTabChanged")
                                                    }

                                                    override fun onContactsChanged() {
                                                        events?.success("onContactsChanged")
                                                    }

                                                    override fun onAudioStateChanged() {
                                                        events?.success("onAudioStateChanged")
                                                    }

                                                    override fun onMicrophonePermissionNotGranted() {
                                                        events?.success(
                                                                "onMicrophonePermissionNotGranted"
                                                        )
                                                    }

                                                    override fun onBluetoothAccessoryStateChanged(
                                                            p0: BluetoothAccessoryType,
                                                            p1: BluetoothAccessoryState,
                                                            p2: String?,
                                                            p3: String?
                                                    ) {
                                                        TODO("Not yet implemented")
                                                    }
                                                    // Implement other event methods and send them
                                                    // to Flutter
                                                }
                                        )
                            }

                            override fun onCancel(arguments: Any?) {
                                //
                                // Zello.getInstance().unsubscribeFromEvents(this) //todo!
                            }
                        }
                )

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call,
                result ->
            when (call.method) {
                "connectToChannel" -> {
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
                "getUserCurrentConnectedContact" -> {
                    val channels = getCurrentConnectedCOntact()
                    result.success(channels)
                }
                "getContacts" -> {
                    val contacts = getContacts()
                    result.success(contacts)
                }
                //                "set" -> {
                //                    val contacts = getContacts()
                //                    result.success(contacts)
                //                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun getMessageIn(): MessageIn? {

        val messageIn = MessageIn()

        Zello.getInstance().getMessageIn(messageIn)

        return messageIn
    }

    private fun getMessageOut(): MessageOut? {

        val messageOut = MessageOut()

        Zello.getInstance().getMessageOut(messageOut)

        return messageOut
    }

    private fun getCurrentContact(): Contact? {
        val selectedContact = Contact()

        // Populate the Contact object with the currently selected contact's details
        Zello.getInstance().getSelectedContact(selectedContact)
        return selectedContact
    }

    private fun getCurrentConnectedCOntact(): Contact? {
        // val contact = Zello.getInstance().getSelectedContact();
        //        return contact;

        return null
    }

    private fun getContacts(): List<Map<String, Any?>> {
        val channelList = mutableListOf<Map<String, Any?>>()
        val contacts = Zello.getInstance().contacts
        val count = contacts!!.count
        for (i in 0 until count) {
            val contact = contacts.getItem(i)

            val map = contactToMap(contact!!)
            channelList.add(map)
        }
        return channelList
    }

    private fun stopTalking() {
        Zello.getInstance().endMessage()
    }

    private fun startTalking() {
        Zello.getInstance().beginMessage()
    }

    private fun connectChannel(channelName: String?) {
        Zello.getInstance().connectChannel(channelName)
    }

    override fun onPause() {
        super.onPause()
        //        Zello.getInstance().enterPowerSavingMode()
    }

    override fun onResume() {
        super.onResume()
        //        Zello.getInstance().leavePowerSavingMode()
    }

    fun contactToMap(contact: Contact): Map<String, Any?> {
        return mapOf(
                "name" to contact.name,
                "fullName" to contact.fullName,
                "displayName" to contact.displayName,
                "type" to contact.type.name,
                "status" to contact.status.name,
                "statusMessage" to contact.statusMessage,
                "usersCount" to contact.usersCount,
                "usersTotal" to contact.usersTotal,
                "title" to contact.title,
                "muted" to contact.muted,
                "noDisconnect" to contact.noDisconnect
        )
    }

    fun messageInToMap(messageIn: MessageIn): Map<String, Any> {
        return mapOf(
                "from" to contactToMap(messageIn.from),
                "author" to contactToMap(messageIn.author),
                "active" to messageIn.isActive
        )
    }

    fun messageOutToMap(messageOut: MessageOut): Map<String, Any> {
        return mapOf(
                "to" to contactToMap(messageOut.to),
                "active" to messageOut.isActive,
                "connecting" to messageOut.isConnecting
        )
    }
}
