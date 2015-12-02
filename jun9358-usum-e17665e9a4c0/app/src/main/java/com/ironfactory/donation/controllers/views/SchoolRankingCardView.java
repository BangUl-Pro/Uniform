package com.ironfactory.donation.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.activities.TimelineActivity;
import com.ironfactory.donation.entities.SchoolEntity;

public class SchoolRankingCardView extends LinearLayout {
    private static final String TAG = "SchoolRankingCardView";
    private class ViewHolder {
        public TextView ranking;
        public TextView schoolname;
        public TextView schoolAddress;
        public NumberProgressBar numberProgressBar;

        public ViewHolder(View view) {
            ranking = (TextView) view.findViewById(R.id.ranking);
            schoolname = (TextView) view.findViewById(R.id.schoolname);
            schoolAddress = (TextView) view.findViewById(R.id.school_address);
            numberProgressBar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
        }
    }

    private ViewHolder viewHolder;

    private SchoolEntity schoolEntity;

    public SchoolRankingCardView(Context context) {
        this(context, null);
    }

    public SchoolRankingCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SchoolRankingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.school_ranking_card, this);
        viewHolder = new ViewHolder(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.context, TimelineActivity.class);
                intent.putExtra("schoolEntity", schoolEntity);
                BaseActivity.startActivityUsingStack(intent);
            }
        });
        viewHolder.numberProgressBar.setProgressTextVisibility(NumberProgressBar.ProgressTextVisibility.Invisible);
    }

    public void setRanking(int ranking) {
        if (100 < ranking) {
            viewHolder.ranking.setText("100+");
        } else {
            viewHolder.ranking.setText(String.valueOf(ranking));
        }
    }

    public void setProgress(int progress) {
        viewHolder.numberProgressBar.setProgress(progress);
    }

    public void setSchoolEntity(SchoolEntity schoolEntity) {
        this.schoolEntity = schoolEntity;

        viewHolder.schoolname.setText(this.schoolEntity.schoolname);
        viewHolder.schoolAddress.setText(this.schoolEntity.address);
    }

    public static int calcProgress(long nowPoint, long maxPoint) {
        int progress = 0;
        if (0 < nowPoint) {
            progress = (int) (((float) nowPoint / maxPoint) * 100);
        }

        return progress;
    }
}
