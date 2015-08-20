// <copyright file="SampleGUI.cs" company="Google Inc.">
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

using UnityEngine;
using Google.SmartLock;

namespace Credentials
{
    /// <summary>
    /// Sample GUI for demonstrating the Credentials API.
    /// </summary>
    public class SampleGUI : MonoBehaviour
    {

        // initialize the current credential to be empty.
        private ICredential currrentCredential = new Credential("", "", "", "", "");
        private string msg = "";
        private Texture2D profilePic;
        private WWW www;

        void OnGUI()
        {
            // Adjust the size to look OK on different devices
            GUI.skin.label.fontSize = Screen.height / 32;
            GUI.skin.label.wordWrap = false;
            GUI.skin.label.alignment = TextAnchor.UpperLeft;
            GUI.skin.textField.fontSize = Screen.height / 32;
            GUI.skin.button.fontSize = Screen.height / 24;
            float width = Screen.width * .95f;
            float rowHeight = Screen.height / 6f;

            GUILayout.BeginVertical(GUILayout.Width(width), GUILayout.Height(Screen.height));

            GUILayout.Label("SmartLock Credentials", GUILayout.Height(rowHeight));
            GUILayout.Label(currrentCredential != null ? currrentCredential.Name : "");

            // Profile picture
            GUILayout.BeginHorizontal();
            GUILayout.FlexibleSpace();
            GUILayout.Label(ProfilePic);
            GUILayout.FlexibleSpace();
            GUILayout.EndHorizontal();

            // Email address
            GUILayout.BeginHorizontal();
            GUILayout.FlexibleSpace();
            GUILayout.Label(currrentCredential != null ? currrentCredential.ID : "");
            GUILayout.FlexibleSpace();
            GUILayout.EndHorizontal();

            // Edit the email (id) of the credential.
            GUILayout.BeginHorizontal();
            GUILayout.Label("ID (Email):", GUILayout.Width(Screen.width / 3f));
            currrentCredential.ID = GUILayout.TextField(currrentCredential.ID,
                GUILayout.ExpandWidth(true),
                GUILayout.Height(rowHeight * .25f));
            GUILayout.EndHorizontal();

            // Edit the password.
            GUILayout.BeginHorizontal();
            GUILayout.Label("Password:", GUILayout.Width(Screen.width / 3f));
            currrentCredential.Password = GUILayout.PasswordField(
                currrentCredential.Password != null ? currrentCredential.Password : "", "*"[0], 25,
                GUILayout.ExpandWidth(true),
                GUILayout.Height(rowHeight * .25f));
            GUILayout.EndHorizontal();

            //Some space then the status message.
            GUILayout.FlexibleSpace();
            GUILayout.Label(msg);

            // Button bar for commands.
            GUILayout.BeginHorizontal(GUILayout.Height(rowHeight));
            GUILayout.FlexibleSpace();
            if (GUILayout.Button("Load"))
            {
                SmartLockCredentials.Instance.Load(
                    (status, credential) =>
                    {
                        msg = "Got Status: " + status;
                        Debug.Log("Got Status: " + status + ": " + credential);
                        // if the status is success, a real app would use the
                        // credential to sign into the other site.  Here, we
                        // just display them.
                        if (credential != null)
                        {
                            currrentCredential = credential;
                        }
                    });
            }
            GUILayout.Space(50);
            if (GUILayout.Button("Save"))
            {
                // A real app would probably call save after the authentication
                // to the other site.  Here is just a button for the demo.
                SmartLockCredentials.Instance.Save(currrentCredential,
                    (status, credential) =>
                    {
                        msg = "Got Status: " + status;
                        Debug.Log("Got Status: " + status + ": " + credential);
                    });
            }
            GUILayout.Space(50);
            if (GUILayout.Button("Delete"))
            {
                // Nice to allow the user to delete or "forget me"
                SmartLockCredentials.Instance.Delete(currrentCredential,
                    (status, credential) =>
                    {
                        msg = "Got Status: " + status;
                        Debug.Log("Got Status: " + status + ": " + credential);
                        currrentCredential = new Credential("", "", "", "", "");
                    });
            }
            GUILayout.FlexibleSpace();
            GUILayout.EndHorizontal();

            GUILayout.EndVertical();
        }

        /// <summary>
        /// Gets the profile pic.
        /// </summary>
        /// <value>The profile pic.</value>
        Texture2D ProfilePic
        {
            get
            {
                if (currrentCredential != null && profilePic == null &&
                    !string.IsNullOrEmpty(currrentCredential.ProfilePictureURL))
                {
                    if (www == null || www.url != currrentCredential.ProfilePictureURL)
                    {
                        www = new WWW(currrentCredential.ProfilePictureURL);
                    }
                    if (profilePic == null && www.isDone)
                    {
                        profilePic = www.texture;
                    }
                }
                return profilePic;
            }
        }
    }
}
