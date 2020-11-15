package com.ethical_techniques.notemaker.auth;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.type.DateTime;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Update profile activity.
 *
 * @author Harry Dulaney
 */
public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getName();
    private ViewGroup mainLayout;
    private ViewGroup emailUpdateLayout;
    private ValueHolder valueHolder;
    private Boolean waitingForVerifyEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainLayout = findViewById(R.id.layoutUpdateUserProfile);
        emailUpdateLayout = findViewById(R.id.email_update_layout);

    }

    private void initUIProfileInfo(FirebaseUser firebaseUser) {
        String pPath = "";
        String dName = "";
        String email = "";
        if (firebaseUser.getDisplayName() != null) {
            dName = firebaseUser.getDisplayName();
        }
        if (firebaseUser.getPhotoUrl() != null) {
            pPath = firebaseUser.getPhotoUrl().getPath();
        }
        if (firebaseUser.getEmail() != null) {
            email = firebaseUser.getEmail();
        }

        valueHolder = new ValueHolder(dName, email, pPath);

        TextInputEditText username = findViewById(R.id.editTextInputUserNameUpdate);
        if (!valueHolder.currDisName.isEmpty()) {
            username.setText(valueHolder.currDisName);
        } else {
            username.setText(R.string.dis_name_empty_message);

        }

        username.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                valueHolder.nooDisPlayName = username.getText().toString();
            }
        });

        TextView emailView = findViewById(R.id.emailAddressDisplay);
        if (!valueHolder.currEmail.isEmpty()) {
            emailView.setText(valueHolder.currEmail);
        } else {
            emailView.setText(R.string.no_email_set);

        }
        ImageView photoView = findViewById(R.id.imageView);
        Uri uriPhoto = firebaseUser.getPhotoUrl();

        if (!valueHolder.currPicPath.isEmpty()) {
            photoView.setImageURI(uriPhoto);
        }

        MaterialButton mb = findViewById(R.id.verifyButton);
        if (firebaseUser.isEmailVerified()) {
            mb.setVisibility(View.GONE);
        } else {
            mb.setVisibility(View.VISIBLE);
        }


    }

//    private void() {
//        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (fUser != null) {
//            UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
//            if (!nooDisName.isEmpty()) {
//                profileUpdates.setDisplayName(nooDisName);
//            } else if (!nooPicPath.isEmpty()) {
//                profileUpdates.setPhotoUri(Uri.parse(nooPicPath));
//            }
//
//            fUser.updateProfile(profileUpdates.build())
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Snackbar.make(mainLayout, "Profile has been updated", Snackbar.LENGTH_LONG).show();
//
//                        }
//                    });
//        } else {
//            Log.e(TAG, "Something has gone horribly wrong :( ");
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            initUIProfileInfo(fUser);
        } else {
            throw new SecurityException("Access Denied");
        }
    }


    /**
     * Send email verification.
     */
    public void sendEmailVerification(FirebaseUser fUser) {
        if (fUser != null) {//TODO: Replace the below url with the whitelisted one from the Firebase console.
            String url = "http://www.example.com/verify?uid=" + fUser.getUid();
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                    .setUrl(url)
                    .build();

            fUser.sendEmailVerification(actionCodeSettings)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "We sent a confirmation to your the email address associated with your account." +
                                            " Please check your email to complete verification.",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Verification email sent.");
                        }
                    });
        }
    }


    /**
     * Update name.
     *
     * @param name  the name
     * @param fUser the f user
     */
    protected void updateName(String name, FirebaseUser fUser) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        fUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Your display name has been set to: " + fUser.getDisplayName(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void launchTakePicture() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void launchPicFromStorage() {


    }


    /**
     * Called when a view within the activity_update_profile layout has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.changeProfilePic) {
            //Change picture options
            DialogUtil.makeAndShow(this,
                    "Set Profile Picture",
                    "From where do you want to retrieve your profile picture?",
                    "Take Picture",
                    "Open File Explorer",
                    "Close Popup",
                    this::launchTakePicture,
                    this::launchPicFromStorage,
                    () -> {
                        Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show();
                    });

        } else if (id == R.id.updateEmailAddressButton) {
            //launch email update ui and events
            hideKeyboard(this, findViewById(R.id.updateEmailAddressButton).getRootView());

            initUpdateEmail();
        } else if (id == R.id.handleDoneButton) {
            hideKeyboard(this, findViewById(R.id.handleDoneButton).getRootView());

            //Submit update account user profile
            onBackPressed();

        } else if (id == R.id.exitProfileUpdate) {
            hideKeyboard(this, findViewById(R.id.exitProfileUpdate).getRootView());
            finish();
        } else if (id == R.id.verifyButton) {
            //Launch verify email event sequence
            hideKeyboard(this, findViewById(R.id.submitUpdateEmailAddress).getRootView());

            sendEmailVerification(FirebaseAuth.getInstance().getCurrentUser());


        } else if (id == R.id.handleClearForm) {
            hideKeyboard(this, findViewById(R.id.handleClearForm).getRootView());

            TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
            TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
            TextInputEditText pw = findViewById(R.id.pword);
            Objects.requireNonNull(nooEmail.getText()).clear();
            Objects.requireNonNull(nooEmailCheck.getText()).clear();
            Objects.requireNonNull(pw.getText()).clear();

        } else if (id == R.id.submitUpdateEmailAddress) {
            hideKeyboard(this, findViewById(R.id.submitUpdateEmailAddress).getRootView());

            final TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
            final TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
            final TextInputEditText pw = findViewById(R.id.pword);

            if (nooEmail.length() < 3 || nooEmailCheck.length() < 3 || pw.length() < 8) {

                if (nooEmail.getText().equals(nooEmailCheck.getText())) {
                    if (validateEmail(nooEmail.getText())) {
                        if (validatePassword(pw.getText())) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential creds = EmailAuthProvider
                                    .getCredential(valueHolder.currEmail, pw.getText().toString());
                            user.reauthenticate(creds)
                                    .addOnCompleteListener(task -> {
                                        Log.d(TAG, "User re-authenticated.");
                                        try {
                                            updateUserEmailAddress(user, nooEmail.getText().toString());
                                        } catch (FirebaseAuthEmailException | FirebaseAuthInvalidCredentialsException fee) {
                                            fee.printStackTrace();
                                        } catch (FirebaseAuthUserCollisionException ffce) {
                                            Toast.makeText(this, "The email address you entered already belongs to an existing account, please re-try with a different email address.", Toast.LENGTH_LONG).show();

                                        }

                                    });

                        } else {
                            Toast.makeText(this, "The password you entered appears to be in an invalid format, please check again and re-try.", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(this, "The email address you entered is not a valid format, please double check.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(this, "The both email address above must be equal, please check again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all of the fields before pressing the Submit button.", Toast.LENGTH_SHORT).show();

            }


        } else {
            Log.e(TAG, "Unknown or unhandled view clicked");
        }

    }

    private void updateUserEmailAddress(FirebaseUser firebaseUser, String str) throws FirebaseAuthInvalidCredentialsException,
            FirebaseAuthEmailException, FirebaseAuthUserCollisionException {

        firebaseUser.updateEmail(str)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Snackbar.make(emailUpdateLayout, "Your email address has been updated, please use the button on the next screen to verify.", Snackbar.LENGTH_LONG);
                                Log.d(TAG, "User email address updated.");
                                reInitMainLayout();
                            } else {
                                Snackbar.make(emailUpdateLayout, "Something went wrong, we could not update your email address. Please visit our github page for help.", Snackbar.LENGTH_LONG);
                                reInitMainLayout();
                            }
                        }
                );
    }

    private void initUpdateEmail() {
        mainLayout.setVisibility(View.GONE);
        emailUpdateLayout.setVisibility(View.VISIBLE);

    }

    private void reInitMainLayout() {
        emailUpdateLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            if (valueHolder.nooDnLen() > 0 && valueHolder.isCurrDnDiff()) {
                updateName(valueHolder.nooDisPlayName, fuser);
            }
        }
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);

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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + "notes_for_android_prof_pic";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        valueHolder.currPicPath = image.getAbsolutePath();
        return image;
    }

    private static class ValueHolder {
        private String nooDisPlayName;
        private String nooPicPath;

        private String currPicPath;
        private String currEmail;
        private String currDisName;

        ValueHolder() {
            throw new SecurityException("Unsupported Operation");
        }

        ValueHolder(String currDisName, String currEmail, String currPicPath) {
            this.currDisName = currDisName;
            this.currEmail = currEmail;
            this.currPicPath = currPicPath;

        }

        int nooDnLen() {
            return nooDisPlayName.length();
        }

        int picStrLen() {
            return nooPicPath.length();
        }

        public boolean isCurrDnDiff() {
            return !nooDisPlayName.equals(currDisName);
        }
    }

}
