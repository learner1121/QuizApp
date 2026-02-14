package com.gautam.quiz_app.auth

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.gautam.quiz_app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class GoogleAuthActivity : ComponentActivity() {
    // Activity to handle Google Auth
    private val TAG = "GoogleAuthActivity"// Tag for logging
    private lateinit var firebaseAuthManager: FirebaseAuthManager// Our custom Firebase manager
    private lateinit var credentialManager: CredentialManager   // Credential manager instance

    override fun onCreate(savedInstanceState: Bundle?) {    // Called when Activity is created
        super.onCreate(savedInstanceState)
        firebaseAuthManager = FirebaseAuthManager(this) // Init FirebaseAuth manager
        credentialManager = CredentialManager.create(this)  // Init Credential Manager

        // Start Google Sign-In immediately (instead of waiting for button click)
        startGoogleSignIn()
    }

    private fun startGoogleSignIn() {
        // Build Google ID option
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))  // Server client ID
            .setFilterByAuthorizedAccounts(false)  // Show all accounts, not just authorized ones
            .build()

        // Build the credential request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(this@GoogleAuthActivity, request)
                handleSignIn(result.credential) // Handle credential after success
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is androidx.credentials.CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // null  check is very very important,without is never move ahead!!!
            if (idToken != null) {
                Log.d(TAG, "Google ID Token received")
                firebaseAuthManager.firebaseAuthWithGoogle(idToken)

                // Return success result to calling activity
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Log.w(TAG, "ID Token is null")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }
}