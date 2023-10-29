// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:logger/logger.dart';
import 'package:ptt_test/models/contact.dart';
import 'package:ptt_test/models/message_in.dart';
import 'package:ptt_test/models/message_out.dart';

var logger = Logger();

class ZelloService {
  static const platform = MethodChannel('com.example.ptt_test/zello');

  static const eventChannel =
      EventChannel('com.example.your_project_name/zello_events');

  ZelloService() {
    connect();
    listen();
  }

  List<Contact> contacts = [];

  void connect() async {
    contacts = await getContacts();
    print(contacts);
  }

  void connectToChannel(String channelName) async {
    if (contacts.indexWhere((element) => element.name == channelName) == -1) {
      print("Channel not found");
      throw Exception("Channel not found");
    }
    var response = await platform
        .invokeMethod('connectToChannel', {"channelName": channelName});
    print(response);
  }

  void printNuberOfContacts() async {
    var channels = await platform.invokeMethod('getUserChannels');
    print(channels);
  }

  void login(String username, String password, String network) async {
    var response = await platform.invokeMethod('login',
        {"userName": username, "password": password, "network": network});
    print(response);
  }

  Future<List<Contact>> getContacts() async {
    final List<dynamic> contacts = await platform.invokeMethod('getContacts');
    var c = contacts
        .map((map) => Contact.fromMap(Map<String, dynamic>.from(map)))
        .toList();
    print(c);

    return c;
  }

  void startTalking() async {
    var response = await platform.invokeMethod('startTalking');
  }

  void stopTalking() async {
    var response = await platform.invokeMethod('stopTalking');
  }

  void listen() {
    eventChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);
  }

  void _onEvent(dynamic event) {
    Map eventMAp = {};
    if (event is Map) {
      switch (event["event"]) {
        case "messageIn":
          eventMAp["event"] = "messageIn";

          MessageIn messageIn =
              MessageIn.fromMap(Map<String, dynamic>.from(event["data"]));
          if (messageIn.isPrivateMessage) {
            eventMAp["Type of message"] = "Private";
          } else {
            eventMAp["Type of message"] = "Channel";
          }
          eventMAp["Author"] = messageIn.author.name;
          eventMAp["Channel"] = messageIn.from.name;
          break;
        case "messageOut":
          eventMAp["event"] = "messageOut";
          MessageOut messageOut =
              MessageOut.fromMap(Map<String, dynamic>.from(event["data"]));
          eventMAp["To"] = messageOut.to.name;
          eventMAp["Active"] = messageOut.active;
          eventMAp["Connecting"] = messageOut.connecting;
          break;
        case "onSelectedContactChanged":
          eventMAp["event"] = "onSelectedContactChanged";
          Contact contact =
              Contact.fromMap(Map<String, dynamic>.from(event["data"]));
          eventMAp["Contact"] = contact.name;
      }

      logger.i(eventMAp);

      return;
    }
    switch (event) {
      case "onMessageStateChanged":
        print("onMessageStateChanged");
        break;
    }
    print("Event received: $event");
    // Handle the event from native code
  }

  void _onError(Object error) {
    print("Error received: $error");
  }
}
