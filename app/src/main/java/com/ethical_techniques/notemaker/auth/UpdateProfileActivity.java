package com.ethical_techniques.notemaker.auth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ethical_techniques.notemaker.R;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.ethical_techniques.notemaker.utils.FileUtil;
import com.ethical_techniques.notemaker.utils.PictureUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Update profile activity.
 *
 * @author Harry Dulaney
 */
public class UpdateProfileActivity extends BaseActivity {

    private final String TAG = getClass().getName();
    private ViewGroup mainLayout;
    private ViewGroup emailUpdateLayout;
    private UserHolder userHolder;
    private boolean emailUpdateScreen;
    private Menu menu;
    private final int PERMISSION_REQUEST_CAMERA = 103;
    private final int CAMERA_REQUEST_CODE = 1888;
    public final int PICK_PHOTO_REQUEST_CODE = 3496;
    ImageView profilePicture;
    private String TagForFile = "notesForAndroid";
    private StorageReference profPicStorageRef;

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
        profilePicture = findViewById(R.id.imageView);
        //Set CloudStore Reference
        initCloudStorageUtil();
    }

    private void initUIProfileInfo(FirebaseUser firebaseUser) {
        userHolder = new UserHolder();

        //Reference the display name UI elements
        TextInputEditText txtInEdiTxtDisName = findViewById(R.id.editTextInputUserNameUpdate);
        TextView disNameTextView = findViewById(R.id.displayNameTextView);
        TextInputLayout txtInpLayDisName = findViewById(R.id.textInLayDisName);
        ImageButton disNameEditButton = findViewById(R.id.editDisNameButton);

        String dName = "";
        String email = "";


        if (firebaseUser.getDisplayName() != null) {
            dName = firebaseUser.getDisplayName();
        }
        if (firebaseUser.getPhotoUrl() != null) {
            userHolder.profPicPath = firebaseUser.getPhotoUrl().toString();
            Log.e(TAG, "Picture Path in initUserProfile:" + userHolder.profPicPath);
        }
        if (firebaseUser.getEmail() != null) {
            email = firebaseUser.getEmail();
        } else {
            email = "none";
        }
        if (dName == null || dName.equals("")) {
            dName = "display_name_placeholder";
        }
        userHolder.currDisName = dName;
        userHolder.currEmail = email;


        try {
            userHolder.profPicFile = FileUtil.getMakeFileByType(dName, FileUtil.PHOTO, UpdateProfileActivity.this);
            profPicStorageRef.getFile(userHolder.profPicFile)
                    .addOnSuccessListener(taskSnapshot -> Log.e(TAG, "Downloaded user profile picture to temp file"))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to download the users profile picture from Firebase cloud storage" +
                                " to the local temp file.");


                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Store view references in the userHolder for view switching */
        userHolder.txtInEdiTxtDisName = txtInEdiTxtDisName;
        userHolder.disNameTextView = disNameTextView;
        userHolder.txtInpLayDisName = txtInpLayDisName;
        userHolder.disNameEditButton = disNameEditButton;

        /* Set users current display name to UI elements */
        if (!userHolder.currDisName.isEmpty()) {
            txtInEdiTxtDisName.setText(userHolder.currDisName);
            disNameTextView.setText(userHolder.currDisName);
        }

        userHolder.txtInEdiTxtDisName.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() != 0) {
                    userHolder.currDisName = s.toString();
                    disNameTextView.setText(userHolder.currDisName);

                }
            }
        });

        txtInpLayDisName.setEndIconOnClickListener(v -> {
            updateName(userHolder.currDisName, firebaseUser);
            toggleDisplayNameInput(userHolder.disNameTextView.getRootView());
        });

        TextView emailView = findViewById(R.id.emailAddressDisplay);
        if (!userHolder.currEmail.isEmpty()) {
            emailView.setText(userHolder.currEmail);
        } else {
            emailView.setText(R.string.no_email_set);
        }


        MaterialButton mb = findViewById(R.id.verifyButton);
        if (firebaseUser.isEmailVerified()) {
            mb.setVisibility(View.GONE);
        } else {
            mb.setVisibility(View.VISIBLE);
        }
    }

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
     *
     * @param fUser the user
     */
    public void sendEmailVerification(FirebaseUser fUser) {
        if (fUser != null) {
            fUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isComplete()) {
                            Toast.makeText(this,
                                    "We sent a confirmation link to your email address." +
                                            " Please check your email and click the link to complete verification.",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Verification email sent.");
                        } else {
                            Toast.makeText(this,
                                    "Something went wrong, we couldn't send the verification link. Please try again later.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    /**
     * Update name.
     *
     * @param name  the updated name
     * @param fUser the user
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

    /**
     * Handle exit profile update.
     *
     * @param view the view
     */
    public void handleExitProfileUpdate(View view) {
        hideKeyboard(this, view.getRootView());
        finish();
    }

    /**
     * Handle verify email address.
     *
     * @param view the view
     */
    public void handleVerifyEmailAddress(View view) {

        //Launch verify email event sequence
        hideKeyboard(this, view.getRootView());
        sendEmailVerification(FirebaseAuth.getInstance().getCurrentUser());
    }

    /**
     * Handle clearing the email address reset form.
     *
     * @param view the view
     */
    public void handleClearForm(View view) {

        hideKeyboard(this, view.getRootView());

        TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
        TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
        TextInputEditText pw = findViewById(R.id.pword);
        nooEmail.getText().clear();
        nooEmailCheck.getText().clear();
        pw.getText().clear();

    }

    /**
     * Handle submit new email button pressed.
     *
     * @param view the view
     */
    public void handleSubmitEmailAddress(View view) {
        hideKeyboard(this, view.getRootView());

        final TextInputEditText nooEmail = findViewById(R.id.editTextNooEmail);
        final TextInputEditText nooEmailCheck = findViewById(R.id.editTextEmailCheck);
        final TextInputEditText pw = findViewById(R.id.pword);

        if (nooEmail.length() < 3 || nooEmailCheck.length() < 3 || pw.length() < 8) {

            if (nooEmail.getText().equals(nooEmailCheck.getText())) {
                if (validateEmail(nooEmail.getText())) {
                    if (validatePassword(pw.getText())) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential creds = EmailAuthProvider
                                .getCredential(userHolder.currEmail, pw.getText().toString());
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
    }

    private void updateUserEmailAddress(FirebaseUser firebaseUser, String str) throws
            FirebaseAuthInvalidCredentialsException,
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
        emailUpdateScreen = true;
        mainLayout.setVisibility(View.GONE);
        emailUpdateLayout.setVisibility(View.VISIBLE);
        onPrepareOptionsMenu(menu);

    }

    private void reInitMainLayout() {
        emailUpdateScreen = false;
        emailUpdateLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (emailUpdateScreen) {
            reInitMainLayout();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuItem cancelButton = menu.findItem(R.id.cancel);
        if (emailUpdateScreen) {
            cancelButton.setVisible(true);

        }
        if (!emailUpdateScreen) {
            cancelButton.setVisible(false);
        }
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
            if (emailUpdateScreen) {
                reInitMainLayout();
            } else {
                onBackPressed();
            }
        } else if (id == R.id.cancel) {
            hideKeyboard(this, emailUpdateLayout.getRootView());
            reInitMainLayout();
        }

        return true;

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

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult called but resultCode was not equal to RESULT_OK :(");
            return;

        } else if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (data != null) {

                Uri pictureUri = data.getData();

                if (pictureUri != null) {
                    Log.e(TAG, "currentPic URI as called from onActivityStart is: " + pictureUri.getPath());

                    File imageFile = null;
                    try {
                        imageFile = FileUtil.getMakeFileByType(userHolder.currDisName, FileUtil.PHOTO, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (imageFile != null) {
                        userHolder.setProfilePicFile(imageFile);
                        profilePicture.postInvalidate();
                        Log.d(TAG, "File path of Image received by onActivityRResult is: " + userHolder.profPicPath);

                        try {
                            Bitmap bitmap = PictureUtil.getProfilePicture(userHolder.profPicPath, this);
                            profilePicture.setImageBitmap(bitmap);
                            updateProfilePicture(Uri.fromFile(userHolder.profPicFile));

                            Log.e(TAG, "Updated user's profile picture: SUCCESS");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "userHolder.profPicFile is null at the setPicture method.");
                    }
                    Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFile);
                }
            }

        } else if (requestCode == CAMERA_REQUEST_CODE) { //CAMERA_CAPTURE_INTENT

            profilePicture.postInvalidate();

            Log.d(TAG, "File path of Image received by onActivityRResult is: " + userHolder.profPicPath);

            if (userHolder.profPicFile != null) {
//                updateProfilePicture(Uri.fromFile(userHolder.profPicFile));
                try {
                    Bitmap bitmap = PictureUtil.getProfilePicture(userHolder.profPicPath, this);
                    profilePicture.setImageBitmap(bitmap);

                    Log.e(TAG, "Updated user's profile picture: SUCCESS");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "userHolder.profPicFile is null at the setPicture method.");
            }

        }
    }

    //Update the reference/'s to the location of the users profile picture
    private void updateProfilePicture(Uri uri) {
        profPicStorageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder().setPhotoUri(profPicStorageRef.getDownloadUrl().getResult()).build();
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fUser != null) {
                fUser.updateProfile(upcr)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                Snackbar.make(userHolder.disNameEditButton.getRootView(),
                                        "Your photo has been updated in your account profile",
                                        Snackbar.LENGTH_LONG).show();


                            } else {
                                DialogUtil.makeAndShow(UpdateProfileActivity.this,
                                        "Something Went Wrong",
                                        "We couldn't update your picture on Firebase",
                                        "Acknowledge",
                                        "Ok",
                                        () -> {

                                        });
                            }
                        });
            }

        });


    }


    private void takePicture() throws Exception {
        try {
            //Create the File object ref to the save location
            userHolder.setProfilePicFile(FileUtil.getMakeFileByType(userHolder.currDisName,
                    FileUtil.PHOTO,
                    UpdateProfileActivity.this));

            Uri saveLocationUri = FileProvider.getUriForFile(UpdateProfileActivity.this,
                    FileUtil.FILE_AUTHORITY,
                    userHolder.profPicFile);

            // Create intent for opening camera and capturing a photo
            Intent capturePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (capturePicture.resolveActivity(getPackageManager()) != null) {

                capturePicture.putExtra(MediaStore.EXTRA_OUTPUT, saveLocationUri);
                startActivityForResult(capturePicture, CAMERA_REQUEST_CODE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                takePicture();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // App needs permissions to use the camera and file system
            Snackbar.make(mainLayout.getRootView(),
                    "The app needs permission to take pictures.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", v -> {
                        ActivityCompat.requestPermissions(UpdateProfileActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_REQUEST_CAMERA);
                    }).show();
        }

    }


    private void launchPicFromStorage() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
        }
    }

    /**
     * Handle change/ reset profile picture button pressed.
     *
     * @param view the view
     */
    public void handleChangeProfPic(View view) {
        //Profile picture options
        if (hasCamera) {
            DialogUtil.makeAndShow(this,
                    "Set Profile Picture",
                    "From where do you want to retrieve your profile picture?",
                    "Take Picture",
                    "Look In File Storage",
                    "Close Popup",
                    this::checkCameraPermissions,
                    this::launchPicFromStorage
                    ,
                    () -> {
                        Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show();
                    });
        } else {
            DialogUtil.makeAndShow(this,
                    "Set Profile Picture",
                    "Choose what you would like to do next.",
                    "Look In File Storage",
                    "Close Popup",
                    this::launchPicFromStorage
                    ,
                    () -> {
                        Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Handle reset email address button pressed.
     *
     * @param view the view
     */
    public void handleEmailAddressChange(View view) {
        //launch email update ui and events
        hideKeyboard(this, findViewById(R.id.verifyButton).getRootView());
        initUpdateEmail();
    }

    /**
     * Handle done button pressed on this Activities main UI.
     *
     * @param view the view
     */
    public void handleSubmitButton(View view) {
        hideKeyboard(this, findViewById(R.id.handleDoneButton).getRootView());
        //Submit update account user profile
        onBackPressed();
    }

    /**
     * Handle button pressed to edit display name
     *
     * @param view the view clicked
     */
    public void handleShowEditDisplayName(View view) {
        toggleDisplayNameInput(view);

    }

    private void toggleDisplayNameInput(View view) {
        if (userHolder.disNameTextView.getVisibility() == View.VISIBLE) {

            userHolder.disNameTextView.setVisibility(View.GONE);
            userHolder.disNameEditButton.setVisibility(View.GONE);
            userHolder.txtInpLayDisName.setVisibility(View.VISIBLE);

        } else if (userHolder.disNameTextView.getVisibility() == View.GONE) {

            userHolder.txtInpLayDisName.setVisibility(View.GONE);
            userHolder.disNameTextView.setVisibility(View.VISIBLE);
            userHolder.disNameEditButton.setVisibility(View.VISIBLE);
        }

    }

    private static class UserHolder {
        //Profile Picture
        private File profPicFile;
        private String profPicPath;

        private void setProfilePicFile(File profPicFile) {
            this.profPicFile = profPicFile;
            profPicPath = profPicFile.getAbsolutePath();
        }


        //Display Name
        private String currDisName;
        private TextInputLayout txtInpLayDisName;
        private TextInputEditText txtInEdiTxtDisName;
        private TextView disNameTextView;
        private ImageButton disNameEditButton;
        private ImageButton edDisNameDoneButton;

        //email
        private String currEmail;

        /**
         * Instantiates a new Value holder.
         */
        private UserHolder() {
        }


    }

}


