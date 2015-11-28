package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.GMailSender;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.socketIo.SocketService;

public class TimelineCommentCardView extends CardView {
    private TimelineCommentCardDto timelineCommentCardDto;
    private TimelineCardView.TimelineActionCallback timelineActionCallback;
    private int from;

    private class ViewHolder {
        public WriterView writerView;
        public TextView contents;

        public ViewHolder(View view) {
            writerView = (WriterView) view.findViewById(R.id.writer_view);
            contents = (TextView) view.findViewById(R.id.contents);
        }
    }

    private ViewHolder viewHolder;

    public TimelineCommentCardView(Context context) {
        this(context, null);
    }

    public TimelineCommentCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimelineCommentCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.timeline_comment_card, this);
        viewHolder = new ViewHolder(view);
    }

    private void showMorePopup() {
        UserEntity userEntity = Global.userEntity;

        PopupMenu popup = new PopupMenu(getContext(), viewHolder.writerView.moreButton);
        popup.getMenuInflater().inflate(R.menu.menu_writer_more, popup.getMenu());
        if (timelineCommentCardDto.commentEntity.user_uuid.equals(userEntity.id)) {
            popup.getMenu().findItem(R.id.action_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.action_report).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report:
                        new MaterialDialog.Builder(BaseActivity.context)
                                .title(R.string.app_name)
                                .content("정말로 신고하시겠습니까?")
                                .positiveText("신고하기")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        new SendMailViaThread().execute(null, null, null);
                                    }
                                })
                                .show();
                        return true;
                    case R.id.action_delete:
                        BaseActivity.showLoadingView();

                        Intent intent = new Intent(getContext(), SocketService.class);
                        intent.putExtra(Global.COMMAND, Global.DELETE_COMMENT);
                        intent.putExtra(Global.TIMELINE_COMMENT, timelineCommentCardDto);
                        intent.putExtra(Global.FROM, from);
                        getContext().startService(intent);
                        Global.OnDeleted = new Global.onDeleted() {
                            @Override
                            public void onSuccess() {
                                BaseActivity.hideLoadingView();
                                if (timelineActionCallback != null) {
                                    timelineActionCallback.onDelete();
                                }
                            }

                            @Override
                            public void onException() {
                                BaseActivity.hideLoadingView();

                                new MaterialDialog.Builder(BaseActivity.context)
                                        .title(R.string.app_name)
                                        .content("댓글을 삭제하는 중에 문제가 발생하였습니다.")
                                        .show();
                            }
                        };

//                        RequestManager.deleteComment(timelineCommentCardDto, new BaasioCallback<BaasioEntity>() {
//                            @Override
//                            public void onResponse(BaasioEntity baasioEntity) {
//                                BaseActivity.hideLoadingView();
//                                if (timelineActionCallback != null) {
//                                    timelineActionCallback.onDelete();
//                                }
//                            }
//
//                            @Override
//                            public void onException(BaasioException e) {
//                                BaseActivity.hideLoadingView();
//
//                                new MaterialDialog.Builder(BaseActivity.context)
//                                        .title(R.string.app_name)
//                                        .content("댓글을 삭제하는 중에 문제가 발생하였습니다.")
//                                        .show();
//                            }
//                        });
                        return true;
                }

                return false;
            }
        });

        popup.show();
    }

    public void setOnTimelineActionCallback(TimelineCardView.TimelineActionCallback timelineActionCallback) {
        this.timelineActionCallback = timelineActionCallback;
    }

    private class SendMailViaThread extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("usum.sender@gmail.com", "!@#usumsender123");
                UserEntity userEntity = Global.userEntity;
                sender.sendMail(
                        "교복통 타임라인 댓글 신고접수(" + timelineCommentCardDto.commentEntity.timeline_item_uuid + ")",
                        "COMMENT UUID: " + timelineCommentCardDto.commentEntity.timeline_item_uuid + "\n" +
                                "내용: " + timelineCommentCardDto.commentEntity.contents + "\n" +
                                "작성자: " + timelineCommentCardDto.userEntity.realName + "\n" +
                                "신고자: " + userEntity.realName + "(" + userEntity.id + ")",
                        "usum.sender@gmail.com",
                        "usum.dev@gmail.com");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("정상적으로 처리되었습니다.")
                        .show();
            } else {
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("신고하는 도중에 문제가 발생하였습니다.")
                        .show();
            }
        }
    }

    public void setTimelineCommentCardDtos(TimelineCommentCardDto timelineCommentCardDto) {
        this.timelineCommentCardDto = timelineCommentCardDto;
        viewHolder.writerView.setUserEntity(timelineCommentCardDto.userEntity);
        viewHolder.writerView.setWrittenTime(timelineCommentCardDto.commentEntity.created);
        viewHolder.writerView.moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMorePopup();
            }
        });

        viewHolder.contents.setText(timelineCommentCardDto.commentEntity.contents);
    }


    public void setFrom(int from) {
        this.from = from;
    }


    public void processDeleteComment(int code) {

    }
}
