package com.ethical_techniques.notemaker.auth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Task;

import com.ethical_techniques.notemaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type User register activity.
 *
 * @author Harry Dulaney
 */
public class UserRegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserRegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_registration_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    private void handleSubmitNewRegistration(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        boolean emailOkay, pwOkay, pwConfirm, fbaseSuccess;
        fbaseSuccess = emailOkay = pwOkay = pwConfirm = false;

        //Check form fields are all filled normally
        TextInputEditText emailInput = findViewById(R.id.editTextEmailAddressRegister);
        TextInputEditText pOne = findViewById(R.id.editTextNewPassword);
        TextInputEditText pTwo = findViewById(R.id.editTextNewPasswordConfirm);

        emailOkay = validateEmail(emailInput.getText());
        pwOkay = validatePassword(pOne.getText());
        pwConfirm = pOne.equals(pTwo);

        if (emailOkay && pwOkay && pwConfirm) {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            fAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), pOne.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                builder.setTitle("Success!")
                                        .setMessage("Please sign in with the credentials you just created to get started!")
                                        .setPositiveButton("Login Screen", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onBackPressed();
                                            }
                                        });
                                Log.d(TAG, "createUserWithEmail:success");

                            } else {
                                Log.w(TAG, "Create new User with email: failed", task.getException());
                                Toast.makeText(UserRegisterActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


        } else {
            String errorMessage = "We found these errors:\n";
            //Registration Error
            builder.setTitle("Registration Error");
            if (!emailOkay) {
                errorMessage += "Email address is not formatted properly.\n";
            }
            if (!pwOkay) {

                errorMessage += "Password contains illegal characters.\n";

            }
            if (!pwConfirm) {

                errorMessage += "Your passwords do not match.";

            }
            builder.setMessage(errorMessage);
            builder.setPositiveButton("OK", (dialog, usersChoice) -> {
                Toast.makeText(UserRegisterActivity.this, "", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        }
    }

    /**
     * Handle reset form fields.
     *
     * @param view the view
     */
    public void handleResetFormFields(View view) {
        TextInputEditText emailInput = findViewById(R.id.editTextEmailAddressRegister);
        TextInputEditText pOne = findViewById(R.id.editTextNewPassword);
        TextInputEditText pTwo = findViewById(R.id.editTextNewPasswordConfirm);

        Objects.requireNonNull(emailInput.getText()).clear();
        Objects.requireNonNull(pOne.getText()).clear();
        Objects.requireNonNull(pTwo.getText()).clear();

        Snackbar.make(view.getRootView(), "Form Cleared", BaseTransientBottomBar.LENGTH_LONG);

    }

    private static boolean validatePassword(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < 8 || charSequence.length() > 20) {
            return false;
        }
        String pwRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        Pattern pwPattern = Pattern.compile(pwRegex);
        Matcher matcher = pwPattern.matcher(charSequence);
        return matcher.matches();
    }

    private static boolean validateEmail(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < 3) {
            return false;
        }
        final String emailValidationRegEx = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailValidationRegEx);
        Matcher matcher = emailPat.matcher(charSequence);
        return matcher.matches();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.handleRegisterSubmit) {
            handleSubmitNewRegistration(v.getRootView());
        }
    }
}
