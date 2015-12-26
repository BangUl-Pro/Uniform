package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.controllers.views.ProductDetailCardView;
import com.songjin.usum.controllers.views.TimelineCommentRecyclerView;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.socketIo.SocketException;
import com.songjin.usum.utils.StringUtil;

import java.util.ArrayList;

public class ProductDetailActivity extends BaseActivity {
    private static final String TAG = "ProductDetailActivity";

    private class ViewHolder {
        ProductDetailCardView productDetailCardView;
        TimelineCommentRecyclerView comments;
        LinearLayout commentsLayout;
        TextView commentContents;
        Button writeCommentButton;

        public ViewHolder(View view) {
            productDetailCardView = (ProductDetailCardView) view.findViewById(R.id.product_detail_card_view);
            comments = (TimelineCommentRecyclerView) view.findViewById(R.id.comments);
            commentsLayout = (LinearLayout) view.findViewById(R.id.comments_layout);
            commentContents = (TextView) view.findViewById(R.id.comment_contents);
            writeCommentButton = (Button) view.findViewById(R.id.write_comment);
        }
    }

    private ViewHolder viewHolder;

    ProductCardDto productCardDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "액티비티 시작");

        productCardDto = getIntent().getParcelableExtra("productCardDto");

        initViews(R.layout.activity_product_detail);
        requestTimelineComments();
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

        // 액션바 설정
        getSupportActionBar().setTitle("상품 상세정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.productDetailCardView.setProductCardDto(productCardDto);

        viewHolder.writeCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String timelineItemUuid = productCardDto.productEntity.uuid;
                String timelineItemUuid = productCardDto.productEntity.id;
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
                    return;
                }

                viewHolder.writeCommentButton.setEnabled(false);

                RequestManager.insertTimelineComment(
                        timelineItemUuid,
                        commentContents,
                        Global.userEntity.id,
                        new RequestManager.OnInsertTimelineComment() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "성공2");
                                requestTimelineComments();
                                viewHolder.commentContents.setText("");
                                viewHolder.commentContents.clearFocus();
                                viewHolder.writeCommentButton.setEnabled(true);
                            }

                            @Override
                            public void onException() {
                                new MaterialDialog.Builder(context)
                                        .title(R.string.app_name)
                                        .content("댓글을 작성하는 도중에 문제가 발생하였습니다.")
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
//
//                            }
//
//                            @Override
//                            public void onException(BaasioException e) {
//
//                            }
//                        }
//                );
            }
        });
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

                    if (command.equals(Global.DELETE_COMMENT)) {
                        // 댓글 삭제
//                        processDeleteComment(code);
                    } else if (command.equals(Global.DELETE_PRODUCT)) {
                        // 제품 삭제
                        processDeleteProduct(code);
                    }
                }
            }
        }
    }


    // TODO: 15. 11. 25. 제품 삭제
    private void processDeleteProduct(int code) {
        viewHolder.productDetailCardView.processDeleteProduct(code);
    }


    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.commentsLayout.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case ProductDetailCardView.INTENT_REQUEST_GET_IMAGES:
                viewHolder.productDetailCardView.onImagePickerActivityResult(intent);
                break;
        }
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

    public void requestTimelineComments() {
        RequestManager.getTimelineComment(productCardDto.productEntity.id, new RequestManager.OnGetTimelineComment() {
            @Override
            public void onSuccess(ArrayList<TimelineCommentCardDto> timelineCommentCardDtos) {
                for (TimelineCommentCardDto timelineCommentCardDto : timelineCommentCardDtos) {
                    timelineCommentCardDto.userEntity.picture = "";
                    if (timelineCommentCardDto.userEntity.id.equals(productCardDto.productEntity.user_id    )) {
                        timelineCommentCardDto.userEntity.realName = "기부자";
                    } else if (timelineCommentCardDto.userEntity.id.equals(productCardDto.transactionEntity.receiver_id)) {
                        timelineCommentCardDto.userEntity.realName = "구매자";
                    } else {
                        timelineCommentCardDto.userEntity.realName = "이방인";
                    }
                }
                Log.d(TAG, "comment size = " + timelineCommentCardDtos.size());
                viewHolder.comments.setTimelineCommentCardDtos(timelineCommentCardDtos);
            }

            @Override
            public void onException() {

            }
        });
    }
}
