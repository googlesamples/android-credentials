// <copyright file="DummySmartLockCredentials.cs" company="Google Inc.">
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

namespace Google.SmartLock.Platforms
{
    /// <summary>
    /// Dummy smart lock credentials. Is used on non-Android players since
    /// the API is only supported on Android.  Sample credential can be set
    /// statically using SetSampleCredential().
    /// </summary>
    public class DummySmartLockCredentials : ISmartLockCredentialsImpl
    {

        private static ICredential sampleCredential;

        /// <summary>
        /// Sets the sample credential to return when calling Load.
        /// </summary>
        /// <returns>the previous sample credential, or null if there was none.</returns>
        /// <param name="newCredential">New credential.</param>
        public static ICredential SetSampleCredential(ICredential newCredential)
        {
            ICredential old = sampleCredential;
            sampleCredential = newCredential;
            return old;
        }

        #region ISmartLockCredentialsImpl implementation


        /// <see cref="ISmartLockCredentialsImpl.Load"/>
        public void Load(System.Action<Status, ICredential> callback, params string[] accountTypes)
        {
            if (sampleCredential == null)
            {
                sampleCredential = new Credential("nobody@google.com",
                    "http://dummy-accounts@example.com",
                    "Sample User", "password123", null);
            }
            callback(Status.Success, sampleCredential);
        }

        /// <see cref="ISmartLockCredentialsImpl.Save"/>
        public void Save(ICredential credential, System.Action<Status, ICredential> callback)
        {
            callback(Status.DeveloperError, null);
        }

        /// <see cref="ISmartLockCredentialsImpl.Delete"/>
        public void Delete(ICredential credential, System.Action<Status, ICredential> callback)
        {
            callback(Status.DeveloperError, null);
        }

        #endregion
    }
}
