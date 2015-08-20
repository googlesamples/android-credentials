/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.smartlocksupport;

/**
 * Interface for the response handler when calling the SmartLock credential API.  Callers
 * of the API need to provide and objec that implements this interface when calling the API.
 */
public interface SmartLockSupportResponseHandler
{

    /**
     * The callback method for handling the result.  The method is flattens out the credential
     * information to simplify the marshalling to other systems (such as Unity).
     * @param resultCode - the result code.
     * @param id - the credential id
     * @param password - the credential password
     * @param name - the credential display name.
     * @param accountType - the credential account type.
     * @param profilePictureURL - the profile picture URL.
     */
    void OnResult(int resultCode, String id, String password, String name, String accountType, String profilePictureURL);

    @Override
    int hashCode();

    @Override
    String toString();

}
