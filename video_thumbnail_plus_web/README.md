# video_thumbnail_plus_web

The web implementation of [`video_thumbnail_plus`][1].

## Usage

This package is [endorsed](https://flutter.dev/docs/development/packages-and-plugins/developing-packages#endorsed-federated-plugin), which means you can simply use `video_thumbnail_plus` normally. This package will be automatically included in your app when you do, so you do not need to add it to your `pubspec.yaml`.

However, if you `import` this package to use any of its APIs directly, you should add it to your `pubspec.yaml` as usual.

## Limitations on the Web platform

Video Thumbnail Plus on the Web platform has some limitations that might surprise developers more familiar with mobile/desktop targets.

In no particular order:

### CORS headers
This plugin requires the server hosting the video to include appropriate CORS headers in the response. Specifically, the server must include the `Access-Control-Allow-Origin` and `Access-Control-Allow-Methods` headers in the response to the request.
For more information, please refer to the [Mozilla Developer Network documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS).

### HTTP range headers
This plugin requires the server hosting the video to support HTTP range headers. If the server does not support range requests, the plugin may generate a thumbnail from the first frame of the video instead of the desired frame.

[1]: ../video_thumbnail_plus