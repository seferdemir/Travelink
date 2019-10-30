package com.bitlink.travelink.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlink.travelink.R;
import com.bitlink.travelink.model.Post;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public CircleImageView photoView;
    public TextView placeView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.user_name);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        photoView = (CircleImageView) itemView.findViewById(R.id.user_photo);
        placeView = (TextView) itemView.findViewById(R.id.post_place);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.getTitle());
        authorView.setText(post.getAuthor());
        numStarsView.setText(String.valueOf(post.getStarCount()));
        bodyView.setText(post.getBody());
        placeView.setText(post.getPlace() == null ? "" : post.getPlace().getName());

        starView.setOnClickListener(starClickListener);
    }
}
