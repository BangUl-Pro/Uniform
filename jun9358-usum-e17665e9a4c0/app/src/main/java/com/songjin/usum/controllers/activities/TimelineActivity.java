package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.TimelineRecyclerView;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class TimelineActivity extends BaseActivity {
    private static final String TAG = "TimelineActivity";
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
        Log.d(TAG, "액티비티 시작");

        Intent intent = getIntent();
        schoolEntity = intent.getParcelableExtra("schoolEntity");
        Log.d(TAG, "schoolEntity = " + schoolEntity);

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
        Log.d(TAG, "1");
    }


    private void getTimeline() {
        if (timelineStatus == 0) {
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
        } else if (timelineStatus == 1) {
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
            timelineStatus = 0;
        }

//        timelineCardDtoQuery = new BaasioQuery();
//        timelineCardDtoQuery.setType(TimelineEntity.COLLECTION_NAME);
//        timelineCardDtoQuery.setWheres(TimelineEntity.PROPERTY_SCHOOL_ID + "=" + String.valueOf(schoolEntity.id));
//        timelineCardDtoQuery.setOrderBy(
//                BaasioEntity.PROPERTY_CREATED,
//                BaasioQuery.ORDER_BY.DESCENDING
//        );
    }

    private void setQueryToMyTimelines() {
        timelineStatus = 1;
//        timelineCardDtoQuery = new BaasioQuery();
//        timelineCardDtoQuery.setType(TimelineEntity.COLLECTION_NAME);
//        timelineCardDtoQuery.setWheres(
//                TimelineEntity.PROPERTY_SCHOOL_ID + "=" + String.valueOf(schoolEntity.id) + " AND " +
//                        TimelineEntity.PROPERTY_USER_UUID + "=" + Baas.io().getSignedInUser().getUuid().toString()
//        );
//        timelineCardDtoQuery.setOrderBy(
//                BaasioEntity.PROPERTY_CREATED,
//                BaasioQuery.ORDER_BY.DESCENDING
//        );
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
                Log.d(TAG, "2");
//                RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);
            }
        });

//        viewHolder.timelines.setOnMoreListener(new OnMoreListener() {
//            @Override
//            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
//                getTimeline();
//                Log.d(TAG, "3");
////                RequestManager.getNextTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);
//            }
//        });
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
                Log.d(TAG, "4");
//                RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
