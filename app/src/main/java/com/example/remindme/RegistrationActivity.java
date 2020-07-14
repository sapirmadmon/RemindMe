package com.example.remindme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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

import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.Continuation;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;

    EditText mUsername;
    EditText mEmail;
    EditText mPassword;
    EditText mConfirmPassword;
    ShapeableImageView imageView;

    User user;
    Uri pickedImgUrl;
    String avatarUrl;

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
        this.imageView = findViewById(R.id.image_view);

        Button chooseImage = findViewById(R.id.choose_image);
        chooseImage.setOnClickListener(getChoose_image_source());

        Button confirmButton = findViewById(R.id.registration_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                String userName = mUsername.getText().toString();

                if(email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill the form.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(getApplicationContext() ,"Please make sure the password match.", Toast.LENGTH_SHORT).show();
                    } else {
                        //createUserAccount(email, password, userName);
                        createUserAccount(email, password, userName);
                    }
                }
            }
        });

    }

    private void createUserAccount(final String email, final String password, final String userName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        user = new User(email, userName, new HashMap<String, UserTask>());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user.setuId(mAuth.getUid());
                            uploadImageToFirebase(pickedImgUrl);
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

    private void updateUI(final User currentUser) {
        currentUser.setAvatar(avatarUrl);
        mDatabase.child(currentUser.getuId()).setValue(currentUser);

        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivityIntent);
    }

    private void uploadImageToFirebase(Uri uri)  {
        String fileName = "users_avatars/"+uri.getLastPathSegment();
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(fileName);
        mStorageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        avatarUrl = uri.toString();
                        updateUI(user);
                    }
                });
            }
        });
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
                            if (bitmapImage != null) {
                                pickedImgUrl = getImageUri(getApplicationContext(), bitmapImage);
                            }
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