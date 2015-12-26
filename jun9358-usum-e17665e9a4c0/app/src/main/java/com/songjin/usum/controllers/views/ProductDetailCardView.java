package com.songjin.usum.controllers.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.MainActivity;
import com.songjin.usum.controllers.activities.ProductDetailActivity;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.fragments.SupportFragment;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.entities.TransactionEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.socketIo.SocketException;

import java.util.ArrayList;
import java.util.Arrays;

import nl.changer.polypicker.ImagePickerActivity;

public class ProductDetailCardView extends CardView {
    private static final String TAG = "ProductDetailCardView";
    private static final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
    private class ViewHolder {
        public ProductCardView productCardView;
        public ProductAddForm productAddForm;
        public MaterialDialog productUpdateDialog;
        public TextView contents;
        public TimelineImageRecyclerView images;
        public Button donateButton;
        public Button cancelButton;
        public Button updateButton;
        public Button deleteButton;

        public ViewHolder(View view) {
            productCardView = (ProductCardView) view.findViewById(R.id.product_card_view);
            productAddForm = new ProductAddForm(BaseActivity.context);
            productUpdateDialog = new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .customView(productAddForm, true)
                    .build();
            contents = (TextView) view.findViewById(R.id.contents);
            images = (TimelineImageRecyclerView) view.findViewById(R.id.images);
            donateButton = (Button) view.findViewById(R.id.donate_button);
            cancelButton = (Button) view.findViewById(R.id.cancel_button);
            updateButton = (Button) view.findViewById(R.id.update_button);
            deleteButton = (Button) view.findViewById(R.id.delete_button);
        }
    }

    public static final int INTENT_REQUEST_GET_IMAGES = 1;
    public static final int MAXIMUM_IMAGES = 5;

    private ViewHolder viewHolder;
    private ProductCardDto productCardDto;
    private ProductCardDto productCardDtoForUpdate;

    public ProductDetailCardView(Context context) {
        this(context, null);
    }

    public ProductDetailCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductDetailCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_product_detail_card, this);

        viewHolder = new ViewHolder(view);

        final RequestManager.OnUpdateTransactionStatus onUpdateTransactionStatus = new RequestManager.OnUpdateTransactionStatus() {
            @Override
            public void onSuccess(TransactionEntity transactionEntity) {
                productCardDto.transactionEntity = transactionEntity;
                notifyTransactionStatusChanged();

                ArrayList<String> targetUserUuids = new ArrayList<>();
                String pushMessage = "";
                String signedUserUuid = Global.userEntity.getId();

                Log.d(TAG, "제품 상태 = " + productCardDto.transactionEntity.status);

                switch (productCardDto.transactionEntity.status) {
                    case Global.REGISTERED:
                        writeCommentToShareMyProfile();

                        if (productCardDto.transactionEntity.donator_id.equals(signedUserUuid)) {
                            Log.d(TAG, "기부자와 내가 일치함 ");
                            targetUserUuids.add(productCardDto.transactionEntity.receiver_id);
                        } else if (productCardDto.transactionEntity.receiver_id.equals(signedUserUuid)) {
                            Log.d(TAG, "기부 요청자와 내가 일치함 ");
                            targetUserUuids.add(productCardDto.transactionEntity.donator_id);
                        }
                        pushMessage = "진행중이던 거래가 취소되었습니다.";
                        break;
                    case Global.REQUESTED:
                        writeCommentToShareMyProfile();

                        targetUserUuids.add(productCardDto.transactionEntity.donator_id);
                        pushMessage = "기부요청이 들어왔습니다.";
                        break;
                    case Global.SENDED:
                        targetUserUuids.add(productCardDto.transactionEntity.receiver_id);
                        pushMessage = "기부자가 상품을 발송하였습니다.";
                        break;
                    case Global.RECEIVED:
                        if (productCardDto.transactionEntity.donator_id.equals(signedUserUuid)) {
                            Log.d(TAG, "기부자와 내가 일치함");
                            new MaterialDialog.Builder(BaseActivity.context)
                                    .title(R.string.app_name)
                                    .content("정상적으로 처리되었습니다.")
                                    .positiveText("확인")
                                    .cancelable(false)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            super.onPositive(dialog);

                                            ((Activity) getContext()).finish();
                                        }
                                    })
                                    .show();
                            return;
                        }

                        Log.d(TAG, "기부자와 내가 일치하지 않음");
                        new MaterialDialog.Builder(BaseActivity.context)
                                .title(R.string.app_name)
                                .content("교복이 마음에 드시나요?" + "\n" +
                                        "이 앱을 위해 후원해주세요!")
                                .positiveText("후원 바로가기")
                                .negativeText("취소")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        SupportFragment.initBillingProcessor(new SupportFragment.BillingProcessorInitCallback() {
                                            public void onBillingInitialized() {
                                                new MaterialDialog.Builder(BaseActivity.context)
                                                        .title(R.string.app_name)
                                                        .items(R.array.supprotItems)
                                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                                            @Override
                                                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                                                ArrayList<String> supportSkus = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.supportSkus)));
                                                                SupportFragment.billingProcessor.purchase((Activity) MainActivity.context, supportSkus.get(i));
                                                            }
                                                        })
                                                        .show();
                                            }
                                        });
                                        super.onPositive(dialog);
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        ((Activity) getContext()).finish();
                                    }
                                })
                                .show();
                        writeTimelineThatTransactionCompleted();

                        targetUserUuids.add(productCardDto.transactionEntity.donator_id);
                        pushMessage = "구매자가 상품을 수취하였습니다.";

                        // activity가 닫혀서 dialog가 표시안되서 특별처리
                        PushManager.sendTransactionPush(targetUserUuids, pushMessage);

                        // 예약알림에서 제거
                        SettingFragment.removeReservedCategory(
                                productCardDto.productEntity.school_id,
                                productCardDto.productEntity.category
                        );
                        return;
                }
                PushManager.sendTransactionPush(targetUserUuids, pushMessage);

                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("정상적으로 처리되었습니다.")
                        .positiveText("확인")
                        .cancelable(false)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                ((Activity) getContext()).finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onException() {

            }
        };

        viewHolder.donateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ONE_WEEK < System.currentTimeMillis() - productCardDto.transactionEntity.modified) {
                    productCardDto.transactionEntity.receiver_id = Global.userEntity.id;
                    RequestManager.updateTransactionStatus(productCardDto.transactionEntity, Global.RECEIVED, onUpdateTransactionStatus);
                    return;
                }

                switch (productCardDto.transactionEntity.status) {
                    case Global.REGISTERED:
                        productCardDto.transactionEntity.receiver_id = Global.userEntity.id;
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, Global.REQUESTED, onUpdateTransactionStatus);
                        break;
                    case Global.REQUESTED:
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, Global.SENDED, onUpdateTransactionStatus);
                        break;
                    case Global.SENDED:
                        productCardDto.transactionEntity.receiver_id = Global.userEntity.id;
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, Global.RECEIVED, onUpdateTransactionStatus);
                        break;
                    case Global.RECEIVED:
                        break;
                }
            }
        });
        viewHolder.cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestManager.updateTransactionStatus(productCardDto.transactionEntity, Global.REGISTERED, onUpdateTransactionStatus);

                RequestManager.getTimelineComment(productCardDto.productEntity.id, new RequestManager.OnGetTimelineComment() {
                    @Override
                    public void onSuccess(ArrayList<TimelineCommentCardDto> timelineCommentCardDtos) {
                        for (TimelineCommentCardDto timelineCommentCardDto : timelineCommentCardDtos) {
                            RequestManager.deleteComment(timelineCommentCardDto, new RequestManager.OnDeleteComment() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onException() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onException() {

                    }
                });
            }
        });
        viewHolder.updateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.productAddForm.setProductCardDto(productCardDto);
                viewHolder.productUpdateDialog.show();
            }
        });
        viewHolder.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.showLoadingView();
                RequestManager.deleteProduct(productCardDto.productEntity.id, new RequestManager.OnDeleteProduct() {
                    @Override
                    public void onSuccess() {
                        BaseActivity.hideLoadingView();
                        ((Activity) BaseActivity.context).finish();
                    }

                    @Override
                    public void onException() {
                        BaseActivity.hideLoadingView();

                        new MaterialDialog.Builder(BaseActivity.context)
                                .title(R.string.app_name)
                                .content("상품을 삭제하는 중에 문제가 발생하였습니다.")
                                .show();
                    }
                });
            }
        });
        viewHolder.productAddForm.setAttachImageButtonOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.context, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, MAXIMUM_IMAGES);
                ((Activity) ProductDetailActivity.context).startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
            }
        });
        viewHolder.productAddForm.setOnSubmitListener(new ProductAddForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.showLoadingView();

                productCardDtoForUpdate = viewHolder.productAddForm.getProductCardDto();
                productCardDtoForUpdate.productEntity.user_id = productCardDto.productEntity.user_id;
                productCardDtoForUpdate.productEntity.product_name = productCardDto.productEntity.product_name;

                if (productCardDtoForUpdate.fileEntities != null && productCardDtoForUpdate.fileEntities.size() > 0) {
                    RequestManager.deleteFile(productCardDto.fileEntities, new RequestManager.OnDeleteFile() {
                        @Override
                        public void onSuccess() {
                            BaseActivity.hideLoadingView();
                            ((Activity) BaseActivity.context).finish();
                        }

                        @Override
                        public void onException() {
                            BaseActivity.hideLoadingView();

                            new MaterialDialog.Builder(BaseActivity.context)
                                    .title(R.string.app_name)
                                    .content("상품을 삭제하는 중에 문제가 발생하였습니다.")
                                    .show();
                        }
                    });
                }

                final boolean isDeleteFile = productCardDtoForUpdate.uris.get(0).toString().contains("com.jerryjang.donation/cache") ? false : true;

                RequestManager.updateProduct(productCardDtoForUpdate, isDeleteFile, new RequestManager.OnUpdateProduct() {
                    @Override
                    public void onSuccess(ProductEntity productEntity) {
                        Log.d(TAG, "업데이트 성공");
                        for (int i = 0; i < productCardDtoForUpdate.uris.size(); i++) {
                            Uri uri = productCardDtoForUpdate.uris.get(i);
                            RequestManager.insertFile(
                                    productEntity.id,
                                    uri.toString(),
                                    i,
                                    new RequestManager.OnInsertFile() {
                                        @Override
                                        public void onSuccess(int position) {
                                            if (position == productCardDtoForUpdate.uris.size() - 1) {
                                                BaseActivity.hideLoadingView();
                                                viewHolder.productUpdateDialog.hide();
                                                ((Activity) BaseActivity.context).finish();
                                            }
                                        }

                                        @Override
                                        public void onException(int code) {

                                        }
                                    }
                            );
                        }

                        if (productCardDtoForUpdate.uris.size() == 0 || !isDeleteFile) {
                            BaseActivity.hideLoadingView();
                            viewHolder.productUpdateDialog.hide();
                            ((Activity) BaseActivity.context).finish();
                        }
                    }

                    @Override
                    public void onException() {

                    }
                });
            }
        });

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.donateButton.setVisibility(View.GONE);
                viewHolder.cancelButton.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                break;
        }
    }


    public void processDeleteProduct(int code) {
        if (code == SocketException.SUCCESS) {
            BaseActivity.hideLoadingView();
            ((Activity) BaseActivity.context).finish();
        } else {
            BaseActivity.hideLoadingView();

            new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .content("상품을 삭제하는 중에 문제가 발생하였습니다.")
                    .show();
        }
    }


    public void onImagePickerActivityResult(Intent intent) {
        Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

        if (parcelableUris == null) {
            return;
        }

        ArrayList<Uri> selectedImageUris = new ArrayList<>();
        for (Parcelable parcelableUri : parcelableUris) {
            selectedImageUris.add(Uri.parse(parcelableUri.toString()));
        }

        viewHolder.productAddForm.setSelectedImageUris(selectedImageUris);
    }

    private void writeCommentToShareMyProfile() {
        UserEntity userEntity = Global.userEntity;
        String commentContents =
                "이름: " + userEntity.realName + "\n" +
                        "휴대폰번호: " + userEntity.phone;

        RequestManager.insertTimelineComment(
                productCardDto.productEntity.id,
                commentContents,
                Global.userEntity.id,
                new RequestManager.OnInsertTimelineComment() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "성공");
                    }

                    @Override
                    public void onException() {

                    }
                });

//        RequestManager.insertTimelineComment(
//                productCardDto.productEntity.uuid,
//                commentContents,
//                new BaasioCallback<BaasioEntity>() {
//                    @Override
//                    public void onResponse(BaasioEntity baasioEntity) {
//
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//
//                    }
//                }
//        );
    }

    private void writeTimelineThatTransactionCompleted() {
        RequestManager.insertTimeline(productCardDto.productEntity.school_id, null, Global.userEntity.id, "교복기부로 점수획득!", new RequestManager.OnInsertTimeline() {
            @Override
            public void onSuccess(TimelineCardDto timelineCardDto) {

            }

            @Override
            public void onException() {

            }
        });
    }

    public void setProductCardDto(ProductCardDto productCardDto) {
        this.productCardDto = productCardDto;

        UserEntity userEntity = Global.userEntity;
        if (productCardDto.productEntity.user_id.equals(userEntity.id) &&
                productCardDto.transactionEntity.status == Global.REGISTERED) {
            viewHolder.cancelButton.setVisibility(GONE);
            viewHolder.updateButton.setVisibility(VISIBLE);
        }

        viewHolder.productCardView.setProductCardDto(productCardDto);
        viewHolder.contents.setText(productCardDto.productEntity.contents);
        viewHolder.images.setFileEntities(productCardDto.fileEntities);
        notifyTransactionStatusChanged();
    }

    private void notifyTransactionStatusChanged() {
        String donatorUuid = productCardDto.transactionEntity.donator_id;
        String receiverUuid = productCardDto.transactionEntity.receiver_id;
//        String signedUserUuid = Baas.io().getSignedInUser().getUuid().toString();
        String signedUserUuid = Global.userEntity.id;
        Log.d(TAG, "상태 = " +  productCardDto.transactionEntity.status);
        switch (productCardDto.transactionEntity.status) {
            case Global.REGISTERED:
                if (donatorUuid.equals(signedUserUuid)) {
                    Log.d(TAG, "기부자와 내가 일치");
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "기부자와 내가 일치하지 않음");
                    viewHolder.donateButton.setVisibility(View.VISIBLE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("기부요청");
                }
                break;
            case Global.REQUESTED:
                if (donatorUuid.equals(signedUserUuid)) {
                    Log.d(TAG, "기부자와 내가 일치");
                    viewHolder.donateButton.setVisibility(View.VISIBLE);
                    viewHolder.cancelButton.setVisibility(View.VISIBLE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("발송완료");
                } else if (receiverUuid.equals(signedUserUuid)) {
                    Log.d(TAG, "구매자와 내가 일치");
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.VISIBLE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("발송대기중");
                } else {
                    Log.d(TAG, "그 누구와도 일치하지 않음");
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
                break;
            case Global.SENDED:
                if (donatorUuid.equals(signedUserUuid)) {
                    Log.d(TAG, "기부자와 내가 일치");
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("수취대기중");

                    if (ONE_WEEK < System.currentTimeMillis() - productCardDto.transactionEntity.modified) {
                        viewHolder.donateButton.setVisibility(View.VISIBLE);
                        viewHolder.donateButton.setText("거래완료처리");
                    }
                } else if (receiverUuid.equals(signedUserUuid)) {
                    viewHolder.donateButton.setVisibility(View.VISIBLE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("수취완료");
                } else {
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
                break;
            case Global.RECEIVED:
                viewHolder.donateButton.setVisibility(View.GONE);
                viewHolder.cancelButton.setVisibility(View.GONE);
                viewHolder.updateButton.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.GONE);
                break;
        }
    }
}
