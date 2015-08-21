﻿// <copyright file="SmartLockAndroid.cs" company="Google Inc.">
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

#if UNITY_ANDROID
namespace Google.SmartLock.Platforms.Android
{
    /// <summary>
    /// Class to hold the factory method of AndroidCredential instance.
    /// </summary>
    public class SmartLockAndroid
    {
        private static AndroidCredentials credImpl;

        public static ISmartLockCredentialsImpl GetCredentialAPI()
        {
            if (credImpl == null)
            {
                credImpl = new AndroidCredentials();
            }
            return credImpl;
        }
    }
}
#endif
