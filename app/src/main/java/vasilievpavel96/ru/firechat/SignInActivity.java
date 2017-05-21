package vasilievpavel96.ru.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private Unbinder mUnbinder;

    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.signInButton) Button mSignInButton;
    @BindView(R.id.signUpButton) Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mUnbinder = ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.signInButton: {
                if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password))
                {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "sign in succesfully");
                                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Log.d(TAG, "failed to sign in");
                                        Snackbar.make(mSignInButton,"Ooops somethink went wrong",Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else{
                    Snackbar.make(mSignInButton,"Enter email,password",Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.signUpButton: {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
