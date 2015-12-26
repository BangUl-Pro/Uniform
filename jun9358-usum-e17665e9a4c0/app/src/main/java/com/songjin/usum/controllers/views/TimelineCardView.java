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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.TimelineDetailActivity;
import com.songjin.usum.controllers.activities.TimelineWriteActivity;
import com.songjin.usum.GMailSender;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.entities.LikeEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.socketIo.SocketException;

public class TimelineCardView extends CardView {
    private TimelineCardDto timelineCardDto;
    private TimelineActionCallback timelineActionCallback;

    private class ViewHolder {
        public WriterView writerView;
        public TextView contents;
        public TimelineImageRecyclerView images;
        public Button commentButton;
        public ImageView divider;
        public Button likeButton;

        public ViewHolder(View view) {
            writerView = (WriterView) view.findViewById(R.id.writer_view);
            contents = (TextView) view.findViewById(R.id.contents);
            images = (TimelineImageRecyclerView) view.findViewById(R.id.images);
            commentButton = (Button) view.findViewById(R.id.comment_button);
            divider = (ImageView) view.findViewById(R.id.divider);
            likeButton = (Button) view.findViewById(R.id.like_button);
        }
    }

    private ViewHolder viewHolder;

    public TimelineCardView(Context context) {
        this(context, null);
    }

    public TimelineCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimelineCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.timeline_card, this);
        viewHolder = new ViewHolder(view);

        setViewVisibilityByAuth();
    }

    private void setViewVisibilityByAuth() {
        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.commentButton.setVisibility(View.GONE);
                viewHolder.divider.setVisibility(View.GONE);
                viewHolder.likeButton.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                viewHolder.commentButton.setVisibility(View.GONE);
                viewHolder.divider.setVisibility(View.GONE);
                viewHolder.likeButton.setVisibility(View.GONE);
                break;
        }
    }

    private void showMorePopup() {
        UserEntity userEntity = Global.userEntity;

        PopupMenu popup = new PopupMenu(getContext(), viewHolder.writerView.moreButton);
        popup.getMenuInflater().inflate(R.menu.menu_writer_more, popup.getMenu());
        if (timelineCardDto.timelineEntity.user_id.equals(userEntity.id)) {
            popup.getMenu().findItem(R.id.action_update).setVisible(true);
            popup.getMenu().findItem(R.id.action_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.action_report).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
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
                    case R.id.action_update:
                        intent = new Intent(BaseActivity.context, TimelineWriteActivity.class);
                        intent.putExtra("isUpdate", true);
                        intent.putExtra("timelineCardDto", timelineCardDto);
                        BaseActivity.startActivityUsingStack(intent);
                        return true;
                    case R.id.action_delete:
                        BaseActivity.showLoadingView();
                        RequestManager.deleteTimeline(timelineCardDto, new RequestManager.OnDeleteTimeline() {
                            @Override
                            public void onSuccess() {
                                processDeleteTimeline(200);
                            }

                            @Override
                            public void onException(int code) {
                                processDeleteTimeline(code);
                            }
                        });

//                        RequestManager.deleteTimeline(timelineCardDto, new BaasioCallback<BaasioEntity>() {
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
//                                        .content("타임라인을 삭제하는 중에 문제가 발생하였습니다.")
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


    public void processDeleteTimeline(int code) {
        if (code == SocketException.SUCCESS) {
            BaseActivity.hideLoadingView();
            if (timelineActionCallback != null) {
                timelineActionCallback.onDelete();
            }
        } else {
            BaseActivity.hideLoadingView();

            new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .content("타임라인을 삭제하는 중에 문제가 발생하였습니다.")
                    .show();
        }
    }

    public interface TimelineActionCallback {
        void onDelete();
    }

    public void setOnTimelineActionCallback(TimelineActionCallback timelineActionCallback) {
        this.timelineActionCallback = timelineActionCallback;
    }

    private class SendMailViaThread extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                GMailSender sender = new GMailSender("usum.sender@gmail.com", "!@#usumsender123");
                UserEntity userEntity = Global.userEntity;
                sender.sendMail(
                        "교복통 타임라인 신고접수(" + timelineCardDto.timelineEntity.id + ")",
                        "TIMELINE UUID: " + timelineCardDto.timelineEntity.id + "\n" +
                                "내용: " + timelineCardDto.timelineEntity.contents + "\n" +
                                "작성자: " + timelineCardDto.userEntity.realName + "\n" +
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

    public void setTimelineCardDto(final TimelineCardDto timelineCardDto) {
        this.timelineCardDto = timelineCardDto;

        viewHolder.writerView.setUserEntity(timelineCardDto.userEntity);
        viewHolder.writerView.setWrittenTime(timelineCardDto.timelineEntity.created);
        setLiked(viewHolder, !timelineCardDto.likeEntity.id.isEmpty());
        viewHolder.images.setFileEntities(timelineCardDto.fileEntities);

        viewHolder.writerView.moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMorePopup();
            }
        });
        viewHolder.commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.context, TimelineDetailActivity.class);
                intent.putExtra("timelineCardDto", timelineCardDto);
                BaseActivity.startActivityUsingStack(intent);
            }
        });
        viewHolder.likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setLiked(viewHolder, timelineCardDto.likeEntity.id.isEmpty());

                viewHolder.likeButton.setActivated(false);
                if (!timelineCardDto.likeEntity.id.isEmpty()) {
                    RequestManager.deleteLike(timelineCardDto.likeEntity, new RequestManager.OnDeleteLike() {
                        @Override
                        public void onSuccess() {
                            timelineCardDto.likeEntity = new LikeEntity();
                            viewHolder.likeButton.setActivated(true);
                        }

                        @Override
                        public void onException(int code) {
                            viewHolder.likeButton.setActivated(true);
                        }
                    });

//                    RequestManager.deleteLike(timelineCardDto.likeEntity, new BaasioCallback<BaasioEntity>() {
//                        @Override
//                        public void onResponse(BaasioEntity baasioEntity) {
//                            timelineCardDto.likeEntity = new LikeEntity();
//                            viewHolder.likeButton.setActivated(true);
//                        }
//
//                        @Override
//                        public void onException(BaasioException e) {
//                            viewHolder.likeButton.setActivated(true);
//                        }
//                    });
                } else {
                    RequestManager.insertLike(timelineCardDto.timelineEntity.id, Global.userEntity.id, new RequestManager.OnInsertLike() {
                        @Override
                        public void onSuccess(LikeEntity likeEntity) {
                            timelineCardDto.likeEntity = likeEntity;
                            viewHolder.likeButton.setActivated(true);
                        }

                        @Override
                        public void onException(int code) {
                            viewHolder.likeButton.setActivated(true);
                        }
                    });
                }
            }
        });

        // 내용 표시
        viewHolder.contents.setText(timelineCardDto.timelineEntity.contents);
    }


    private void setLiked(ViewHolder viewHolder, boolean isLiked) {
        if (isLiked) {
            viewHolder.likeButton.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_love_pressed),
                    null,
                    null,
                    null
            );
        } else {
            viewHolder.likeButton.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_love_normal),
                    null,
                    null,
                    null
            );
        }
    }

    public void setCommentButtonVisibility(int visibility) {
        viewHolder.commentButton.setVisibility(visibility);
        viewHolder.divider.setVisibility(visibility);
    }
}