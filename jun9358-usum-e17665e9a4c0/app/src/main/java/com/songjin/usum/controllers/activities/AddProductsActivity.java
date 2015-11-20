package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioUploadCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.file.BaasioFile;
import com.kth.baasio.exception.BaasioException;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.ProductAddForm;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.socketIo.SocketService;

import java.util.ArrayList;
import java.util.List;

import nl.changer.polypicker.ImagePickerActivity;

public class AddProductsActivity extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews(R.layout.activity_add_products);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

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
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content("추가할 항목이 하나도 없습니다.")
                            .show();
                    break;
                }

                showLoadingView();

                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.putExtra(Global.COMMAND, Global.INSERT_PRODUCT);
                intent.putExtra(Global.PRODUCT_CARD, productCardDtos);
                startService(intent);


                RequestManager.insertProductsInBackground(productCardDtos, new BaasioCallback<List<BaasioEntity>>() {
                    @Override
                    public void onResponse(List<BaasioEntity> baasioEntities) {
                        ArrayList<ProductEntity> productEntities = new ArrayList<>();
                        for (BaasioEntity baasioEntity : baasioEntities) {
                            productEntities.add(new ProductEntity(baasioEntity));
                        }

                        RequestManager.insertTransactionsInBackground(productEntities, new BaasioCallback<List<BaasioEntity>>() {
                            @Override
                            public void onResponse(List<BaasioEntity> baasioEntities) {

                            }

                            @Override
                            public void onException(BaasioException e) {

                            }
                        });

                        for (int i = 0; i < productEntities.size(); i++) {
                            for (Uri uri : productCardDtos.get(i).uris) {
                                RequestManager.insertFile(
                                        productEntities.get(i).uuid,
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
                        }

                        hideLoadingView();
                        finish();
                    }

                    @Override
                    public void onException(BaasioException e) {
                        // TODO: 에러메세지
                    }
                });
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
