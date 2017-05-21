package vasilievpavel96.ru.firechat;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Message> messages;
    Context ctx;
    FirebaseStorage storage;

    public MessageAdapter(Context ctx, List<Message> messages) {
        this.ctx = ctx;
        this.messages = messages;
        storage = FirebaseStorage.getInstance();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new MessageViewHolderLeft(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MessageViewHolderRight(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String uid = messages.get(position).authorUid;
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) return 1;
        else return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        int type = getItemViewType(position);
        final ImageView imageView;
        StorageReference ref = storage.getReference(msg.authorUid);
        if (type == 1){
            MessageViewHolderRight holderRight = (MessageViewHolderRight) holder;
            holderRight.msgText.setText(msg.message);
            imageView = holderRight.profileImage;
        }else{
            MessageViewHolderLeft holderLeft = (MessageViewHolderLeft) holder;
            holderLeft.msgText.setText(msg.message);
            imageView = holderLeft.profileImage;
        }
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(ctx).load(task.getResult().toString()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_person_dark);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolderLeft extends RecyclerView.ViewHolder {
        public TextView msgText;
        public ImageView profileImage;

        public MessageViewHolderLeft(View itemView) {
            super(itemView);
            msgText = (TextView) itemView.findViewById(R.id.msgText);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
        }
    }

    class MessageViewHolderRight extends RecyclerView.ViewHolder {
        public TextView msgText;
        public ImageView profileImage;

        public MessageViewHolderRight(View itemView) {
            super(itemView);
            msgText = (TextView) itemView.findViewById(R.id.msgText);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
        }
    }
}
