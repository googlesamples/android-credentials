// <copyright file="Credential.cs" company="Google Inc.">
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

namespace Google.SmartLock
{
    /// <summary>
    /// Credential object.  This holds the information that is contained
    /// in a Credential.
    /// </summary>
    public class  Credential :  ICredential
    {
        private string id;
        private string accountType;
        private string name;
        private string password;
        private string profilePictureURL;

        public Credential(string id, string accountType, string name,
                          string password, string profilePictureURL)
        {
            this.id = id;
            this.accountType = accountType;
            this.name = name;
            this.password = password;
            this.profilePictureURL = profilePictureURL;
        }

        #region ICredential implementation

        /// <see cref="ICredential.ID"/>
        public string ID
        {
            get
            {
                return id;
            }
            set
            {
                id = value;
            }
        }

        /// <see cref="ICredential.AccountType"/>
        public string AccountType
        {
            get
            {
                return accountType;
            }
            set
            {
                accountType = value;
            }
        }

        /// <see cref="ICredential.Name"/>
        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }

        /// <see cref="ICredential.Password"/>
        public string Password
        {
            get
            {
                return password;
            }
            set
            {
                password = value;
            }
        }

        /// <see cref="ICredential.ProfilePictureURL"/>
        public string ProfilePictureURL
        {
            get
            {
                return profilePictureURL;
            }
            set
            {
                profilePictureURL = value;
            }
        }

        #endregion
    }

}