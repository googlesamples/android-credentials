// <copyright file="AndroidCredentials.cs" company="Google Inc.">
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
using System;

#if UNITY_ANDROID
namespace Google.SmartLock.Platforms.Android
{
    /// <summary>
    /// Implementation class for SmartLock for Passwords on
    /// Android.  This builds the JNI calls to access an Activity
    /// that invokes the API.
    /// </summary>
    public class AndroidCredentials : ISmartLockCredentialsImpl
    {
        private const string SupportActivityClassname =
            "com.google.smartlocksupport.SmartLockSupportActivity";

        private AndroidJavaClass supportActivityClass;

        #region ISmartLockCredentialsImpl implementation

        /// <see cref="ISmartLockCredentialsImpl.Load"/>
        public void Load(Action<Status, ICredential> callback, params string[] accountTypes)
        {
            AndroidJavaClass supportClass = GetSupportClass();
            if (supportClass == null)
            {
                Debug.LogError("Cannot load java support class: " + SupportActivityClassname);
                callback(Status.Error, null);
                return;
            }

            AndroidJavaObject unityActivity = GetUnityActivity();
            if (unityActivity == null)
            {
                Debug.LogError("Cannot get Unity player activity");
                callback(Status.Error, null);
                return;
            }

            IntPtr methodId = AndroidJNI.GetStaticMethodID(supportClass.GetRawClass(),
                                  "doLoad",
                                  "(Landroid/app/Activity;" +
                                  "Lcom/google/smartlocksupport/SmartLockSupportResponseHandler;" +
                                  "[Ljava/lang/String;)V");

            if (methodId.Equals(IntPtr.Zero))
            {
                Debug.LogError("Cannot find method doLoad in java support class");
                callback(Status.Error, null);
                return;
            }

            object[] objectArray = new object[3];
            jvalue[] jArgs;
            LoadCallback cb = new LoadCallback(callback);

            objectArray[0] = unityActivity;
            objectArray[1] = cb;
            objectArray[2] = accountTypes;
            jArgs = AndroidJNIHelper.CreateJNIArgArray(objectArray);

            AndroidJNI.CallStaticVoidMethod(supportClass.GetRawClass(), methodId, jArgs);

            Debug.Log("Done calling doLoad");
        }

        /// <see cref="ISmartLockCredentialsImpl.Save"/>
        public void Save(ICredential credential, Action<Status, ICredential> callback)
        {
            AndroidJavaClass supportClass = GetSupportClass();
            if (supportClass == null)
            {
                Debug.LogError("Cannot load java support class: " + SupportActivityClassname);
                callback(Status.Error, null);
                return;
            }

            AndroidJavaObject unityActivity = GetUnityActivity();
            if (unityActivity == null)
            {
                Debug.LogError("Cannot get Unity player activity");
                callback(Status.Error, null);
                return;
            }

            IntPtr methodId = AndroidJNI.GetStaticMethodID(supportClass.GetRawClass(),
                                  "doSave",
                                  "(Landroid/app/Activity;" +
                                  "Lcom/google/smartlocksupport/SmartLockSupportResponseHandler;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;)V");

            if (methodId.Equals(IntPtr.Zero))
            {
                Debug.LogError("Cannot find method doSave in java support class");
                callback(Status.Error, null);
                return;
            }
            object[] objectArray = new object[7];
            jvalue[] jArgs;
            LoadCallback cb = new LoadCallback(callback);

            objectArray[0] = unityActivity;
            objectArray[1] = cb;
            objectArray[2] = credential.ID;
            objectArray[3] = credential.Password;
            objectArray[4] = credential.AccountType;
            objectArray[5] = credential.Name;
            objectArray[6] = credential.ProfilePictureURL;
            jArgs = AndroidJNIHelper.CreateJNIArgArray(objectArray);

            AndroidJNI.CallStaticVoidMethod(supportClass.GetRawClass(), methodId, jArgs);

            Debug.Log("Done calling doSave");
        }

        /// <see cref="ISmartLockCredentialsImpl.Delete"/>
        public void Delete(ICredential credential, Action<Status, ICredential> callback)
        {
            AndroidJavaClass supportClass = GetSupportClass();
            if (supportClass == null)
            {
                Debug.LogError("Cannot load java support class: " + SupportActivityClassname);
                callback(Status.Error, null);
                return;
            }

            AndroidJavaObject unityActivity = GetUnityActivity();
            if (unityActivity == null)
            {
                Debug.LogError("Cannot get Unity player activity");
                callback(Status.Error, null);
                return;
            }

            IntPtr methodId = AndroidJNI.GetStaticMethodID(supportClass.GetRawClass(),
                                  "doDelete",
                                  "(Landroid/app/Activity;" +
                                  "Lcom/google/smartlocksupport/SmartLockSupportResponseHandler;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;" +
                                  "Ljava/lang/String;)V");

            if (methodId.Equals(IntPtr.Zero))
            {
                Debug.LogError("Cannot find method doDelete in java support class");
                callback(Status.Error, null);
                return;
            }
            object[] objectArray = new object[5];
            jvalue[] jArgs;
            LoadCallback cb = new LoadCallback(callback);

            objectArray[0] = unityActivity;
            objectArray[1] = cb;
            objectArray[2] = credential.ID;
            objectArray[3] = credential.Password;
            objectArray[4] = credential.AccountType;
            jArgs = AndroidJNIHelper.CreateJNIArgArray(objectArray);

            AndroidJNI.CallStaticVoidMethod(supportClass.GetRawClass(), methodId, jArgs);

            Debug.Log("Done calling doDelete");
        }

        #endregion

        /// <summary>
        /// Gets the unity activity.
        /// </summary>
        /// <returns>The unity activity.</returns>
        internal AndroidJavaObject GetUnityActivity()
        {
            return new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity");
        }

        /// <summary>
        /// Gets the support class.
        /// </summary>
        /// <returns>The support class.</returns>
        internal AndroidJavaClass GetSupportClass()
        {
            if (supportActivityClass == null)
            {
                supportActivityClass = new AndroidJavaClass(SupportActivityClassname);
            }
            return supportActivityClass;
        }

        /// <summary>
        /// Unity object that implements the SmartLockSupportResponseHandler
        /// interface.  This allows a C# object to be passed to the Java code and
        /// invoked from there.
        /// </summary>
        internal class LoadCallback : AndroidJavaProxy
        {
            private Action<Status, ICredential> callback;

            internal LoadCallback(Action<Status, ICredential> callback)
                : base(
                    "com.google.smartlocksupport.SmartLockSupportResponseHandler")
            {
                this.callback = callback;
            }

            string toString()
            {
                return base.ToString();
            }

            int hashCode()
            {
                return base.GetHashCode();
            }

            void OnResult(Int32 code, String id, String password, String name,
                          String accountType, String profilePictureURL)
            {
                if ((Status)code == Status.Success)
                {
                    ICredential cred = new Credential(id, accountType, name, password,
                                           profilePictureURL);
                    callback((Status)code, cred);
                }
                else
                {
                    callback((Status)code, null);
                }
            }
        }
    }
}
#endif

