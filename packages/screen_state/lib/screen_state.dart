import 'dart:async';
import 'dart:io' show Platform;
import 'package:flutter/services.dart';

/// Enumeration of Screen State events coming from android.
enum ScreenStateEvent { SCREEN_UNLOCKED, SCREEN_ON, SCREEN_OFF }

/// Custom Exception for the plugin,
/// thrown whenever the plugin is used on platforms other than Android
class ScreenStateException implements Exception {
  String _cause;

  ScreenStateException(this._cause);

  @override
  String toString() {
    return _cause;
  }
}

/// Screen representation as object which holds the stream for [ScreenStateEvent]s.
class Screen {
  EventChannel _eventChannel = const EventChannel('screenStateEvents');
  Stream<ScreenStateEvent>? _screenStateStream;

  /// Stream of [ScreenStateEvent]s.
  /// Each event is streamed as it occurs on the phone.
  /// Only Android [ScreenStateEvent] are streamed.
  Stream<ScreenStateEvent>? get screenStateStream {
    if (Platform.isAndroid) {
      if (_screenStateStream == null) {
        _screenStateStream = _eventChannel
            .receiveBroadcastStream()
            .map((event) => _parseScreenStateEvent(event));
      }
      return _screenStateStream;
    }
    throw ScreenStateException(
        'Screen State API exclusively available on Android!');
  }

  ScreenStateEvent _parseScreenStateEvent(String event) {
    switch (event) {
      /** Android **/
      case 'android.intent.action.SCREEN_OFF':
        return ScreenStateEvent.SCREEN_OFF;
      case 'android.intent.action.SCREEN_ON':
        return ScreenStateEvent.SCREEN_ON;
      case 'android.intent.action.USER_PRESENT':
        return ScreenStateEvent.SCREEN_UNLOCKED;
      default:
        throw new ArgumentError('$event was not recognized.');
    }
  }
}
