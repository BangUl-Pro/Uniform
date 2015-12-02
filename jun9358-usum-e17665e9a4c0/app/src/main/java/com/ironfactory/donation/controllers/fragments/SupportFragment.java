package com.ironfactory.donation.controllers.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.activities.FaqActivity;
import com.ironfactory.donation.controllers.activities.MainActivity;
import com.ironfactory.donation.controllers.activities.SupportCompletedActivity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class SupportFragment extends SlidingBaseFragment {
    public class ViewHolder {
        public Button launchInAppBilling;
        public Button showFaq;

        public ViewHolder(View view) {
            launchInAppBilling = (Button) view.findViewById(R.id.launch_in_app_billing);
            showFaq = (Button) view.findViewById(R.id.show_faq);
        }
    }

    private ViewHolder viewHolder;

    public static BillingProcessor billingProcessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        viewHolder = new ViewHolder(view);
        viewHolder.launchInAppBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .items(R.array.supprotItems)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                ArrayList<String> supportSkus = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.supportSkus)));
                                billingProcessor.purchase((Activity) MainActivity.context, supportSkus.get(i));
                            }
                        })
                        .show();
            }
        });
        viewHolder.showFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.startActivityUsingStack(FaqActivity.class);
            }
        });
        viewHolder.launchInAppBilling.setEnabled(false);
        initBillingProcessor(new BillingProcessorInitCallback() {
            @Override
            public void onBillingInitialized() {
                viewHolder.launchInAppBilling.setEnabled(true);
            }
        });
        return view;
    }

    public interface BillingProcessorInitCallback {
        void onBillingInitialized();
    }

    public static void initBillingProcessor(final BillingProcessorInitCallback callback) {
        billingProcessor = new BillingProcessor(
                MainActivity.context,
                BaseActivity.context.getResources().getString(R.string.google_public_key),
                new BillingProcessor.IBillingHandler() {
                    @Override
                    public void onProductPurchased(String productId, TransactionDetails transactionDetails) {
                        Log.d("USUM", "[onProductPurchased]");
                        Log.d("USUM", productId);
                        Log.d("USUM", transactionDetails.toString());

                        billingProcessor.consumePurchase(BaseActivity.context.getResources().getString(R.string.sku_id));
                        BaseActivity.startActivityUsingStack(SupportCompletedActivity.class);
                    }

                    @Override
                    public void onPurchaseHistoryRestored() {
                        Log.d("USUM", "[onPurchaseHistoryRestored]");

                        for (String sku : billingProcessor.listOwnedProducts()) {
                            Log.d("USUM", "Owned Managed Product: " + sku);
                        }
                        for (String sku : billingProcessor.listOwnedSubscriptions()) {
                            Log.d("USUM", "Owned Subscription: " + sku);
                        }
                    }

                    @Override
                    public void onBillingError(int errorCode, Throwable throwable) {
                        Log.d("USUM", "[onBillingError]");
                        Log.d("USUM", errorCode + "");
                        Log.d("USUM", throwable != null ? throwable.getMessage() : "throwable is null");

                        if (errorCode == 1) {   // 단순 취소
                            return;
                        }
                        new MaterialDialog.Builder(BaseActivity.context)
                                .title(R.string.app_name)
                                .content("인앱결제 진행중에 문제가 발생하였습니다.")
                                .show();
                    }

                    @Override
                    public void onBillingInitialized() {
                        Log.d("USUM", "[onBillingInitialized]");

                        billingProcessor.consumePurchase(BaseActivity.context.getResources().getString(R.string.sku_id));
                        if (callback != null) {
                            callback.onBillingInitialized();
                        }
                    }
                });
    }

    public static void destroyBillingProcess() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
    }

    @Override
    public void onPageSelected() {
        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                BaseActivity.showGuestBlockedDialog();
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                break;
        }
    }

    @Override
    public void onDestroy() {
        destroyBillingProcess();

        super.onDestroy();
    }
}
