# Google Smart Lock for Passwords plugin for Unity
_Copyright (c) 2015 Google Inc. All rights reserved._

The Google Smart Lock for Passwords plugin for Unity&reg; is an open-source project whose goal
is to provide a plugin that allows developers to integrate with
the Google Smart Lock Credentials API from a game written in Unity&reg;. However, this project is
not in any way endorsed or supervised by Unity Technologies.

_Unity&reg; is a trademark of Unity Technologies._

## Overview

The plugin provides support for the Credentials API on Android.  For
more information see: https://developers.google.com/identity/smartlock-passwords/android/overview.

This plugin only works on Android at this time.

Features:

* easy GUI-oriented project setup (integrated into the Unity GUI)
* no need to override/customize the player Activity
* no need to override/customize AndroidManifest.xml

System requirements:

* Unity&reg; 4.5 or above
* To deploy on Android:
    * Android SDK
    * Android v4.0 or higher
    * Google Play Services library, version 7.0 or above

## Upgrading

If you have already integrated your project with a previous
version of the plugin and wish to upgrade to a new version,
please refer to the [upgrade instructions](UPGRADING.txt).


## Plugin Installation

To download the plugin, clone this Git repository into your file system (or download it as
a ZIP file and unpack it). Then, look for the **unitypackage** file in
the **current-build** directory:

    current-build/GoogleSmartLock-X.YY.ZZ.unitypackage

To install the plugin, simply open your game project in Unity and import that file into
your project's assets, as you would any other Unity package. This is accomplished through
the **Assets | Import Package | Custom Package** menu item (you can also reach this menu it
by right-clicking the **Assets** folder). After importing, you should see that
a new menu item was added to the Window menu: **"Google > SmartLock > Setup..."**.
If you don't see the new menu items, refresh the assets by
clicking **Assets | Refresh** and try again.

## Load Your Game Project

Next, load your game project into the Unity editor.

If you do not have a game project to work with, you can use the **Credentials** sample
available in the **samples** directory. Using that sample will allow you to
quickly test your setup and make sure you can access the API.

## Android Setup

To configure your Unity game to run with Google Play Services on Android, first
open the Android SDK manager and verify that you have downloaded the **Google
Play Services** package. The Android SDK manager is usually available in your
SDK installation directory, under the "tools" subdirectory, and is called
**android** (or **android.exe** on Windows). The **Google Play Services**
package is available under the **Extras** folder. If it is not installed
or is out of date, install or update it before proceeding.

Next, set up the path to your Android SDK installation in Unity. This is located in the
preferences menu, under the **External Tools** section.

Next, configure your game's package name. To do this, click **File | Build Settings**,
select the **Android** platform and click **Player Settings** to show Unity's
Player Settings window. In that window, look for the **Bundle Identifier** setting
under **Other Settings**. Enter your package name there (for example
_com.example.my.awesome.game_).

Next, click the **Window |Google|SmartLock|Setup...** menu item.
This will copy the play services library project into the Unity project.

## Run the Project

If you are working with the **Credentials** sample, you should be able to build
and run the project at this point. You will see a screen with edit fields and
3 buttons.  These buttons exercise the merthods of the API:
* Load - loads the password credentials for this application.  The first time
there are no save credentials, but the email and profile picture should be pre-populated.
* Save - saves the credentials including any password entered in the password field.
* Delete - deletes the stored password.


**Note: Running in the editor will return sample (empty) values.  In order to use
this API you must be running the application on an Android device.

To build and run on
Android, click **File | Build Settings**, select the **Android** platform, then
**Switch to Platform**, then **Build and Run**.


## Sample code to load credentials
```csharp
    using Google.SmartLock;

    SmartLockCredentials.Instance.Load(
                    (status, credential) =>
                    {
                        Debug.Log("Got Status: " + status + ": " + credential);
                        // if the status is success, a real app would use the
                        // credential to sign into the other site.  Here, we
                        // just display them.
                        if (credential != null)
                        {
                            currrentCredential = credential;
                        }
                    });
```

## Sample code to save credentials
```csharp
    using Google.SmartLock;

    // A real app would probably call save after the authentication.
    // Here is just a button for the demo.
    SmartLockCredentials.Instance.Save(currrentCredential,
        (status, credential) =>
        {
            Debug.Log("Got Status: " + status + ": " + credential);
        });
```

## Sample code to delete credentials
```csharp
    using Google.SmartLock;

    // Nice to allow the user to delete or "forget me"
    SmartLockCredentials.Instance.Delete(currrentCredential,
        (status, credential) =>
        {
            Debug.Log("Got Status: " + status + ": " + credential);
            // initialize to empty credential.
            currrentCredential = new Credential("", "", "", "", "");
        });
```
