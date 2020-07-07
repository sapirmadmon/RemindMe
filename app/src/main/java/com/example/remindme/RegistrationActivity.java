package com.example.remindme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;

    EditText mUsername;
    EditText mEmail;
    EditText mPassword;
    EditText mConfirmPassword;
    EditText mProfileImage;
    ShapeableImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        InitComponents();
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
                        Uri mImageCaptureUri = data.getData();
                        this.imageView.setImageURI(mImageCaptureUri);
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