package vasilievpavel96.ru.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private MessageAdapter mAdapter;
    private List<Message> messages;
    private Unbinder mUnbinder;

    @BindView(R.id.list) RecyclerView mList;
    @BindView(R.id.message) EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (mFirebaseUser != null) {
            messages = new ArrayList<>();
            mAdapter = new MessageAdapter(MainActivity.this, messages);
            mList.setAdapter(mAdapter);
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference("messages");
            mRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    messages.add(msg);
                    mAdapter.notifyItemInserted(messages.size()-1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mMessage.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                        String message = mMessage.getText().toString();
                        String messageId = mRef.push().getKey();
                        Message msg = new Message();
                        msg.message = message;
                        msg.author = mFirebaseUser.getDisplayName();
                        msg.authorUid = mFirebaseUser.getUid();
                        mRef.child(messageId).setValue(msg);
                        mMessage.setText("");
                        return true;
                    }
                    return false;
                }
            });
        } else {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            mFirebaseAuth.signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        return true;
    }

}
