// <copyright file="SetupUtils.cs" company="Google Inc.">
// Copyright (C) 2015 Google Inc. All Rights Reserved.
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
    using System.IO;
    using UnityEditor;
    using UnityEngine;

    public static class SetupUtils
    {

        public static string SlashesToPlatformSeparator(string path)
        {
            return path.Replace("/", Path.DirectorySeparatorChar.ToString());
        }

        public static void Alert(string title, string s)
        {
            EditorUtility.DisplayDialog(title, s, "OK");
        }

        public static string GetAndroidSdkPath()
        {
            string sdkPath = EditorPrefs.GetString("AndroidSdkRoot");
            if (sdkPath != null && (sdkPath.EndsWith("/") || sdkPath.EndsWith("\\")))
            {
                sdkPath = sdkPath.Substring(0, sdkPath.Length - 1);
            }

            return sdkPath;
        }

        public static bool HasAndroidSdk()
        {
            string sdkPath = GetAndroidSdkPath();
            return sdkPath != null && sdkPath.Trim() != string.Empty && System.IO.Directory.Exists(sdkPath);
        }

        public static bool CopySupportLibs()
        {
            string sdkPath = GetAndroidSdkPath();
            string libProjPath = sdkPath +
                SlashesToPlatformSeparator(
                    "/extras/google/google_play_services/libproject/google-play-services_lib");

            string libProjDestDir = SlashesToPlatformSeparator(
                "Assets/Plugins/Android/google-play-services_lib");

            // check that the Google Play Services lib project is there
            if (!System.IO.Directory.Exists(libProjPath))
            {
                Debug.LogError("Google Play Services lib project not found at: " + libProjPath);
                EditorUtility.DisplayDialog("Google Play Services Library Project Not Found",
                    "Google Play Services library project " +
                    "could not be found your SDK installation. Make sure it is installed (open " +
                    "the SDK manager and go to Extras, and select Google Play Services).",
                    "OK");
                return false;
            }
            // create needed directories
            EnsureDirExists("Assets/Plugins");
            EnsureDirExists("Assets/Plugins/Android");

            // clear out the destination library project
            DeleteDirIfExists(libProjDestDir);

            // Copy Google Play Services library
            FileUtil.CopyFileOrDirectory(libProjPath, libProjDestDir);

            return true;
        }

        public static void EnsureDirExists(string dir)
        {
            dir = dir.Replace("/", Path.DirectorySeparatorChar.ToString());
            if (!System.IO.Directory.Exists(dir))
            {
                System.IO.Directory.CreateDirectory(dir);
            }
        }

        public static void DeleteDirIfExists(string dir)
        {
            if (System.IO.Directory.Exists(dir))
            {
                System.IO.Directory.Delete(dir, true);
            }
        }
    }
}
