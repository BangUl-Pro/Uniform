package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.TimelineCardView;
import com.songjin.usum.controllers.views.TimelineCommentRecyclerView;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.utils.StringUtil;

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
        }
    }

    private ViewHolder viewHolder;

    private TimelineCardDto timelineCardDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        RequestManager.getTimelineComment(timelineCardDto.timelineEntity.id, new RequestManager.OnGetTimelineComment() {
            @Override
            public void onSuccess(ArrayList<TimelineCommentCardDto> timelineCommentCardDtos) {
                viewHolder.comments.setTimelineCommentCardDtos(timelineCommentCardDtos);
                hideLoadingView();
            }

            @Override
            public void onException() {
                hideLoadingView();
            }
        });

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


//    // TODO: 15. 11. 25. 좋아요
//    private void processInsertLike(int code, Intent intent) {
//        LikeEntity likeEntity = intent.getParcelableExtra(Global.LIKE);
//        viewHolder.timelineCardView.processInsertLike(code, likeEntity);
//    }
//
//
//    // TODO: 15. 11. 25. 타임라인 지우기
//    private void processDeleteTimeline(int code) {
//        viewHolder.timelineCardView.processDeleteTimeline(code);
//    }


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
                    new MaterialDialog.Builder(context)
                            .title(R.string.app_name)
                            .content("게시글에 문제가 있습니다.")
                            .show();
                }
                if (StringUtil.isEmptyString(commentContents)) {
                    new MaterialDialog.Builder(context)
                            .title(R.string.app_name)
                            .content("내용을 입력해주세요.")
                            .show();
                }

                viewHolder.writeCommentButton.setEnabled(false);

                RequestManager.insertTimelineComment(
                        timelineItemUuid,
                        commentContents,
                        Global.userEntity.id,
                        new RequestManager.OnInsertTimelineComment() {
                            @Override
                            public void onSuccess() {
                                requestTimelineComments();
                                viewHolder.commentContents.setText("");
                                viewHolder.commentContents.clearFocus();
                                viewHolder.writeCommentButton.setEnabled(true);
                            }

                            @Override
                            public void onException() {
                                new MaterialDialog.Builder(context)
                                        .title(R.string.app_name)
                                        .content("덧글을 작성하는 도중에 문제가 발생하였습니다.")
                                        .show();
                                viewHolder.writeCommentButton.setEnabled(true);
                            }
                        }
                );

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
