package com.ethical_techniques.notemaker.auth;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;

public class AuthManager {

    private AuthCredential credential;
    private OAuthCredential oAuthCredential;
    private OAuthProvider provider;
    private FirebaseAuth firebaseAuth;

    private AuthManager() {
        throw new IllegalArgumentException("Access Denied, this object is read-only");
    }


}
