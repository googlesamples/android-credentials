// <copyright file="SmartLockCredentials.cs" company="Google Inc.">
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

namespace Google.SmartLock
{
    /// <summary>
    /// Smart lock credentials API.  This is a singleton pattern, use
    /// the Instance property to access the API.
    /// </summary>
    public class SmartLockCredentials : ISmartLockCredentialsImpl
    {
        private static ISmartLockCredentialsImpl theInstance;

        static SmartLockCredentials()
        {
            if (Application.isEditor)
            {
                theInstance = new Google.SmartLock.Platforms.DummySmartLockCredentials();
            }
            else
            {
                #if UNITY_ANDROID
                theInstance = Google.SmartLock.Platforms.Android.SmartLockAndroid.GetCredentialAPI();
                #elif UNITY_IOS
                 // currently no support for iOS, return a dummy api with
                // and empty credential.
                theInstance = DummySmartLockCredentials();
                ((DummySmartLockCredentials)theInstance).SetSampleCredential(
                        new Credential("","","","","","",""));
                #else
                theInstance = new Google.SmartLock.Platforms.DummySmartLockCredentials();
                #endif
            }
        }

        public  static ISmartLockCredentialsImpl Instance
        {
            get
            {
                return theInstance;
            }
        }

        #region ISmartLockCredentialsImpl implementation

        /// <see cref="ISmartLockCredentialsImpl.Load"/>
        public void Load(System.Action<Status, ICredential> callback, params string[] accountTypes)
        {
            Instance.Load(callback, accountTypes);
        }

        /// <see cref="ISmartLockCredentialsImpl.Save"/>
        public void Save(ICredential credential, Action<Status, ICredential> callback)
        {
            Instance.Save(credential, callback);
        }

        /// <see cref="ISmartLockCredentialsImpl.Delete"/>
        public void Delete(ICredential credential, Action<Status, ICredential> callback)
        {
            Instance.Delete(credential, callback);
        }

        #endregion
    }
}
