// ignore_for_file: avoid_print

import 'package:flutter/services.dart';
import 'package:ptt_test/models/contact.dart';



class ZelloService {
    static const platform =  MethodChannel('com.example.ptt_test/zello');

   static const eventChannel = EventChannel('com.example.your_project_name/zello_events');



  void connect() {
    // Connect to the Zello service
  }

  void printNuberOfContacts() async{
    var channels = await platform.invokeMethod('getUserChannels');
    print(channels);
  }

    Future<List<Contact>> getContacts() async {
    final List<dynamic> contacts = await platform.invokeMethod('getContacts');
    var c = contacts.map((map) => Contact.fromMap(Map<String, dynamic>.from(map))).toList();
    print(c);

    return c;
  }

  void startTalking() async{
    var response = await platform.invokeMethod('startTalking');
  }

  void stopTalking() async{
    var response = await platform.invokeMethod('stopTalking');
  }

  void listen(){
     eventChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);
  }

      void _onEvent(dynamic event) {
    print("Event received: $event");
    // Handle the event from native code
  }

  void _onError(Object error) {
    print("Error received: $error");
  }

}