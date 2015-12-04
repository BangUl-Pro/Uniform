package com.ironfactory.donation.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.TimelineCardView;
import com.ironfactory.donation.controllers.views.TimelineCommentRecyclerView;
import com.ironfactory.donation.dtos.TimelineCardDto;
import com.ironfactory.donation.dtos.TimelineCommentCardDto;
import com.ironfactory.donation.entities.LikeEntity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketService;
import com.ironfactory.donation.utils.StringUtil;

import java.util.ArrayList;

public class TimelineDetailActivity extends BaseActivity {
    private static final String TAG = "TimelineDetailActivity";

    private class ViewHolder {
        public TimelineCardView timelineCardView;
        public EditText commentContents;
        public Button writeCommentButton;

        public TimelineCommentRecyclerView comments;

        public ViewHolder(View view) {
            timelineCardView = (TimelineCardView) view.findViewById(R.id.timeline_card);
            commentContents = (EditText) view.findViewById(R.id.comment_contents);
            writeCommentButton = (Button) view.findViewById(R.id.write_comment);

            comments = (TimelineCommentRecyclerView) view.findViewById(R.id.comments);
            comments.setFrom(1);
        }
    }

    private ViewHolder viewHolder;

    private TimelineCardDto timelineCardDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "액티비티 시작");

        timelineCardDto = getIntent().getParcelableExtra("timelineCardDto");

        initViews(R.layout.activity_timeline_detail);
        requestTimelineComments();
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.commentContents.setVisibility(View.GONE);
                viewHolder.writeCommentButton.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                viewHolder.commentContents.setVisibility(View.GONE);
                viewHolder.writeCommentButton.setVisibility(View.GONE);
                break;
        }
    }

    public void requestTimelineComments() {
        showLoadingView();
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra(Global.COMMAND, Global.GET_TIMELINE_COMMENT);
        intent.putExtra(Global.ID, timelineCardDto.timelineEntity.id);
        intent.putExtra(Global.FROM, 2);
        startService(intent);

//        RequestManager.getTimelineComments(timelineCardDto.timelineEntity.id, new RequestManager.TypedBaasioQueryCallback<TimelineCommentCardDto>() {
//            @Override
//            public void onResponse(List<TimelineCommentCardDto> entities) {
//                ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = new ArrayList<>();
//                timelineCommentCardDtos.addAll(entities);
//                viewHolder.comments.setTimelineCommentCardDtos(timelineCommentCardDtos);
//
//                hideLoadingView();
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//                hideLoadingView();
//            }
//        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                int code = intent.getIntExtra(Global.CODE, -1);
                if (code != -1) {
                    SocketException.printErrMsg(code);
                    SocketException.toastErrMsg(code);

                    if (command.equals(Global.GET_TIMELINE_COMMENT)) {
                        // 타임라인 댓글 요청 응답
                        processGetTimelineComment(code, intent);
                    } else if (command.equals(Global.INSERT_TIMELINE_COMMENT)) {
                        // 타임라인 댓글 입력 응답
                        processInsertTimelineComment(code);
                    } else if (command.equals(Global.DELETE_LIKE)) {
                        // 좋아요 지우기
                        processDeleteLike(code);
                    } else if (command.equals(Global.INSERT_LIKE)) {
                        // 좋아요
                        processInsertLike(code, intent);
                    }
                }
            }
        }
    }


    // TODO: 15. 11. 25. 좋아요
    private void processInsertLike(int code, Intent intent) {
        LikeEntity likeEntity = intent.getParcelableExtra(Global.LIKE);
        viewHolder.timelineCardView.processInsertLike(code, likeEntity);
    }


    // TODO: 15. 11. 25. 좋아요 지우기
    private void processDeleteLike(int code) {
        viewHolder.timelineCardView.processDeleteLike(code);
    }


    // TODO: 15. 11. 25. 타임라인 지우기
    private void processDeleteTimeline(int code) {
        viewHolder.timelineCardView.processDeleteTimeline(code);
    }


    // TODO: 15. 11. 23. 타임라인 댓글 작성 응답
    private void processInsertTimelineComment(int code) {
        viewHolder.writeCommentButton.setEnabled(true);
        if (code == SocketException.SUCCESS) {
            requestTimelineComments();
            viewHolder.commentContents.setText("");
            viewHolder.commentContents.clearFocus();
        } else {
            new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .content("덧글을 작성하는 도중에 문제가 발생하였습니다.")
                    .show();
        }
    }


    // TODO: 15. 11. 23. 타임라인 댓글 요청 응답
    private void processGetTimelineComment(int code, Intent intent) {
        if (code == SocketException.SUCCESS) {
            // 성공
            ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = (ArrayList) intent.getSerializableExtra(Global.TIMELINE_COMMENT);
            viewHolder.comments.setTimelineCommentCardDtos(timelineCommentCardDtos);
        }
        hideLoadingView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
        viewHolder = new ViewHolder(getWindow().getDecorView().findViewById(android.R.id.content));

        // 액션바 설정
        getSupportActionBar().setTitle("타임라인");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder.timelineCardView.setTimelineCardDto(timelineCardDto);
        viewHolder.timelineCardView.setCommentButtonVisibility(View.GONE);
        viewHolder.timelineCardView.setOnTimelineActionCallback(new TimelineCardView.TimelineActionCallback() {
            @Override
            public void onDelete() {
                finish();
            }
        });

        // 리스너 등록
        viewHolder.writeCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timelineItemUuid = timelineCardDto.timelineEntity.id;
                String commentContents = viewHolder.commentContents.getText().toString();

                if (StringUtil.isEmptyString(timelineItemUuid)) {
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content("게시글에 문제가 있습니다.")
                            .show();
                }
                if (StringUtil.isEmptyString(commentContents)) {
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content("내용을 입력해주세요.")
                            .show();
                }

                viewHolder.writeCommentButton.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.putExtra(Global.COMMAND, Global.INSERT_TIMELINE_COMMENT);
                intent.putExtra(Global.TIMELINE_ITEM_ID, timelineItemUuid);
                intent.putExtra(Global.COMMENT_CONTENT, commentContents);
                intent.putExtra(Global.USER_ID, Global.userEntity.id);
                intent.putExtra(Global.FROM, 2);
                startService(intent);

//                RequestManager.insertTimelineComment(
//                        timelineItemUuid,
//                        commentContents,
//                        new BaasioCallback<BaasioEntity>() {
//                            @Override
//                            public void onResponse(BaasioEntity baasioEntity) {
//                                requestTimelineComments();
//                                viewHolder.commentContents.setText("");
//                                viewHolder.commentContents.clearFocus();
//                                viewHolder.writeCommentButton.setEnabled(true);
//                            }
//
//                            @Override
//                            public void onException(BaasioException e) {
//                                new MaterialDialog.Builder(BaseActivity.context)
//                                        .title(R.string.app_name)
//                                        .content("덧글을 작성하는 도중에 문제가 발생하였습니다.")
//                                        .show();
//                                viewHolder.writeCommentButton.setEnabled(true);
//                            }
//                        }
//                );
            }
        });
    }
}
