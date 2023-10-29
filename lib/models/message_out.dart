import 'package:ptt_test/models/contact.dart';

class MessageOut {
  final Contact to;
  bool active;
  bool connecting;

  MessageOut({Contact? to, this.active = false, this.connecting = false})
      : to = to ?? Contact();

  void reset() {
    to.reset();
    active = false;
    connecting = false;
  }

  MessageOut clone() {
    return MessageOut(
      to: to.clone(),
      active: active,
      connecting: connecting,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'to': to.toMap(),
      'active': active,
      'connecting': connecting,
    };
  }

  factory MessageOut.fromMap(Map<String, dynamic> map) {
    return MessageOut(
      to: Contact.fromMap(Map<String, dynamic>.from(map['to'])),
      active: map['active'],
      connecting: map['connecting'],
    );
  }
}
