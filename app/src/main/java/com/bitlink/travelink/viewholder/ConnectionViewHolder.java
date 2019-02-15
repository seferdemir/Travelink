package com.bitlink.travelink.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlink.travelink.R;
import com.bitlink.travelink.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectionViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView photoView;
    public TextView userView;
    public ImageButton followView;
    public ImageButton unfollowView;

    public ConnectionViewHolder(View itemView) {
        super(itemView);

        photoView = (CircleImageView) itemView.findViewById(R.id.user_photo);
        userView = (TextView) itemView.findViewById(R.id.user_name);
        followView = (ImageButton) itemView.findViewById(R.id.followButton);
        unfollowView = (ImageButton) itemView.findViewById(R.id.unfollowButton);
    }

    public void bindToUser(User user, View.OnClickListener followClickListener, View.OnClickListener unfollowClickListener) {
        userView.setText(user.getUsername());

        followView.setOnClickListener(followClickListener);
        unfollowView.setOnClickListener(unfollowClickListener);
    }
}
