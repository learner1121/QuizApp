package com.gautam.quiz_app.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseAuthManager(private val activity: Activity) {

    private val TAG = "FirebaseAuthManager"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "signInWithCredential:success → ${user?.email}")
                    // TODO: Navigate to HomeScreen
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    //Sign in via link
    fun sendEmailLink(email: String, onResult: (Boolean, String?) -> Unit) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(
                "com.gautam.quiz_app.",
                true,
                null
            )
            .setHandleCodeInApp(true)
            .setUrl("https://quiz-app-6d875.firebaseapp.com")
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signInWithEmailLink(email: String, link: String, onResult: (Boolean, String?) -> Unit) {
        if (auth.isSignInWithEmailLink(link)) {
            auth.signInWithEmailLink(email, link)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        } else {
            onResult(false, "Invalid link")
        }
    }


    fun signOut() {
        auth.signOut()
        Log.d(TAG, "User signed out")
    }

    fun getCurrentUser() = auth.currentUser
}
