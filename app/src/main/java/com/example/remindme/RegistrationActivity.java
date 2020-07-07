package com.example.remindme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;

    EditText mUsername;
    EditText mEmail;
    EditText mPassword;
    EditText mConfirmPassword;
    EditText mProfileImage;
    ShapeableImageView imageView;

    Uri pickedImgUrl;

    private String email, password, confirmPassword, userName;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private String TAG = "RegistrationActivity";
    private static final String USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        InitComponents();

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();



        Button registerButton = findViewById(R.id.registration_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();
                userName = mUsername.getText().toString();

                if(email.isEmpty() || userName.isEmpty() || password.isEmpty() || !(password.equals(confirmPassword))) {
                    //TODO display error massage
                }
                else {
                    user = new User(email, password, userName,null);
                    //createUserAccount(email, password, userName);
                    createUserAccount();
                }
            }
        });


    }

    private void uploadImageToFirebase(Uri uri){

         StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_avatars");
         final StorageReference imageFilePath = mStorageRef.child(pickedImgUrl.getLastPathSegment());

       // StorageReference fileRef = mStorageRef.child("images/rivers.jpg");
        imageFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "image uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "image failed");
            }
        });
    }


    private void InitComponents() {
        TypeWriter tw = findViewById(R.id.title_typewriter);
        tw.setText("");
        tw.setCharacterDelay(250);
        tw.animateText("Registration");

        this.mUsername = findViewById(R.id.user_name_text_view);
        this.mEmail = findViewById(R.id.email_register_text_view);
        this.mPassword = findViewById(R.id.password_text_view);
        this.mConfirmPassword = findViewById(R.id.confirm_password_text_view);
        //this.mProfileImage = findViewById(R.id.choose_image_text_view);
        this.imageView = findViewById(R.id.image_view);

        Button chooseImage = findViewById(R.id.choose_image);
        chooseImage.setOnClickListener(getChoose_image_source());

        Button confirmButton = findViewById(R.id.registration_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPassword.getEditableText().equals(mConfirmPassword.getEditableText())) {
                    Toast.makeText(getApplicationContext() ,"Please make sure the password match.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUserAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user); //update user
                            //updateUI(pickedImgUrl, user); //update user info (+ avatar user)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }

    //update user photo and userName
    //private void updateUI(Uri pickedImgUrl, final FirebaseUser currentUser) {

        private void updateUI(final FirebaseUser currentUser) {

        String keyId = mDatabase.push().getKey();
        mDatabase.child(keyId).setValue(user); //adding user info to database
        uploadImageToFirebase(pickedImgUrl);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);

//        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_avatars");
//        final StorageReference imageFilePath = mStorage.child(pickedImgUrl.getLastPathSegment());
//        imageFilePath.putFile(pickedImgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                //image upload successfully
//                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        //uri contain user image url
//
//
//
//                        currentUser.updateProfile(profleUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()) {
//                                    Log.d(TAG, "register complete");
//
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });


//        Intent loginIntent = new Intent(this, LoginFragment.class);
//        startActivity(loginIntent);
    }

    private View.OnClickListener getChoose_image_source() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Gallery", "Camera"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                        startActivityForResult(intent,
                                                PICK_FROM_GALLARY);
                                        break;
                                    case 1:
                                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent1, PICK_FROM_CAMERA);

                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

                builder.show();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShapeAppearanceModel shapeAppearanceModel = this.imageView.getShapeAppearanceModel();
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.getExtras() != null) {
                            Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
                            this.imageView.setImageBitmap(bitmapImage);
                            float radius = getResources().getDimension(R.dimen.default_corner_radius);
                            imageView.setShapeAppearanceModel(shapeAppearanceModel
                                    .toBuilder()
                                    .setAllCorners(CornerFamily.ROUNDED,radius)
                                    .build());
                        }
                    }
                }
                break;

            case PICK_FROM_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        pickedImgUrl = data.getData();
                        this.imageView.setImageURI(pickedImgUrl);
                        float radius = getResources().getDimension(R.dimen.default_corner_radius);
                        this.imageView.setShapeAppearanceModel(shapeAppearanceModel
                                .toBuilder()
                                .setAllCorners(CornerFamily.ROUNDED,radius)
                                .build());
                    }
                }
                break;
        }
    }
}