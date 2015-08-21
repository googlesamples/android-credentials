// <copyright file="ISmartLockCredentialsImpl.cs" company="Google Inc.">
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

using System;

namespace Google.SmartLock
{

    /// <summary>
    /// Interface defining the API for Smart Lock for Passwords and Connected
    /// Accounts.
    /// See: https://developers.google.com/android/reference/com/google/android/gms/auth/api/credentials/package-summary
    /// </summary>
    public interface ISmartLockCredentialsImpl
    {
        /// <summary>
        /// Loads the credential for the given account Types.
        /// </summary>
        /// <param name="callback">Callback.</param>
        /// <param name="accountTypes">Account types.</param>
        void Load(Action<Status, ICredential> callback, params string[] accountTypes);

        /// <summary>
        /// Saves the credential and invokes the callback.
        /// </summary>
        /// <param name="credential">Credential.</param>
        /// <param name="callback">Callback.</param>
        void Save(ICredential credential, Action<Status, ICredential> callback);

        /// <summary>
        /// Deletes the credential and invokes the callback.
        /// </summary>
        /// <param name="credential">Credential.</param>
        /// <param name="callback">Callback.</param>
        void Delete(ICredential credential, Action<Status, ICredential> callback);
    }
}
