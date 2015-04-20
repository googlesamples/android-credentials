/*
 * Copyright Google Inc. All Rights Reserved.
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
package com.google.example.credentialsbasic;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * A minimal example of saving and loading username/password credentials from the Credentials API.
 * @author samstern@google.com
 */
public class MainActivity extends ActionBarActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SAVE = 1;
    private static final int RC_READ = 2;

    private EditText mEmailField;
    private EditText mPasswordField;

    private GoogleApiClient mCredentialsApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fields
        mEmailField = (EditText) findViewById(R.id.edit_text_email);
        mPasswordField = (EditText) findViewById(R.id.edit_text_password);

        // Buttons
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_load_credentials).setOnClickListener(this);

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
        super.onStart();
        mCredentialsApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCredentialsApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                processRetrievedCredential(credential);
            } else {
                Log.e(TAG, "Credential Read: NOT OK");
                showToast("Credential Read Failed");
            }
        } else if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Credential Save: OK");
                showToast("Credential Save Success");
            } else {
                Log.e(TAG, "Credential Save: NOT OK");
                showToast("Credential Save Failed");
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        findViewById(R.id.button_load_credentials).setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended:" + i);
        mCredentialsApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        findViewById(R.id.button_load_credentials).setEnabled(false);
    }

    /**
     * Called when the Sign In button is clicked.  Reads the entries in the email and password
     * fields and attempts to save a new Credential to the Credentials API.
     */
    private void signInClicked() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        // Create a Credential with the user's email as the ID and storing the password.  We
        // could also add 'Name' and 'ProfilePictureURL' but that is outside the scope of this
        // minimal sample.
        Credential credential = new Credential.Builder(email)
                .setPassword(password)
                .build();

        // NOTE: this method unconditionally saves the Credential built, even if all the fields
        // are blank or it is invalid in some other way.  In a real application you should contact
        // your app's back end and determine that the credential is valid before saving it to the
        // Credentials backend since there is no API to delete an existing Credential.
        Auth.CredentialsApi.save(mCredentialsApiClient, credential).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "SAVE: OK");
                            showToast("Credential Saved");
                        } else {
                            resolveResult(status, RC_SAVE);
                        }
                    }
                });
    }

    /**
     * Called when the Load Credentials button is clicked. Attempts to read the user's saved
     * Credentials from the Credentials API.
     *
     * <b>Note:</b> in a normal application loading credentials should happen without explicit user
     * action, this is only connected to a 'Load Credentials' button for easier demonstration
     * in this sample.  Make sure not to load credentials automatically if the user has clicked
     * a "sign out" button in your application in order to avoid a sign-in loop.
     */
    private void loadCredentialsClicked() {
        // Request all of the user's saved username/password credentials.  We are not using
        // setAccountTypes so we will not load any credentials from other Identity Providers.
        CredentialRequest request = new CredentialRequest.Builder()
                .setSupportsPasswordLogin(true)
                .build();

        Auth.CredentialsApi.request(mCredentialsApiClient, request).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            // Successfully read the credential without any user interaction, this
                            // means there was only a single credential and the user has auto
                            // sign-in enabled.
                            processRetrievedCredential(credentialRequestResult.getCredential());
                        } else {
                            // Reading the credential requires a resolution, which means the user
                            // may be asked to pick among multiple credentials if they exist.
                            resolveResult(credentialRequestResult.getStatus(), RC_READ);
                        }
                    }
                });
    }

    /**
     * Attempt to resolve a non-successful Status from an asynchronous request.
     * @param status the Status to resolve.
     * @param requestCode the request code to use when starting an Activity for result,
     *                    this will be passed back to onActivityResult.
     */
    private void resolveResult(Status status, int requestCode) {
        Log.d(TAG, "Resolving: " + status);
        if (status.hasResolution()) {
            Log.d(TAG, "STATUS: RESOLVING");
            try {
                status.startResolutionForResult(MainActivity.this, requestCode);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "STATUS: Failed to send resolution.", e);
            }
        } else {
            Log.e(TAG, "STATUS: FAIL");
            showToast("Could Not Resolve Error");
        }
    }

    /**
     * Process a Credential object retrieved from a successful request.
     * @param credential the Credential to process.
     */
    private void processRetrievedCredential(Credential credential) {
        Log.d(TAG, "Credential Retrieved: " + credential.getId());
        showToast("Credential Retrieved");

        mEmailField.setText(credential.getId());
        mPasswordField.setText(credential.getPassword());
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in:
                signInClicked();
                break;
            case R.id.button_load_credentials:
                loadCredentialsClicked();
                break;
        }
    }


}
