import 'package:ptt_test/models/contact.dart';

class MessageIn {
  final Contact from;
  final Contact author;
  bool active;

  bool get isPrivateMessage => this.author.name == null;

  MessageIn({Contact? from, Contact? author, this.active = false})
      : from = from ?? Contact(),
        author = author ?? Contact();

  void reset() {
    from.reset();
    author.reset();
    active = false;
  }

  MessageIn clone() {
    return MessageIn(
      from: from.clone(),
      author: author.clone(),
      active: active,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'from': from.toMap(),
      'author': author.toMap(),
      'active': active,
    };
  }

  factory MessageIn.fromMap(Map<String, dynamic> map) {
    return MessageIn(
      from: Contact.fromMap(Map<String, dynamic>.from(map['from'])),
      author: Contact.fromMap(Map<String, dynamic>.from(map['author'])),
      active: map['active'],
    );
  }
}
