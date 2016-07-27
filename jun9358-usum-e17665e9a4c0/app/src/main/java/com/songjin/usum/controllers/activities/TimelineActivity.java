package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.TimelineRecyclerView;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;

import java.util.ArrayList;

public class TimelineActivity extends BaseActivity {
    private static final String TAG = "TimelineActivity";
    private static final int ALL = 0;
    private static final int MINE = 1;

    private int timelineStatus = 0;

    private class ViewHolder {
        public TimelineRecyclerView timelines;
        public FloatingActionButton writeTimelineButton;

        public ViewHolder(View view) {
            timelines = (TimelineRecyclerView) view.findViewById(R.id.timelines);
            writeTimelineButton = (FloatingActionButton) view.findViewById(R.id.write_timeline);
        }
    }

    private ViewHolder viewHolder;

//    private RequestManager.TypedBaasioQueryCallback<TimelineCardDto> timelineCardDtoQueryCallback;
    private ArrayList<TimelineCardDto> timelineCardDtos;
//    private BaasioQuery timelineCardDtoQuery;
    private SchoolEntity schoolEntity;

    private MenuItem queryTypeMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        schoolEntity = intent.getParcelableExtra("schoolEntity");

        timelineCardDtos = new ArrayList<>();
        setQueryToAllTimelines();
//        initCallback();

        initViews(R.layout.activity_timeline);
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.writeTimelineButton.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                viewHolder.writeTimelineButton.setVisibility(View.GONE);
                break;
        }

        timelineCardDtos.clear();
        getTimeline();
        SharedPreferences preferences = getSharedPreferences(Global.APP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Global.TIMELINE, System.currentTimeMillis());
        editor.commit();
    }


    private void getTimeline() {
        if (timelineStatus == ALL) {
            RequestManager.getAllTimeline(Global.userEntity.id, schoolEntity.id, new RequestManager.OnGetAllTimeline() {
                @Override
                public void onSuccess(ArrayList<TimelineCardDto> timelineCardDtos) {
                    viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
                    viewHolder.timelines.hideMoreProgress();
                }

                @Override
                public void onException(int code) {
                    viewHolder.timelines.hideMoreProgress();
                }
            });
        } else if (timelineStatus == MINE) {
            RequestManager.getMyTimeline(Global.userEntity.id, schoolEntity.id, new RequestManager.OnGetMyTimeline() {
                @Override
                public void onSuccess(ArrayList<TimelineCardDto> timelineCardDtos) {
                    viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
                }

                @Override
                public void onException(int code) {
                    viewHolder.timelines.hideMoreProgress();
                }
            });
        }
    }


    private void setQueryToAllTimelines() {
        if (schoolEntity != null) {
            timelineStatus = ALL;
        }
    }

    private void setQueryToMyTimelines() {
        timelineStatus = MINE;
    }

//    private void initCallback() {
//        timelineCardDtoQueryCallback = new RequestManager.TypedBaasioQueryCallback<TimelineCardDto>() {
//            @Override
//            public void onResponse(List<TimelineCardDto> entities) {
//                timelineCardDtos.addAll(entities);
//                viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
//                viewHolder.timelines.hideMoreProgress();
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//                viewHolder.timelines.hideMoreProgress();
//            }
//        };
//    }


//    // TODO: 15. 11. 23. 타임라인 모두 받아오기 응답
//    private void processGetAllTimeline(int code, Intent intent) {
//        if (code == SocketException.SUCCESS) {
//            ArrayList<TimelineCardDto> timelineCardDtos = intent.getParcelableArrayListExtra(Global.TIMELINE);
//            viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
//        }
//        viewHolder.timelines.hideMoreProgress();
//    }


//    // TODO: 15. 11. 23. 타임라인 모두 받아오기 응답
//    private void processGetMyTimeline(int code, Intent intent) {
//        if (code == SocketException.SUCCESS) {
//            ArrayList<TimelineCardDto> timelineCardDtos = intent.getParcelableArrayListExtra(Global.TIMELINE);
//            viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
//        }
//        viewHolder.timelines.hideMoreProgress();
//    }


    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

        // 액션바 설정
        getSupportActionBar().setTitle(schoolEntity.schoolname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder = new ViewHolder(getWindow().getDecorView());

        UserEntity signedUser = Global.userEntity;
        if (signedUser.schoolId != schoolEntity.id) {
            viewHolder.writeTimelineButton.setVisibility(View.GONE);
        }
        viewHolder.writeTimelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimelineWriteActivity.class);
                startActivity(intent);
            }
        });

        viewHolder.timelines.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timelineCardDtos.clear();
                getTimeline();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                break;
            case Global.STUDENT:
                queryTypeMenuItem = menu.add(0, 0, 0, "내가쓴글");
                queryTypeMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                break;
            case Global.PARENT:
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 0:
                timelineCardDtos.clear();
                if (queryTypeMenuItem.getTitle() == "내가쓴글") {
                    queryTypeMenuItem.setTitle("전체글");
                    setQueryToMyTimelines();
                } else if (queryTypeMenuItem.getTitle() == "전체글") {
                    queryTypeMenuItem.setTitle("내가쓴글");
                    setQueryToAllTimelines();
                }
                getTimeline();
//                RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
