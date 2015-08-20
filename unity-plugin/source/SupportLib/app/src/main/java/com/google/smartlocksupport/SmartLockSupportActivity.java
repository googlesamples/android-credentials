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

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;

/**
 * SmartLockSupportActivity is used to launch an invisible activity to
 * handle calling the SmartLock for Credentials API from an external plugin (such as for Unity).
 * The general design is:
 * 1. a static method is called passing the parent activity, the parameters for the API call, and
 * a response handler object.
 * 2. The intent is created and the parameters and operation code are added as extra data to the intent.
 * The handler object is stored in a static hash map.  The key is added to the intent as extra data.
 * 3. The activity is started.  OnStart reads the extra data and performs the requested API calls.
 * 4. When processing is complete (either successfully, or an error) the result handler is invoked.
 */
public class SmartLockSupportActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SmartLockSupportActvty";

    // These are the API calls that can be made.
    private static final int RC_SAVE = 1;
    private static final int RC_READ = 3;
    private static final int RC_DELETE = 4;

    // Keys to the extra data in the intent.
    private static final String OperationKey = "op";
    private static final String ResponseHandlerKey = "responseHandler";
    private static final String AccountTypesKey = "accountTypes";
    private static final String AccountTypeKey = "accountType";
    private static final String EmailKey = "email";
    private static final String PasswordKey = "password";
    private static final String NameKey = "name";
    private static final String AvatarURLKey = "avatar";

    private GoogleApiClient mCredentialsApiClient;

    // static map of callback objects.
    private static HashMap<String, SmartLockSupportResponseHandler> responseHandlers = new HashMap<>();


    /**
     * Perform the Load SDK call to load credentials.
     *
     * @param parentActivity  - the activity to be the parent.
     * @param responseHandler - the handler object to call back when complete.
     * @param accountTypes    - the array of account types to limit the credential returned.
     */
    public static void doLoad(Activity parentActivity,
                              SmartLockSupportResponseHandler responseHandler,
                              String... accountTypes
    ) {
        String responseHandlerKey = null;
        try {
            Log.d(TAG, "Called doLoad");
            Intent intent = new Intent(parentActivity, SmartLockSupportActivity.class);
            responseHandlerKey = responseHandler.hashCode() + "x" + Long.toHexString(System.currentTimeMillis());
            responseHandlers.put(responseHandlerKey, responseHandler);
            intent.putExtra(ResponseHandlerKey, responseHandlerKey)
                    .putExtra(AccountTypesKey, accountTypes)
                    .putExtra(OperationKey, RC_READ);
            Log.d(TAG, "Starting intent!");
            parentActivity.startActivity(intent);
        } catch (Throwable t) {
            Log.e(TAG, "Got a throwable, cannot start activity " + t.getMessage());
            // if there was a problem starting, just fail.
            responseHandlers.remove(responseHandlerKey);
            responseHandler.OnResult(CommonStatusCodes.ERROR, null, null, null, null, null);
        } finally {
            Log.d(TAG, "Returning from doLoad");
        }
    }


    /**
     * Perform the Save SDK call to store credentials.  The credential is flattened out
     * to make it easy to marshal the call into other environments vs. dealing with marshalling
     * complex types.
     *
     * @param parentActivity  - the activity to be the parent.
     * @param responseHandler - the handler object to call back when complete.
     * @param email           - email or user id for the credential.
     * @param password        - the password
     * @param accountType     - the account type of the credential.  null is allowed.
     * @param name            - the display name of the user.
     * @profilePicutureURL - the url for the profile image for the credential.
     */
    public static void doSave(Activity parentActivity, SmartLockSupportResponseHandler responseHandler,
                              String email,
                              String password,
                              String accountType,
                              String name,
                              String profilePicutureURL) {
        Log.d(TAG, "Called doSave");
        String responseHandlerKey = null;
        try {
            Intent intent = new Intent(parentActivity, SmartLockSupportActivity.class);
            responseHandlerKey = responseHandler.hashCode() + "x" + Long.toHexString(System.currentTimeMillis());
            responseHandlers.put(responseHandlerKey, responseHandler);
            intent.putExtra(ResponseHandlerKey, responseHandlerKey)
                    .putExtra(EmailKey, email)
                    .putExtra(PasswordKey, password)
                    .putExtra(AccountTypeKey, accountType)
                    .putExtra(NameKey, name)
                    .putExtra(AvatarURLKey, profilePicutureURL)
                    .putExtra(OperationKey, RC_SAVE);
            parentActivity.startActivity(intent);
        } catch (Throwable t) {
            Log.e(TAG, "Got a throwable, cannot start activity " + t.getMessage());
            // if there was a problem starting, just fail.
            responseHandlers.remove(responseHandlerKey);
            responseHandler.OnResult(CommonStatusCodes.ERROR, null, null, null, null, null);
        } finally {
            Log.d(TAG, "Returning from doSave");
        }
    }


    /**
     * Performs the delete SDK call to delete the specfied credential.
     *
     * @param parentActivity  - the activity to be the parent.
     * @param responseHandler - the handler object to call back when complete.
     * @param email           - email or user id for the credential.
     * @param password        - the password
     * @param accountType     - the account type of the credential.  null is allowed.
     */
    public static void doDelete(Activity parentActivity, SmartLockSupportResponseHandler responseHandler,
                                String email,
                                String password,
                                String accountType) {
        Log.d(TAG, "Called doDelete");
        String responseHandlerKey = null;
        try {
            Intent intent = new Intent(parentActivity, SmartLockSupportActivity.class);
            responseHandlerKey = responseHandler.hashCode() + "x" + Long.toHexString(System.currentTimeMillis());
            responseHandlers.put(responseHandlerKey, responseHandler);
            intent.putExtra(ResponseHandlerKey, responseHandlerKey)
                    .putExtra(EmailKey, email)
                    .putExtra(PasswordKey, password)
                    .putExtra(AccountTypeKey, accountType)
                    .putExtra(OperationKey, RC_DELETE);
            parentActivity.startActivity(intent);
        } catch (Throwable t) {
            Log.e(TAG, "Got a throwable, cannot start activity " + t.getMessage());
            // if there was a problem starting, just fail.
            responseHandlers.remove(responseHandlerKey);
            responseHandler.OnResult(CommonStatusCodes.ERROR, null, null, null, null, null);
        } finally {
            Log.d(TAG, "Returning from doDelete");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate called");
        super.onCreate(savedInstanceState);
        // Instantiate GoogleApiClient.  This is a very simple GoogleApiClient that only connects
        // to the Auth.CREDENTIALS_API, which does not require the user to go through the sign-in
        // flow before connecting.  If you are going to use this API with other Google APIs/scopes
        // that require sign-in (such as Google+ or Google Drive), it may be useful to separate
        // the CREDENTIALS_API into its own GoogleApiClient with separate lifecycle listeners.
        mCredentialsApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

    }

    @Override
    public void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        mCredentialsApiClient.connect();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        mCredentialsApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected called");

        // Now that we are connected, figure out what needs to be done and do it.
        Intent intent = getIntent();
        int op = intent.getIntExtra(OperationKey, -1);
        SmartLockSupportResponseHandler responseHandler = responseHandlers.get(
                intent.getStringExtra(ResponseHandlerKey));

        String accountType = intent.getStringExtra(AccountTypeKey);
        String email = intent.getStringExtra(EmailKey);
        String password = intent.getStringExtra(PasswordKey);
        String name = intent.getStringExtra(NameKey);
        String avatarURL = intent.getStringExtra(AvatarURLKey);

        switch (op) {
            case RC_READ:
                String accountTypes[] = intent.getStringArrayExtra(AccountTypesKey);
                loadCredentials(responseHandler, accountTypes);
                break;
            case RC_SAVE:
                saveCredential(responseHandler, email, password, accountType, name, avatarURL);
                break;
            case RC_DELETE:
                deleteCredential(accountType, email, password, responseHandler);
                break;
            default:
                Log.w(TAG, "No operation key in the extras, finishing activity");
                callbackAndFinish(responseHandler, CommonStatusCodes.DEVELOPER_ERROR,
                        null, null, null, null, null);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended:" + cause);
        mCredentialsApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);


        switch (requestCode) {
            case RC_READ:
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    SmartLockSupportResponseHandler callback = responseHandlers.get(
                            getIntent().getStringExtra(ResponseHandlerKey));
                    processRetrievedCredential(credential, callback);
                } else {
                    Log.e(TAG, "Credential Read: NOT OK");
                    SmartLockSupportResponseHandler callback = responseHandlers.get(
                            getIntent().getStringExtra(ResponseHandlerKey));
                    callbackAndFinish(callback, CommonStatusCodes.CANCELED, null, null, null, null, null);
                }
                break;
            case RC_SAVE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Credential Save: OK");
                    SmartLockSupportResponseHandler callback = responseHandlers.get(
                            getIntent().getStringExtra(ResponseHandlerKey));
                    callbackAndFinish(callback, resultCode, null, null, null, null, null);
                } else {
                    Log.e(TAG, "Credential Save: NOT OK");
                    SmartLockSupportResponseHandler callback = responseHandlers.get(
                            getIntent().getStringExtra(ResponseHandlerKey));
                    callbackAndFinish(callback, resultCode, null, null, null, null, null);
                }
                break;
        }
    }

    /**
     * Invokes the result handler and cleans up the activity.
     *
     * @param responseHandler   - the response handler to invoke.
     * @param resultCode        - the result code of the operation.
     * @param id                - the credential id
     * @param password          - the credential password.
     * @param name              - the credential display name.
     * @param accountType       - the account type
     * @param profilePictureURL - the profile picture.
     */
    private void callbackAndFinish(SmartLockSupportResponseHandler responseHandler,
                                   int resultCode, String id, String password, String name,
                                   String accountType, String profilePictureURL) {
        try {
            Log.d(TAG, "Calling responseHandler with code: " + resultCode);

            responseHandler.OnResult(resultCode, id != null ? id : "",
                    password != null ? password : "",
                    name != null ? name : "",
                    accountType != null ? accountType : "",
                    profilePictureURL != null ? profilePictureURL : "");
        } catch (Throwable t) {
            Log.w(TAG, "Caught throwable calling responseHandler: " + t.getMessage());
        } finally {
            Log.d(TAG, "Finishing");
            responseHandlers.remove(getIntent().getStringExtra(ResponseHandlerKey));
            finish();
        }
    }

    /**
     * Save the given credential information.
     *
     * @param responseHandler   - the response handler to invoke.
     * @param id                - the credential id
     * @param password          - the credential password.
     * @param name              - the credential display name.
     * @param accountType       - the account type
     * @param profilePictureURL - the profile picture.
     */
    public void saveCredential(final SmartLockSupportResponseHandler responseHandler,
                               String id,
                               String password,
                               String accountType,
                               String name,
                               String profilePictureURL) {


        try {

            // Create a Credential with the user's email as the ID and storing the password.  We
            // could also add 'Name' and 'ProfilePictureURL' but that is outside the scope of this
            // minimal sample.
            Log.d(TAG, "Saving Credential type:" + accountType + ":" + id + ": " + name + " " + profilePictureURL);
            Credential.Builder builder = new Credential.Builder(id);

            if (password != null) {
                builder.setPassword(password);
            }
            if (accountType != null && !accountType.isEmpty()) {
                builder.setAccountType(accountType);
            }
            if (name != null) {
                builder.setName(name);
            }
            if (profilePictureURL != null) {
                builder.setProfilePictureUri(Uri.parse(profilePictureURL));
            }

            final Credential credential = builder.build();


            // NOTE: this method unconditionally saves the Credential built, even if all the fields
            // are blank or it is invalid in some other way.  In a real application you should contact
            // your app's back end and determine that the credential is valid before saving.
            Auth.CredentialsApi.save(mCredentialsApiClient, credential).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "SAVE: OK");
                                callbackAndFinish(responseHandler, status.getStatusCode(), credential.getId(),
                                        credential.getPassword(), credential.getName(),
                                        credential.getAccountType(), credential.getProfilePictureUri().toString());
                            } else {
                                resolveResult(status, RC_SAVE, responseHandler);
                            }
                        }
                    });

        }
        catch (Throwable t) {
            Log.e(TAG, "Exception caught: " + t.getMessage());
            callbackAndFinish(responseHandler, CommonStatusCodes.DEVELOPER_ERROR, null, null,null,null,null);
        }
    }

    /**
     * Attempts to read the user's saved
     * Credentials from the Credentials API.
     * <p/>
     * <b>Note:</b> Make sure not to load credentials automatically if the user has clicked
     * a "sign out" button in your application in order to avoid a sign-in loop.
     */
    private void loadCredentials(final SmartLockSupportResponseHandler callback,
                                 String... accountTypes) {
        // Request all of the user's saved username/password credentials.  We are not using
        // setAccountTypes so we will not load any credentials from other Identity Providers.
        CredentialRequest request = new CredentialRequest.Builder()
                .setSupportsPasswordLogin(true)
                .setAccountTypes(accountTypes)
                .build();

        Auth.CredentialsApi.request(mCredentialsApiClient, request).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            // Successfully read the credential without any user interaction, this
                            // means there was only a single credential and the user has auto
                            // sign-in enabled.
                            processRetrievedCredential(credentialRequestResult.getCredential(),
                                    callback);
                        } else {
                            // Reading the credential requires a resolution, which means the user
                            // may be asked to pick among multiple credentials if they exist.
                            Status status = credentialRequestResult.getStatus();
                            // This is most likely the case where the user has multiple saved
                            // credentials and needs to pick one
                            resolveResult(status, RC_READ, callback);
                        }
                    }
                });
    }

    /**
     * Deletes the credential information.
     *
     * @param responseHandler - the response handler to invoke.
     * @param id              - the credential id
     * @param password        - the credential password.
     * @param accountType     - the account type
     */
    public void deleteCredential(String accountType, String id, String password,
                                 final SmartLockSupportResponseHandler responseHandler) {
        Credential.Builder builder = new Credential.Builder(id);

        if (password != null) {
            builder.setPassword(password);
        }
        if (accountType != null && !accountType.isEmpty()) {
            builder.setAccountType(accountType);
        }

        final Credential credential = builder.build();

        Log.d(TAG, "Deleting credential " + id);

        Auth.CredentialsApi.delete(mCredentialsApiClient, credential).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "deleted: OK");
                            callbackAndFinish(responseHandler, status.getStatusCode(), null, null, null, null, null);
                        } else {
                            resolveResult(status, RC_SAVE, responseHandler);
                        }
                    }
                });


    }

    /**
     * Attempt to resolve a non-successful Status from an asynchronous request.
     *
     * @param status      the Status to resolve.
     * @param requestCode the request code to use when starting an Activity for result,
     */
    private void resolveResult(Status status, int requestCode, SmartLockSupportResponseHandler callback) {
        Log.d(TAG, "Resolving: " + status);
        if (status.hasResolution()) {
            Log.d(TAG, "STATUS: RESOLVING");
            try {
                status.startResolutionForResult(SmartLockSupportActivity.this, requestCode);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "STATUS: Failed to send resolution.", e);
                callbackAndFinish(callback, CommonStatusCodes.INTERNAL_ERROR, null, null, null, null, null);
            }
        } else {
            Log.e(TAG, "STATUS: FAIL");
            callbackAndFinish(callback, CommonStatusCodes.INTERNAL_ERROR, null, null, null, null, null);
        }
    }

    /**
     * Process a Credential object retrieved from a successful request.
     *
     * @param credential the Credential to process.
     */
    private void processRetrievedCredential(Credential credential,
                                            SmartLockSupportResponseHandler callback) {
        Log.d(TAG, "Credential Retrieved: " + credential.getId());

        callbackAndFinish(callback, CommonStatusCodes.SUCCESS,
                credential.getId(),
                credential.getPassword(),
                credential.getName(),
                credential.getAccountType(),
                credential.getProfilePictureUri() != null ?credential.getProfilePictureUri().toString():null);
    }
}
