# Uninstalling & Hiding Apps Programmatically

This sample shows how to uninstall apps programmatically without user confirmation and how to hide apps (including pre-installed ones), so that they are inaccessible to the user.

Before you can use the app, you first need to make it a device owner with the following ADB command
```
adb shell dpm set-device-owner eu.sisik.removehideapps/.DevAdminReceiver
```

For more information check out my blogposts
[www.sisik.eu/blog/android/media/add-text-to-video](https://sisik.eu/blog/android/dev-admin/uninstalling-and-disabling-apps)
