package com.songjin.usum.controllers.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songjin.usum.R;
import com.songjin.usum.entities.AlarmEntity;

public class AlarmCardView extends LinearLayout {
    private class ViewHolder {
        public TextView pushMessage;
        public TextView receivedDate;

        public ViewHolder(View view) {
            pushMessage = (TextView) view.findViewById(R.id.push_message);
            receivedDate = (TextView) view.findViewById(R.id.received_date);
        }
    }

    private ViewHolder viewHolder;

    public AlarmCardView(Context context) {
        this(context, null);
    }

    public AlarmCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View view = inflate(getContext(), R.layout.view_alarm_card, this);
        viewHolder = new ViewHolder(view);
    }

    public void setAlarmEntity(AlarmEntity alarmEntity) {
        viewHolder.pushMessage.setText(alarmEntity.message);
        viewHolder.receivedDate.setText(DateFormat.format("yyyy년 MM월 dd일", alarmEntity.timestamp));
    }
}
