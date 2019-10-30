package com.bitlink.travelink.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bitlink.travelink.R;
import com.github.vipulasri.timelineview.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_timeline_date)
    public TextView dateView;

    @BindView(R.id.text_timeline_title)
    public TextView messageView;

    @BindView(R.id.time_marker)
    public TimelineView timelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        timelineView.initLine(viewType);
    }
}
