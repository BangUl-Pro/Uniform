package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.controllers.views.ProductAddForm;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.R;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.TransactionEntity;
import com.songjin.usum.managers.RequestManager;

import java.util.ArrayList;

import nl.changer.polypicker.ImagePickerActivity;

public class AddProductsActivity extends BaseActivity {

    private static final String TAG = "AddProductActivity";

    private class ViewHolder {
        ProductAddForm productAddForm;
        ProductRecyclerView products;

        public ViewHolder(View view) {
            productAddForm = (ProductAddForm) view.findViewById(R.id.product_add_form);
            products = (ProductRecyclerView) view.findViewById(R.id.products);
        }
    }

    private ViewHolder viewHolder;

    private static final int INTENT_REQUEST_GET_IMAGES = 1;
    private static final int MAXIMUM_IMAGES = 5;

    private ArrayList<ProductCardDto> productCardDtos;
    private int positions = 0;
    private int size = 0;


    private final RequestManager.OnInsertProduct onInsertProduct = new RequestManager.OnInsertProduct() {
        @Override
        public void onSuccess(ProductCardDto productCardDto) {
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.donator_id = productCardDto.productEntity.user_id;
            transactionEntity.receiver_id = "";
            transactionEntity.product_id = productCardDto.productEntity.id;
            transactionEntity.product_name = productCardDto.productEntity.product_name;
            RequestManager.insertTransaction(transactionEntity);

            for (Uri uri : productCardDtos.get(positions).uris) {
                RequestManager.insertFile(productCardDto.productEntity.id, uri.toString(), positions, new RequestManager.OnInsertFile() {
                    @Override
                    public void onSuccess(int position) {
                        if (position == positions - 1) {
                            size++;
                            if (productCardDtos.get(position).uris.size() == size) {
                                hideLoadingView();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onException(int code) {
                    }
                });
            }
            positions++;
            if (productCardDtos.size() > positions) {
                RequestManager.insertProduct(productCardDtos.get(positions), onInsertProduct);
            } else if (productCardDtos.get(positions - 1).uris.size() == 0) {
                hideLoadingView();
                finish();
            }
        }

        @Override
        public void onException() {
            new MaterialDialog.Builder(context)
                    .title(R.string.app_name)
                    .content("등록 도중 에러가 발생했습니다.")
                    .show();
            hideLoadingView();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews(R.layout.activity_add_products);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
        Log.d(TAG, "액티비티 시작");

        // 액션바 설정
        getSupportActionBar().setTitle("기증하기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.productAddForm.setOnSubmitListener(new ProductAddForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                productCardDtos.add(viewHolder.productAddForm.getProductCardDto());
                viewHolder.products.setProductCardDtos(productCardDtos);

                viewHolder.productAddForm.clear();
            }
        });
        viewHolder.productAddForm.setAttachImageButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, MAXIMUM_IMAGES);
                startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
            }
        });

        productCardDtos = new ArrayList<>();
        viewHolder.products.setProductCardDtos(productCardDtos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_products, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                if (productCardDtos.isEmpty()) {
                    new MaterialDialog.Builder(context)
                            .title(R.string.app_name)
                            .content("추가할 항목이 하나도 없습니다.")
                            .show();
                    break;
                }

                showLoadingView();

                RequestManager.insertProduct(productCardDtos.get(0), onInsertProduct);


//                RequestManager.insertProductsInBackground(productCardDtos, new BaasioCallback<List<BaasioEntity>>() {
//                    @Override
//                    public void onResponse(List<BaasioEntity> baasioEntities) {
//                        ArrayList<ProductEntity> productEntities = new ArrayList<>();
//                        for (BaasioEntity baasioEntity : baasioEntities) {
//                            productEntities.add(new ProductEntity(baasioEntity));
//                        }
//
//                        RequestManager.insertTransactionsInBackground(productEntities, new BaasioCallback<List<BaasioEntity>>() {
//                            @Override
//                            public void onResponse(List<BaasioEntity> baasioEntities) {
//
//                            }
//
//                            @Override
//                            public void onException(BaasioException e) {
//
//                            }
//                        });
//
//                        for (int i = 0; i < productEntities.size(); i++) {
//                            for (Uri uri : productCardDtos.get(i).uris) {
//                                RequestManager.insertFile(
//                                        productEntities.get(i).uuid,
//                                        uri,
//                                        new BaasioUploadCallback() {
//                                            @Override
//                                            public void onResponse(BaasioFile baasioFile) {
//
//                                            }
//
//                                            @Override
//                                            public void onException(BaasioException e) {
//
//                                            }
//
//                                            @Override
//                                            public void onProgress(long l, long l2) {
//
//                                            }
//                                        }
//                                );
//                            }
//                        }
//
//                        hideLoadingView();
//                        finish();
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        // TODO: 에러메세지
//                    }
//                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                ArrayList<Uri> selectedImageUris = new ArrayList<>();
                for (Parcelable parcelableUri : parcelableUris) {
                    selectedImageUris.add(Uri.parse(parcelableUri.toString()));
                }

                viewHolder.productAddForm.setSelectedImageUris(selectedImageUris);
                break;
        }
    }
}
