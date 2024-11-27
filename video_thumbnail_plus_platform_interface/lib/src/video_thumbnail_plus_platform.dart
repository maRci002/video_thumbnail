import 'dart:typed_data';

import 'package:cross_file/cross_file.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:video_thumbnail_plus_platform_interface/src/image_format.dart';
import 'package:video_thumbnail_plus_platform_interface/src/method_channel_video_thumbnail_plus.dart';

abstract class VideoThumbnailPlusPlatform extends PlatformInterface {
  /// Constructs a VideoThumbnailPlusPlatform.
  VideoThumbnailPlusPlatform() : super(token: _token);

  static final Object _token = Object();

  static VideoThumbnailPlusPlatform _instance =
      MethodChannelVideoThumbnailPlus();

  /// The default instance of [VideoThumbnailPlusPlatform] to use.
  ///
  /// Defaults to [MethodChannelVideoThumbnailPlus].
  static VideoThumbnailPlusPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [VideoThumbnailPlusPlatform] when
  /// they register themselves.
  static set instance(VideoThumbnailPlusPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<XFile> thumbnailFile({
    required String video,
    required Map<String, String>? headers,
    required String? thumbnailPath,
    required ImageFormat imageFormat,
    required int maxHeight,
    required int maxWidth,
    required int timeMs,
    required int quality,
  }) {
    throw UnimplementedError('thumbnailFile() has not been implemented.');
  }

  Future<Uint8List> thumbnailData({
    required String video,
    required Map<String, String>? headers,
    required ImageFormat imageFormat,
    required int maxHeight,
    required int maxWidth,
    required int timeMs,
    required int quality,
  }) {
    throw UnimplementedError('thumbnailData() has not been implemented.');
  }
}
