# video_thumbnail_plus_platform_interface

A common platform interface for the [`video_thumbnail_plus`][1] plugin.

This interface allows platform-specific implementations of the `video_thumbnail_plus` plugin, as well as the plugin itself, to ensure they are supporting the same interface.

# Usage

To implement a new platform-specific implementation of `video_thumbnail_plus`, extend [`VideoThumbnailPlusPlatform`][2] with an implementation that performs the platform-specific behavior, and when you register your plugin, set the default `VideoThumbnailPlusPlatform` by calling `VideoThumbnailPlusPlatform.instance = MyVideoThumbnailPlusPlatform()`.

# Note on breaking changes

Strongly prefer non-breaking changes (such as adding a method to the interface) over breaking changes for this package.

See https://flutter.dev/go/platform-interface-breaking-changes for a discussion on why a less-clean interface is preferable to a breaking change.

[1]: ../video_thumbnail_plus
[2]: lib/video_thumbnail_plus_platform_interface.dart