J0Util for Android
======

A small pack of independent utility methods and classes for Android. Were collected while i was working on different projects. Actually i'm sure you could find a lot of such staff around, but for me hand made is always more interesting ;)

This repository goes as standalone Android Studio module; gradle builds with plugin 'android-library'.

BmpUtil
======
Utility class with methods for working with android.graphics.Bitmap. Mostly for scaling and converting large bitmaps from byte arrays to Bitmap objects. The point is, Android can manage Bitmap objects only sized 2048x2048 or less. Otherwise you'll get OutOfMemoryError. So you could use OpenGL directly, or use something like this BmpUtil to correctly subsample large bitmap from byte array.

LogUtil
======
Utility class for better log usage: log tag consists of LOG_TAG and class name, so you could filter messages from a certain class.

PathUtil
======
Utility class for working with paths in java.io.File, java.net.URL and java.net.URI. Actually just works with strings :)

RotationUtil
======
Utility class for device rotation handling. Also wraps android.view.OrientationEventListener.

SecurityUtil
======
Utility class for simplifying java.security.MessageDigest usage for hashing algorithms.