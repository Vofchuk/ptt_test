package com.example.ptt_test

import android.os.Bundle
import androidx.annotation.NonNull
import com.zello.sdk.AppState
import com.zello.sdk.BluetoothAccessoryState
import com.zello.sdk.BluetoothAccessoryType
import com.zello.sdk.Contact
import com.zello.sdk.MessageIn
import com.zello.sdk.MessageOut
import com.zello.sdk.Tab
import com.zello.sdk.Theme
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

        EventChannel(flutterEngine.dartExecutor.binaryMessenger, EVENT_CHANNEL).setStreamHandler(
                object : EventChannel.StreamHandler {
                    override fun onListen(
                        arguments: Any?, events: EventChannel.EventSink?
                    ) {
                        Zello.getInstance().subscribeToEvents(object : com.zello.sdk.Events {
                                override fun onSelectedContactChanged() {
                                    val contact: Contact = Contact()
                                    Zello.getInstance().getSelectedContact(contact)

                                    events?.success(
                                        mapOf(
                                            "event" to "onSelectedContactChanged",
                                            "contacts" to contactToMap(contact),
                                        )
                                    )
                                }

                                override fun onMessageStateChanged() {

                                    val messageIn = getMessageIn()

                                    if (messageIn!!.isActive) {
                                        events?.success(
                                            mapOf(
                                                "event" to "messageIn",
                                                "messageIn" to messageInToMap(
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
                                                "messageOut" to messageOutToMap(
                                                    messageOut
                                                ),
                                            )
                                        )
                                    }

//                                    val selectedContact = getCurrentContact()
//                                    events?.success(
//                                        "Message state changed for contact: ${selectedContact!!.name}"
//                                    )
                                }

                                override fun onAppStateChanged() {
                                    val appState: AppState = AppState()
                                    Zello.getInstance().getAppState(appState)
                                    events?.success( mapOf(
                                        "event" to "appStateChanged",
                                        "appState" to appStateToMap(
                                            appState
                                        ),
                                    ),)
                                }

                                override fun onLastContactsTabChanged(p0: Tab) {
//                                    events?.success("onLastContactsTabChanged")
                                }

                                override fun onContactsChanged() {
                                    val contacts = getContacts()
                                    events?.success( mapOf(
                                        "event" to "contactsChanged",
                                        "contacts" to contacts,
                                    ),)

                                }

                                override fun onAudioStateChanged() {
                                   // events?.success("onAudioStateChanged")
                                }

                                override fun onMicrophonePermissionNotGranted() {
//                                    events?.success(
//                                        "onMicrophonePermissionNotGranted"
//                                    )
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
                            })
                    }

                    override fun onCancel(arguments: Any?) {
                        //
                        // Zello.getInstance().unsubscribeFromEvents(this) //todo!
                    }
                })

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "connectToChannel" -> {
                    val channelName: String? = call.argument("channelName")
                    connectChannel(channelName)
                    result.success("Connecting to channel $channelName")
                }

                "connectToUser" -> {
                    val userName: String? = call.argument("userName")
                    connectToContact(userName)
                    result.success("Connecting to user $userName")
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

                "login"->{
                    val userName: String? = call.argument("userName")
                    val password: String? = call.argument("password")
                    val network: String? = call.argument("network")
                    Zello.getInstance().signIn(network, userName, password);
                    result.success(true);
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

    private fun getMessageIn(): MessageIn {

        val messageIn = MessageIn()

        Zello.getInstance().getMessageIn(messageIn)

        return messageIn
    }

    private fun getMessageOut(): MessageOut {

        val messageOut = MessageOut()

        Zello.getInstance().getMessageOut(messageOut)

        return messageOut
    }

    private fun getCurrentContact(): Contact {
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

    private fun connectToContact(contactName: String?){
        Zello.getInstance().setSelectedUserOrGateway(contactName)
    }

    private fun connectChannel(channelName: String?) {
        Zello.getInstance().setSelectedChannelOrGroup(channelName)
//        Zello.getInstance().set(channelName);
//        Zello.getInstance().selectContact(
//            "Select a contact",
//            arrayOf<Tab>(Tab.RECENTS, Tab.USERS, Tab.CHANNELS),
//            Tab.RECENTS,
//          tance().connectChannel(channelName)
////        Zello.getInstance().selectContact()  Theme.DARK
//        )
//        Zello.getIns
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

    fun appStateToMap(appState: AppState): Map<String, Any?> {
        return mapOf(
            "available" to appState.isAvailable,
            "initializing" to appState.isInitializing,
            "customBuild" to appState.isCustomBuild,
            "configuring" to appState.isConfiguring,
            "locked" to appState.isLocked,
            "signedIn" to appState.isSignedIn,
            "signingIn" to appState.isSigningIn,
            "signingOut" to appState.isSigningOut,
            "cancelling" to appState.isCancellingSignin,
            "reconnectTimer" to appState.reconnectTimer,
            "waitingForNetwork" to appState.isWaitingForNetwork,
            "showContacts" to appState.showContacts,
            "status" to appState.status,
            "autoRun" to appState.isAutoRunEnabled,
            "autoChannels" to appState.isChannelAutoConnectEnabled,
            "lastError" to appState.lastError,
            "statusMessage" to appState.statusMessage,
            "network" to appState.network,
            "networkUrl" to appState.networkUrl,
            "username" to appState.username,
            "externalId" to appState.externalId
        )
    }
}
