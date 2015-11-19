package com.songjin.usum.controllers.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.songjin.usum.R;
import com.songjin.usum.entities.AlarmEntity;

import java.util.ArrayList;

public class AlarmRecyclerView extends SuperRecyclerView {
    private ArrayList<AlarmEntity> alarmEntitiess;

    public AlarmRecyclerView(Context context) {
        this(context, null);
    }

    public AlarmRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        alarmEntitiess = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new AlaramAdapter(alarmEntitiess));
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

    public void setAlarmEntities(ArrayList<AlarmEntity> alarmEntities) {
        setEmptyView(R.layout.view_empty);

        this.alarmEntitiess.clear();
        this.alarmEntitiess.addAll(alarmEntities);
        getAdapter().notifyDataSetChanged();
    }

    private class AlaramAdapter extends RecyclerView.Adapter<AlaramAdapter.ViewHolder> {
        private ArrayList<AlarmEntity> alarmEntities;

        public AlaramAdapter(ArrayList<AlarmEntity> alarmEntities) {
            this.alarmEntities = alarmEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AlarmCardView alarmCardView = new AlarmCardView(getContext());
            alarmCardView.setTag("AlarmCardView");
            alarmCardView.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            return new ViewHolder(alarmCardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            AlarmEntity alarmEntity = alarmEntities.get(position);
            viewHolder.alarmCardView.setAlarmEntity(alarmEntity);
        }

        @Override
        public int getItemCount() {
            return alarmEntities.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public AlarmCardView alarmCardView;

            public ViewHolder(View view) {
                super(view);

                alarmCardView = (AlarmCardView) view.findViewWithTag("AlarmCardView");
            }
        }
    }
}
