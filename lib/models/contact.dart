// ignore_for_file: constant_identifier_names

enum ContactType {
  USER,
  CHANNEL,
  GROUP,
  GATEWAY,
  UNKNOWN,
  CONVERSATION;

  static fromMap(String? name) {
    if (name == null) return ContactType.UNKNOWN;
    return values.firstWhere(
      (e) => e.name == name,
    );
  }
}

enum ContactStatus {
  OFFLINE,
  AVAILABLE,
  BUSY,
  STANDBY,
  CONNECTING;

  static fromMap(String name) => values.firstWhere(
        (e) => e.name == name,
      );
}

class Contact {
  String? name;
  String? fullName;
  String? displayName;
  ContactType type;
  ContactStatus status;
  String? statusMessage;
  int usersCount;
  int usersTotal;
  String? title;
  bool muted;
  bool noDisconnect;

  Contact({
    this.name,
    this.fullName,
    this.displayName,
    this.type = ContactType.USER,
    this.status = ContactStatus.OFFLINE,
    this.statusMessage,
    this.usersCount = 0,
    this.usersTotal = 0,
    this.title,
    this.muted = false,
    this.noDisconnect = false,
  });

  void reset() {
    name = null;
    fullName = null;
    displayName = null;
    type = ContactType.USER;
    status = ContactStatus.OFFLINE;
    statusMessage = null;
    usersCount = 0;
    usersTotal = 0;
    title = null;
    muted = false;
    noDisconnect = false;
  }

  Contact clone() {
    return Contact(
      name: name,
      fullName: fullName,
      displayName: displayName,
      type: type,
      status: status,
      statusMessage: statusMessage,
      usersCount: usersCount,
      usersTotal: usersTotal,
      title: title,
      muted: muted,
      noDisconnect: noDisconnect,
    );
  }

  bool isValid() {
    return name != null && name!.isNotEmpty;
  }

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'fullName': fullName,
      'displayName': displayName,
      'type': type.name,
      'status': status.name,
      'statusMessage': statusMessage,
      'usersCount': usersCount,
      'usersTotal': usersTotal,
      'title': title,
      'muted': muted,
      'noDisconnect': noDisconnect,
    };
  }

  factory Contact.fromMap(Map<String, dynamic> map) {
    return Contact(
      name: map['name'],
      fullName: map['fullName'].toString(),
      displayName: map['displayName'],
      type: ContactType.fromMap(map['type']),
      status: ContactStatus.fromMap(map['status']),
      statusMessage: map['statusMessage'],
      usersCount: map['usersCount'] ?? 0,
      usersTotal: map['usersTotal'],
      title: map['title'],
      muted: map['muted'],
      noDisconnect: map['noDisconnect'],
    );
  }
}
