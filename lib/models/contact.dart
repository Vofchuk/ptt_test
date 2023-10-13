import 'dart:convert';

class Contact {
  final String name;
  final String displayName;
  final String status;
  final bool muted;
  Contact({
    required this.name,
    required this.displayName,
    required this.status,
    required this.muted,
  });
  // Add other properties here


  Contact copyWith({
    String? name,
    String? displayName,
    String? status,
    bool? muted,
  }) {
    return Contact(
      name: name ?? this.name,
      displayName: displayName ?? this.displayName,
      status: status ?? this.status,
      muted: muted ?? this.muted,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'displayName': displayName,
      'status': status,
      'muted': muted,
    };
  }

  factory Contact.fromMap(Map<String, dynamic> map) {
    return Contact(
      name: map['name'] ?? '',
      displayName: map['displayName'] ?? '',
      status: map['status'] ?? '',
      muted: map['muted'] ?? false,
    );
  }

  String toJson() => json.encode(toMap());

  factory Contact.fromJson(String source) => Contact.fromMap(json.decode(source));

  @override
  String toString() {
    return 'Contact(name: $name, displayName: $displayName, status: $status, muted: $muted)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
  
    return other is Contact &&
      other.name == name &&
      other.displayName == displayName &&
      other.status == status &&
      other.muted == muted;
  }

  @override
  int get hashCode {
    return name.hashCode ^
      displayName.hashCode ^
      status.hashCode ^
      muted.hashCode;
  }
}
