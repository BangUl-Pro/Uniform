package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.exception.BaasioException;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.ProductDetailCardView;
import com.songjin.usum.controllers.views.TimelineCommentRecyclerView;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {
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
                String timelineItemUuid = productCardDto.productEntity.uuid;
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
                    return;
                }

                viewHolder.writeCommentButton.setEnabled(false);
                RequestManager.insertTimelineComment(
                        timelineItemUuid,
                        commentContents,
                        new BaasioCallback<BaasioEntity>() {
                            @Override
                            public void onResponse(BaasioEntity baasioEntity) {
                                requestTimelineComments();
                                viewHolder.commentContents.setText("");
                                viewHolder.commentContents.clearFocus();
                                viewHolder.writeCommentButton.setEnabled(true);
                            }

                            @Override
                            public void onException(BaasioException e) {
                                new MaterialDialog.Builder(BaseActivity.context)
                                        .title(R.string.app_name)
                                        .content("댓글을 작성하는 도중에 문제가 발생하였습니다.")
                                        .show();
                                viewHolder.writeCommentButton.setEnabled(true);
                            }
                        }
                );
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                viewHolder.commentsLayout.setVisibility(View.GONE);
                break;
            case STUDENT:
                break;
            case PARENT:
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
        RequestManager.getTimelineComments(productCardDto.productEntity.uuid, new RequestManager.TypedBaasioQueryCallback<TimelineCommentCardDto>() {
            @Override
            public void onResponse(List<TimelineCommentCardDto> entities) {
                ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = new ArrayList<>();
                timelineCommentCardDtos.addAll(entities);
                for (TimelineCommentCardDto timelineCommentCardDto : timelineCommentCardDtos) {
                    timelineCommentCardDto.userEntity.picture = "";
                    if (timelineCommentCardDto.userEntity.uuid.equals(productCardDto.productEntity.user_uuid)) {
                        timelineCommentCardDto.userEntity.realName = "기부자";
                    } else if (timelineCommentCardDto.userEntity.uuid.equals(productCardDto.transactionEntity.receiver_uuid)) {
                        timelineCommentCardDto.userEntity.realName = "구매자";
                    } else {
                        timelineCommentCardDto.userEntity.realName = "이방인";
                    }
                }

                viewHolder.comments.setTimelineCommentCardDtos(timelineCommentCardDtos);
            }

            @Override
            public void onException(BaasioException e) {
            }
        });
    }
}
