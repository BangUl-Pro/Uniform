package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.TimelineDetailActivity;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.entities.UserEntity;

import java.util.ArrayList;

public class TimelineRecyclerView extends SuperRecyclerView {
    private ArrayList<TimelineCardDto> timelineCardDtos;
    private UserEntity userEntity;

    public TimelineRecyclerView(Context context) {
        this(context, null);
    }

    public TimelineRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimelineRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        timelineCardDtos = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new TimelineAdapter(timelineCardDtos));
    }

    public void setEmptyView(int resId) {
        if (mEmptyId != 0) {
            return;
        }
        mEmptyId = resId;

        this.mEmpty = (ViewStub) findViewById(com.malinskiy.superrecyclerview.R.id.empty);
        this.mEmpty.setLayoutResource(this.mEmptyId);
        if (this.mEmptyId != 0) {
            this.mEmptyView = this.mEmpty.inflate();
        }

        this.mEmpty.setVisibility(View.GONE);
    }

    public void setTimelineCardDtos(ArrayList<TimelineCardDto> timelineCardDtos) {
        setEmptyView(R.layout.view_empty);

        this.timelineCardDtos = timelineCardDtos;
        getAdapter().notifyDataSetChanged();
    }

    public void addTimelineCardDtos(ArrayList<TimelineCardDto> timelineCardDtos) {
        this.timelineCardDtos.addAll(timelineCardDtos);
        getAdapter().notifyDataSetChanged();
    }

    private class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
        private ArrayList<TimelineCardDto> timelineCardDtos;

        public TimelineAdapter(ArrayList<TimelineCardDto> timelineCardDtos) {
            this.timelineCardDtos = timelineCardDtos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TimelineCardView timelineCardView = new TimelineCardView(getContext());
            timelineCardView.setTag("TimelineCardView");
            timelineCardView.setUserEntity(userEntity);

            return new ViewHolder(timelineCardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final TimelineCardDto timelineCardDto = timelineCardDtos.get(position);
            viewHolder.timelineCard.setTimelineCardDto(timelineCardDto);
            viewHolder.timelineCard.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.context, TimelineDetailActivity.class);
                    intent.putExtra("timelineCardDto", timelineCardDto);
                    BaseActivity.startActivityUsingStack(intent);
                }
            });
            viewHolder.timelineCard.setOnTimelineActionCallback(new TimelineCardView.TimelineActionCallback() {
                @Override
                public void onDelete() {
                    timelineCardDtos.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return timelineCardDtos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TimelineCardView timelineCard;

            public ViewHolder(View view) {
                super(view);

                timelineCard = (TimelineCardView) view.findViewWithTag("TimelineCardView");
                timelineCard.setUserEntity(userEntity);
            }
        }
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
