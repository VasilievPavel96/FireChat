package vasilievpavel96.ru.firechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static vasilievpavel96.ru.firechat.ImageUtils.getCircularBitmap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 1;
    private FirebaseAuth mFirebaseAuth;
    private Uri mImgUri;
    private Unbinder mUnbinder;

    @BindView(R.id.profileImage) ImageView mProfileImage;
    @BindView(R.id.name) EditText mName;
    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.signUpButton) Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mUnbinder = ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignUpButton.setOnClickListener(this);
        mProfileImage.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        switch (v.getId())
        {
            case R.id.signUpButton:{
                final String name = mName.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name))
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "sign up succesfully");
                                        final FirebaseUser user = task.getResult().getUser();
                                        final UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name);
                                        if (mImgUri != null) {
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference ref = storage.getReference().child(user.getUid());
                                            UploadTask uploadTask = ref.putFile(mImgUri);
                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        user.updateProfile(builder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                            }
                                                        });
                                                    } else {
                                                        Log.d(TAG, "failed to load image to server:");
                                                        Snackbar.make(mSignUpButton,"Failed to load image to server",Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            user.updateProfile(builder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d(TAG, "failed to sign up");
                                        Snackbar.make(mSignUpButton,"Oops somethink went wrong",Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else{
                    Snackbar.make(mSignUpButton,"Enter email,password and name",Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.profileImage:{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                mImgUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImgUri);
                    bitmap = getCircularBitmap(bitmap);
                    mProfileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "not select image");
            }
        }
    }

}
