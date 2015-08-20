// <copyright file="SmartLockSetup.cs" company="Google Inc.">
// Copyright (C) Google Inc. All Rights Reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//    limitations under the License.
// </copyright>

namespace Google.SmartLock.Editor
{
    using UnityEngine;
    using UnityEditor;

    /// <summary>
    /// Smart lock setup. Copies the play services library
    /// and checks the version.
    /// </summary>
    public class SmartLockSetup : EditorWindow
    {
        [MenuItem("Window/Google/SmartLock for Passwords/Setup...", false, 1)]
        public static void MenuItemSetup()
        {
            if (PerformSetup())
            {
                EditorUtility.DisplayDialog("Google Smartlock for Passwords",
                    "Setup complete",
                    "OK");
            }
        }

        /// <summary>
        /// Provide static access to setup for facilitating automated builds.
        /// </summary>
        public static bool PerformSetup()
        {
            // check that Android SDK is there
            if (!SetupUtils.HasAndroidSdk())
            {
                Debug.LogError("Android SDK not found.");
                EditorUtility.DisplayDialog("Android SDK Not found",
                    "The Android SDK path was not found. " +
                    "Please configure it in the Unity preferences window (under External Tools).",
                    "OK");

                    return false;
            }

            return SetupUtils.CopySupportLibs();
        }
    }
}
