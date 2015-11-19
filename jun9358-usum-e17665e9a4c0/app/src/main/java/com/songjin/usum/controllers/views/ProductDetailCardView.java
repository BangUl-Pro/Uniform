package com.songjin.usum.controllers.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioUploadCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.file.BaasioFile;
import com.kth.baasio.exception.BaasioException;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.MainActivity;
import com.songjin.usum.controllers.activities.ProductDetailActivity;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.fragments.SupportFragment;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.entities.TransactionEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.changer.polypicker.ImagePickerActivity;

public class ProductDetailCardView extends CardView {
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
        final BaasioCallback<BaasioEntity> callback = new BaasioCallback<BaasioEntity>() {
            @Override
            public void onResponse(BaasioEntity baasioEntity) {
                productCardDto.transactionEntity = new TransactionEntity(baasioEntity);
                notifyTransactionStatusChanged();

                ArrayList<String> targetUserUuids = new ArrayList<>();
                String pushMessage = "";
                String signedUserUuid = Baas.io().getSignedInUser().getUuid().toString();
                switch (productCardDto.transactionEntity.status) {
                    case REGISTERED:
                        writeCommentToShareMyProfile();

                        if (productCardDto.transactionEntity.donator_uuid.equals(signedUserUuid)) {
                            targetUserUuids.add(productCardDto.transactionEntity.receiver_uuid);
                        } else if (productCardDto.transactionEntity.receiver_uuid.equals(signedUserUuid)) {
                            targetUserUuids.add(productCardDto.transactionEntity.donator_uuid);
                        }
                        pushMessage = "진행중이던 거래가 취소되었습니다.";
                        break;
                    case REQUESTED:
                        writeCommentToShareMyProfile();

                        targetUserUuids.add(productCardDto.transactionEntity.donator_uuid);
                        pushMessage = "기부요청이 들어왔습니다.";
                        break;
                    case SENDED:
                        targetUserUuids.add(productCardDto.transactionEntity.receiver_uuid);
                        pushMessage = "기부자가 상품을 발송하였습니다.";
                        break;
                    case RECEIVED:
                        if (productCardDto.transactionEntity.donator_uuid.equals(signedUserUuid)) {
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

                        targetUserUuids.add(productCardDto.transactionEntity.donator_uuid);
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
            public void onException(BaasioException e) {

            }
        };
        viewHolder.donateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1000 * 60 * 60 * 24 * 7 < System.currentTimeMillis() - productCardDto.transactionEntity.modified) {
                    RequestManager.updateTransactionStatus(productCardDto.transactionEntity, TransactionEntity.STATUS_TYPE.RECEIVED, callback);
                    return;
                }

                switch (productCardDto.transactionEntity.status) {
                    case REGISTERED:
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, TransactionEntity.STATUS_TYPE.REQUESTED, callback);
                        break;
                    case REQUESTED:
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, TransactionEntity.STATUS_TYPE.SENDED, callback);
                        break;
                    case SENDED:
                        RequestManager.updateTransactionStatus(productCardDto.transactionEntity, TransactionEntity.STATUS_TYPE.RECEIVED, callback);
                        break;
                    case RECEIVED:
                        break;
                }
            }
        });
        viewHolder.cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestManager.updateTransactionStatus(productCardDto.transactionEntity, TransactionEntity.STATUS_TYPE.REGISTERED, new BaasioCallback<BaasioEntity>() {
                    @Override
                    public void onResponse(BaasioEntity baasioEntity) {
                        productCardDto.transactionEntity = new TransactionEntity(baasioEntity);
                        notifyTransactionStatusChanged();
                    }

                    @Override
                    public void onException(BaasioException e) {

                    }
                });
                RequestManager.getTimelineComments(productCardDto.productEntity.uuid, new RequestManager.TypedBaasioQueryCallback<TimelineCommentCardDto>() {
                    @Override
                    public void onResponse(List<TimelineCommentCardDto> entities) {
                        ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = new ArrayList<>();
                        timelineCommentCardDtos.addAll(entities);
                        for (TimelineCommentCardDto timelineCommentCardDto : timelineCommentCardDtos) {
                            RequestManager.deleteComment(timelineCommentCardDto, new BaasioCallback<BaasioEntity>() {
                                @Override
                                public void onResponse(BaasioEntity baasioEntity) {
                                }

                                @Override
                                public void onException(BaasioException e) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onException(BaasioException e) {
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
                RequestManager.deleteProduct(productCardDto, new BaasioCallback<BaasioEntity>() {
                    @Override
                    public void onResponse(BaasioEntity baasioEntity) {
                        BaseActivity.hideLoadingView();
                        ((Activity) BaseActivity.context).finish();
                    }

                    @Override
                    public void onException(BaasioException e) {
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

                final ProductCardDto productCardDtoForUpdate = viewHolder.productAddForm.getProductCardDto();
                productCardDtoForUpdate.productEntity.uuid = productCardDto.productEntity.uuid;
                productCardDtoForUpdate.productEntity.product_name = productCardDto.productEntity.product_name;
                RequestManager.deleteFileEntities(productCardDto.fileEntities);
                RequestManager.updateProduct(productCardDtoForUpdate, new BaasioCallback<BaasioEntity>() {
                    @Override
                    public void onResponse(BaasioEntity baasioEntity) {
                        for (Uri uri : productCardDtoForUpdate.uris) {
                            RequestManager.insertFile(
                                    productCardDtoForUpdate.productEntity.uuid,
                                    uri,
                                    new BaasioUploadCallback() {
                                        @Override
                                        public void onResponse(BaasioFile baasioFile) {

                                        }

                                        @Override
                                        public void onException(BaasioException e) {

                                        }

                                        @Override
                                        public void onProgress(long l, long l2) {

                                        }
                                    }
                            );
                        }

                        BaseActivity.hideLoadingView();
                        viewHolder.productUpdateDialog.hide();
                        ((Activity) BaseActivity.context).finish();
                    }

                    @Override
                    public void onException(BaasioException e) {
                        BaseActivity.hideLoadingView();
                        viewHolder.productUpdateDialog.hide();
                    }
                });
            }
        });

        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                viewHolder.donateButton.setVisibility(View.GONE);
                viewHolder.cancelButton.setVisibility(View.GONE);
                break;
            case STUDENT:
                break;
            case PARENT:
                break;
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
        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        String commentConents =
                "이름: " + userEntity.realName + "\n" +
                        "휴대폰번호: " + userEntity.phone;
        RequestManager.insertTimelineComment(
                productCardDto.productEntity.uuid,
                commentConents,
                new BaasioCallback<BaasioEntity>() {
                    @Override
                    public void onResponse(BaasioEntity baasioEntity) {

                    }

                    @Override
                    public void onException(BaasioException e) {

                    }
                }
        );
    }

    private void writeTimelineThatTransactionCompleted() {
        RequestManager.insertTimeline(
                productCardDto.productEntity.school_id,
                "교복기부로 점수획득!",
                new BaasioCallback<BaasioEntity>() {
                    @Override
                    public void onResponse(BaasioEntity baasioEntity) {

                    }

                    @Override
                    public void onException(BaasioException e) {

                    }
                }
        );
    }

    public void setProductCardDto(ProductCardDto productCardDto) {
        this.productCardDto = productCardDto;

        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        if (productCardDto.productEntity.user_uuid.equals(userEntity.uuid) &&
                productCardDto.transactionEntity.status == TransactionEntity.STATUS_TYPE.REGISTERED) {
            viewHolder.cancelButton.setVisibility(GONE);
            viewHolder.updateButton.setVisibility(VISIBLE);
        }

        viewHolder.productCardView.setProductCardDto(productCardDto);
        viewHolder.contents.setText(productCardDto.productEntity.contents);
        viewHolder.images.setFileEntities(productCardDto.fileEntities);
        notifyTransactionStatusChanged();
    }

    private void notifyTransactionStatusChanged() {
        String donatorUuid = productCardDto.transactionEntity.donator_uuid;
        String receiverUuid = productCardDto.transactionEntity.receiver_uuid;
        String signedUserUuid = Baas.io().getSignedInUser().getUuid().toString();
        switch (productCardDto.transactionEntity.status) {
            case REGISTERED:
                if (donatorUuid.equals(signedUserUuid)) {
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.donateButton.setVisibility(View.VISIBLE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("기부요청");
                }
                break;
            case REQUESTED:
                if (donatorUuid.equals(signedUserUuid)) {
                    viewHolder.donateButton.setVisibility(View.VISIBLE);
                    viewHolder.cancelButton.setVisibility(View.VISIBLE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("발송완료");
                } else if (receiverUuid.equals(signedUserUuid)) {
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.VISIBLE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("발송대기중");
                } else {
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
                break;
            case SENDED:
                if (donatorUuid.equals(signedUserUuid)) {
                    viewHolder.donateButton.setVisibility(View.GONE);
                    viewHolder.cancelButton.setVisibility(View.GONE);
                    viewHolder.updateButton.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);

                    viewHolder.donateButton.setText("수취대기중");

                    if (1000 * 60 * 60 * 24 * 7 < System.currentTimeMillis() - productCardDto.transactionEntity.modified) {
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
            case RECEIVED:
                viewHolder.donateButton.setVisibility(View.GONE);
                viewHolder.cancelButton.setVisibility(View.GONE);
                viewHolder.updateButton.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.GONE);
                break;
        }
    }
}
