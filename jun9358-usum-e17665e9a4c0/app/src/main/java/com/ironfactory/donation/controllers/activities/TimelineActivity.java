package com.ironfactory.donation.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.melnykov.fab.FloatingActionButton;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.TimelineRecyclerView;
import com.ironfactory.donation.dtos.TimelineCardDto;
import com.ironfactory.donation.entities.SchoolEntity;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketService;

import java.util.ArrayList;

public class TimelineActivity extends BaseActivity {
    private static final String TAG = "TimelineActivity";

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
    private Intent intent;
    private SchoolEntity schoolEntity;

    private MenuItem queryTypeMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "액티비티 시작");

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
//        RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);
        startService(intent);
    }

    private void setQueryToAllTimelines() {
        intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra(Global.COMMAND, Global.GET_ALL_TIMELINE);
        intent.putExtra(Global.SCHOOL_ID, schoolEntity.id);

//        timelineCardDtoQuery = new BaasioQuery();
//        timelineCardDtoQuery.setType(TimelineEntity.COLLECTION_NAME);
//        timelineCardDtoQuery.setWheres(TimelineEntity.PROPERTY_SCHOOL_ID + "=" + String.valueOf(schoolEntity.id));
//        timelineCardDtoQuery.setOrderBy(
//                BaasioEntity.PROPERTY_CREATED,
//                BaasioQuery.ORDER_BY.DESCENDING
//        );
    }

    private void setQueryToMyTimelines() {
        intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra(Global.COMMAND, Global.GET_MY_TIMELINE);
        intent.putExtra(Global.SCHOOL_ID, schoolEntity.id);
        intent.putExtra(Global.USER_ID, Global.userEntity.id);

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


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                int code = intent.getIntExtra(Global.CODE, -1);
                if (code != -1) {
                    SocketException.toastErrMsg(code);
                    SocketException.printErrMsg(code);
                    if (command.equals(Global.GET_ALL_TIMELINE)) {
                        // 타임라인 모두 받아오기 응답
                        processGetAllTimeline(code, intent);
                    } else if (command.equals(Global.GET_MY_TIMELINE)) {
                        // 타임라인 내 글 받아오기 응답
                        processGetMyTimeline(code, intent);
                    }
                }
            }
        }
    }


    // TODO: 15. 11. 23. 타임라인 모두 받아오기 응답
    private void processGetAllTimeline(int code, Intent intent) {
        if (code == SocketException.SUCCESS) {
            ArrayList<TimelineCardDto> timelineCardDtos = intent.getParcelableArrayListExtra(Global.TIMELINE);
            viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
        }
        viewHolder.timelines.hideMoreProgress();
    }


    // TODO: 15. 11. 23. 타임라인 모두 받아오기 응답
    private void processGetMyTimeline(int code, Intent intent) {
        if (code == SocketException.SUCCESS) {
            ArrayList<TimelineCardDto> timelineCardDtos = intent.getParcelableArrayListExtra(Global.TIMELINE);
            viewHolder.timelines.setTimelineCardDtos(timelineCardDtos);
        }
        viewHolder.timelines.hideMoreProgress();
    }


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
                BaseActivity.startActivityUsingStack(TimelineWriteActivity.class);
            }
        });

        viewHolder.timelines.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timelineCardDtos.clear();
                startService(intent);
//                RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);
            }
        });

        viewHolder.timelines.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                startService(intent);
//                RequestManager.getNextTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);
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
                startService(intent);
//                RequestManager.getTimelinesInBackground(timelineCardDtoQuery, timelineCardDtoQueryCallback);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
