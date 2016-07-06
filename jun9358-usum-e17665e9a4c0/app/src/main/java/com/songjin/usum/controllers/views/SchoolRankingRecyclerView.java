package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.TimelineActivity;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.managers.RequestManager;

import java.util.ArrayList;

public class SchoolRankingRecyclerView extends SuperRecyclerView {
    private static final String TAG = "SchoolRankRecyclerView";
    private ArrayList<SchoolRanking> schoolRankings;
    private int index = 0;

    public SchoolRankingRecyclerView(Context context) {
        this(context, null);
    }

    public SchoolRankingRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SchoolRankingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        schoolRankings = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new SchoolRankingAdapter(schoolRankings));

        setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                RequestManager.getSchoolRanking(new RequestManager.OnGetSchoolRanking() {
                    @Override
                    public void onSuccess(ArrayList<SchoolRanking> schoolRankings) {
                        addSchoolRankings(schoolRankings);
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "학교랭킹 loadmore 실패");
                    }
                }, ++index);
            }
        }, 10);
    }

    public void addSchoolRankings(ArrayList<SchoolRanking> schoolRankings) {
        this.schoolRankings.addAll(schoolRankings);
        getAdapter().notifyDataSetChanged();
    }

    private class SchoolRankingAdapter extends RecyclerView.Adapter<SchoolRankingAdapter.ViewHolder> {
        private ArrayList<SchoolRanking> schoolRankings;

        public SchoolRankingAdapter(ArrayList<SchoolRanking> schoolRankings) {
            this.schoolRankings = schoolRankings;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            Log.d("USUM", position + "");
            SchoolRanking schoolRanking = schoolRankings.get(position);

            SchoolRankingCardView schoolRankingCardView = new SchoolRankingCardView(getContext());
            schoolRankingCardView.setTag("SchoolRankingCardView");
            schoolRankingCardView.setSchoolEntity(schoolRanking.getSchoolEntity());

            return new ViewHolder(schoolRankingCardView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final SchoolRanking schoolRanking = schoolRankings.get(position);

            int progress = 0;
            if (0 < schoolRanking.point) {
                long nowPoint = schoolRanking.point;
                long maxPoint = schoolRankings.get(0).point;
                progress = (int) (((float) nowPoint / maxPoint) * 100);
            }
            viewHolder.schoolRankingCardView.setProgress(progress);
            viewHolder.schoolRankingCardView.setRanking(position + 1);
            viewHolder.schoolRankingCardView.setSchoolEntity(schoolRanking.getSchoolEntity());
            viewHolder.schoolRankingCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.context, TimelineActivity.class);
                    intent.putExtra("schoolEntity", schoolRanking.getSchoolEntity());
                    BaseActivity.startActivityUsingStack(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return schoolRankings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public SchoolRankingCardView schoolRankingCardView;

            public ViewHolder(View view) {
                super(view);

                schoolRankingCardView = (SchoolRankingCardView) view.findViewWithTag("SchoolRankingCardView");
            }
        }
    }
}
