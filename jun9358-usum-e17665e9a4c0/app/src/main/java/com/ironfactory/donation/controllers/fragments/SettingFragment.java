package com.ironfactory.donation.controllers.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.activities.LoginActivity;
import com.ironfactory.donation.controllers.activities.MainActivity;
import com.ironfactory.donation.entities.AlarmEntity;
import com.ironfactory.donation.entities.ReservedCategoryEntity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.slidingtab.SlidingBaseFragment;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.PushDeregisterHttpResponseHandler;
import com.kakao.PushService;
import com.kakao.Session;
import com.kakao.UnlinkResponseCallback;
import com.kakao.UserManagement;
import com.kakao.helper.SharedPreferencesCache;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.Map;

public class SettingFragment extends SlidingBaseFragment {
    private static final String TAG = "SettingFragment";
    public static Context context;

    private class ViewHolder {
        public Button disconnectButton;
        public Button logoutButton;
        public Switch useTransactionPush;
        public Switch useTimelinePush;

        public ViewHolder(View view) {
            disconnectButton = (Button) view.findViewById(R.id.disconnect_button);
            logoutButton = (Button) view.findViewById(R.id.logout_button);
            useTransactionPush = (Switch) view.findViewById(R.id.use_transaction_push);
            useTimelinePush = (Switch) view.findViewById(R.id.use_timeline_push);
        }
    }

    private ViewHolder viewHolder;

    public static final String PREFERENCE_USE_TRANSACTION_PUSH = "useTransactionPush";
    public static final String PREFERENCE_USE_TIMELINE_PUSH = "useTimelinePush";
    public static final String PREFERENCE_RESERVED_CATEGORIES = "useReservationPush";
    public static final String PREFERENCE_RECEIVED_PUSH_MESSAGES = "pushMessages";
    public static final String PREFERENCE_ALARM_SYNCED_TIMESTAMP = "alarmSyncedTimestamp";
    public static final String PREFERENCE_SCHOOLS_LOADED = "schoolsLoaded";
    public static final String PREFERENCE_LAST_RANK = "lastRank";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                viewHolder.useTimelinePush.setEnabled(false);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        viewHolder = new ViewHolder(view);
        viewHolder.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUnlink();
                clearAllSharedPreferences();
                Log.d(TAG, "회원 탈퇴 ");
            }
        });
        viewHolder.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogout();
            }
        });
        viewHolder.useTransactionPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SecurePreferences securePrefs = new SecurePreferences(getActivity());
                SecurePreferences.Editor editor = securePrefs.edit();
                editor.putBoolean(PREFERENCE_USE_TRANSACTION_PUSH, isChecked);
                editor.commit();
            }
        });
        viewHolder.useTimelinePush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SecurePreferences securePrefs = new SecurePreferences(getActivity());
                SecurePreferences.Editor editor = securePrefs.edit();
                editor.putBoolean(PREFERENCE_USE_TIMELINE_PUSH, isChecked);
                editor.commit();
            }
        });
//        viewHolder.useTransactionPush.setChecked(useTransactionPush(getActivity()));
//        viewHolder.useTimelinePush.setChecked(useTimelinePush(getActivity()));
        viewHolder.useTransactionPush.setChecked(useTransactionPush());
        viewHolder.useTimelinePush.setChecked(useTimelinePush());

        return view;
    }

    public static Boolean useTransactionPush() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        return securePreferences.getBoolean(SettingFragment.PREFERENCE_USE_TRANSACTION_PUSH, true);
    }

    public static Boolean useTimelinePush() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        return securePreferences.getBoolean(SettingFragment.PREFERENCE_USE_TIMELINE_PUSH, true);
    }

    private void requestUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(getActivity())
                .setMessage(appendMessage)
                .setPositiveButton(
                        getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BaseActivity.showLoadingView();
                                UserManagement.requestUnlink(new UnlinkResponseCallback() {
                                    @Override
                                    protected void onSuccess(final long userId) {
                                        BaseActivity.hideLoadingView();
                                        BaseActivity.startActivityOnTopStack(LoginActivity.class);
                                        ((Activity)MainActivity.context).finish();

                                        SharedPreferencesCache cache = Session.getAppCache();
                                        String deviceId = cache.getString(Global.ID);

                                        PushService.deregisterPushToken(new PushDeregisterHttpResponseHandler() {
                                            @Override
                                            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                                                Log.d(TAG, "토큰 제거 실패");
                                            }
                                        }, deviceId);

                                        cache.clearAll();
                                    }

                                    @Override
                                    protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                                        BaseActivity.hideLoadingView();
                                        new MaterialDialog.Builder(BaseActivity.context)
                                                .title(R.string.app_name)
                                                .content("연결해제하는 중에 세션에 문제가 생겼습니다.")
                                                .show();
                                    }

                                    @Override
                                    protected void onFailure(final APIErrorResult errorResult) {
                                        BaseActivity.hideLoadingView();
                                        new MaterialDialog.Builder(BaseActivity.context)
                                                .title(R.string.app_name)
                                                .content("연결해제에 실패하였습니다.")
                                                .show();
                                    }
                                });
//                                RequestManager.unlinkAppInBackground(new UnlinkResponseCallback() {
//                                    @Override
//                                    protected void onSuccess(final long userId) {
//                                        BaseActivity.hideLoadingView();
//                                        BaseActivity.startActivityOnTopStack(LoginActivity.class);
//                                        ((Activity) MainActivity.context).finish();
//                                    }
//
//                                    @Override
//                                    protected void onSessionClosedFailure(final APIErrorResult errorResult) {
//                                        BaseActivity.hideLoadingView();
//                                        new MaterialDialog.Builder(BaseActivity.context)
//                                                .title(R.string.app_name)
//                                                .content("연결해제하는 중에 세션에 문제가 생겼습니다.")
//                                                .show();
//                                    }
//
//                                    @Override
//                                    protected void onFailure(final APIErrorResult errorResult) {
//                                        BaseActivity.hideLoadingView();
//                                        new MaterialDialog.Builder(BaseActivity.context)
//                                                .title(R.string.app_name)
//                                                .content("연결해제에 실패하였습니다.")
//                                                .show();
//                                    }
//                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(
                        getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void requestLogout() {
        BaseActivity.showLoadingView();
        PushService.deregisterPushToken(new PushDeregisterHttpResponseHandler() {
            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                Log.d(TAG, "토큰 삭제 에러 = " + errorResult.getErrorCodeInt());
                Log.d(TAG, "토큰 삭제 에러 = " + errorResult.getErrorMessage());

            }
        }, Global.userEntity.id);
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                BaseActivity.hideLoadingView();
                BaseActivity.startActivityOnTopStack(LoginActivity.class);
                ((Activity) MainActivity.context).finish();

                SharedPreferencesCache cache = Session.getAppCache();
                cache.clearAll();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                BaseActivity.hideLoadingView();
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("로그아웃에 실패하였습니다.")
                        .show();
            }
        });
//        RequestManager.logoutAppInBackground(new LogoutResponseCallback() {
//            @Override
//            protected void onSuccess(final long userId) {
//                BaseActivity.hideLoadingView();
//                BaseActivity.startActivityOnTopStack(LoginActivity.class);
//                ((Activity) MainActivity.context).finish();
//            }
//
//            @Override
//            protected void onFailure(final APIErrorResult apiErrorResult) {
//                BaseActivity.hideLoadingView();
//                new MaterialDialog.Builder(BaseActivity.context)
//                        .title(R.string.app_name)
//                        .content("로그아웃에 실패하였습니다.")
//                        .show();
//            }
//        });
    }

    public static ArrayList<ReservedCategoryEntity> getReservedCategories() {
        ArrayList<String> reservedCategoryJsonStrings;
//        SecurePreferences securePreferences = new SecurePreferences(BaasioApplication.context);
        SecurePreferences securePreferences = new SecurePreferences(context);
        String rawJsonString = securePreferences.getString(PREFERENCE_RESERVED_CATEGORIES, "");
        Gson gson = new Gson();
        try {
            reservedCategoryJsonStrings = gson.fromJson(
                    rawJsonString,
                    new TypeToken<ArrayList<String>>() {
                    }.getType()
            );
        } catch (Exception e) {
            reservedCategoryJsonStrings = new ArrayList<>();
        }
        if (reservedCategoryJsonStrings == null) {
            return new ArrayList<>();
        }

        ArrayList<ReservedCategoryEntity> reservedCategoryEntities = new ArrayList<>();
        for (String jsonString : reservedCategoryJsonStrings) {
            reservedCategoryEntities.add(ReservedCategoryEntity.createObject(jsonString));
        }

        return reservedCategoryEntities;
    }

    public static void addReservedCategory(int schoolId, int category) {
        ArrayList<ReservedCategoryEntity> reservedCategories = getReservedCategories();
        reservedCategories.add(new ReservedCategoryEntity(schoolId, category, System.currentTimeMillis()));
        writeReservedCategory(reservedCategories);
    }

    public static void removeReservedCategory(int schoolId, int category) {
        ArrayList<ReservedCategoryEntity> reservedCategories = getReservedCategories();
        int index = 1;
        while (index != -1) {
            index = findIndexOfReservedCategories(reservedCategories ,schoolId, category);
            if (index != -1 && index < reservedCategories.size()) {
                reservedCategories.remove(index);
            }
        }
        writeReservedCategory(reservedCategories);
    }

    public static void updateReservedCategoryTimestamp(int schoolId, int category) {
        removeReservedCategory(schoolId, category);
        addReservedCategory(schoolId, category);
    }

    public static int findIndexOfReservedCategories(ArrayList<ReservedCategoryEntity> reservedCategories, int schoolId, int category) {
        int index = -1;
        for (int i=0 ; i<reservedCategories.size() ; i++) {
            ReservedCategoryEntity reservedCategory = reservedCategories.get(i);
            if (reservedCategory.schoolId == schoolId && reservedCategory.category == category) {
                index = i;
                break;
            }
        }

        return index;
    }

    private static void writeReservedCategory(ArrayList<ReservedCategoryEntity> reservedCategories) {
        ArrayList<String> reservedCategoryJsonStrings = new ArrayList<>();
        for (ReservedCategoryEntity reservedCategory : reservedCategories) {
            reservedCategoryJsonStrings.add(reservedCategory.toString());
        }

        SecurePreferences securePrefs = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePrefs.edit();
        Gson gson = new Gson();
        editor.putString(PREFERENCE_RESERVED_CATEGORIES, gson.toJson(reservedCategoryJsonStrings));
        editor.commit();
    }

    public static ArrayList<AlarmEntity> getReceivedPushMessages() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        String pushMessageJsonString = securePreferences.getString(PREFERENCE_RECEIVED_PUSH_MESSAGES, "");

        Gson gson = new Gson();
        ArrayList<String> pushMessageJsonStrings;
        try {
            pushMessageJsonStrings = gson.fromJson(
                    pushMessageJsonString,
                    new TypeToken<ArrayList<String>>() {
                    }.getType());
        } catch (Exception e) {
            pushMessageJsonStrings = new ArrayList<>();
        }
        if (pushMessageJsonStrings == null) {
            return new ArrayList<>();
        }

        ArrayList<AlarmEntity> receivedPushMessages = new ArrayList<>();
        for (String jsonString : pushMessageJsonStrings) {
            receivedPushMessages.add(gson.fromJson(jsonString, AlarmEntity.class));
        }

        return receivedPushMessages;
    }
//    public static ArrayList<BaasioPayload> getReceivedPushMessages() {
//        SecurePreferences securePreferences = new SecurePreferences(BaasioApplication.context);
//        String pushMessageJsonString = securePreferences.getString(PREFERENCE_RECEIVED_PUSH_MESSAGES, "");
//
//        Gson gson = new Gson();
//        ArrayList<String> pushMessageJsonStrings;
//        try {
//            pushMessageJsonStrings = gson.fromJson(
//                    pushMessageJsonString,
//                    new TypeToken<ArrayList<String>>() {
//                    }.getType());
//        } catch (Exception e) {
//            pushMessageJsonStrings = new ArrayList<>();
//        }
//        if (pushMessageJsonStrings == null) {
//            return new ArrayList<>();
//        }
//
//        ArrayList<BaasioPayload> receivedPushMessages = new ArrayList<>();
//        for (String jsonString : pushMessageJsonStrings) {
//            receivedPushMessages.add(BaasioPayload.createObject(jsonString));
//        }
//
//        return receivedPushMessages;
//    }

    public static void addReceivedPushMessage(AlarmEntity alarmEntity) {
        ArrayList<AlarmEntity> pushMessages = getReceivedPushMessages();
        pushMessages.add(alarmEntity);

        Gson gson = new Gson();
        ArrayList<String> pushMessageJsonStrings = new ArrayList<>();
        for (AlarmEntity pushMessage : pushMessages) {

            pushMessageJsonStrings.add(0, gson.toJson(pushMessage));
            Log.d(TAG, "pushMessage = " + gson.toJson(pushMessage));
        }

        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putString(PREFERENCE_RECEIVED_PUSH_MESSAGES, gson.toJson(pushMessageJsonStrings));
        editor.commit();
    }
//    public static void addReceivedPushMessage(BaasioPayload baasioPayload) {
//        ArrayList<BaasioPayload> pushMessages = getReceivedPushMessages();
//        pushMessages.add(baasioPayload);
//
//        ArrayList<String> pushMessageJsonStrings = new ArrayList<>();
//        for (BaasioPayload pushMessage : pushMessages) {
//            pushMessageJsonStrings.add(0, pushMessage.toString());
//        }
//
//        SecurePreferences securePreferences = new SecurePreferences(BaasioApplication.context);
//        SecurePreferences.Editor editor = securePreferences.edit();
//        Gson gson = new Gson();
//        editor.putString(PREFERENCE_RECEIVED_PUSH_MESSAGES, gson.toJson(pushMessageJsonStrings));
//        editor.commit();
//    }

    public static long getLastAlarmSyncedTimestamp() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        return securePreferences.getLong(PREFERENCE_ALARM_SYNCED_TIMESTAMP, 0);
    }

    public static void updateLastAlarmSyncedTimestamp() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putLong(PREFERENCE_ALARM_SYNCED_TIMESTAMP, System.currentTimeMillis());
        editor.commit();
    }

    private static void clearAllSharedPreferences() {
        SecurePreferences securePrefs = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePrefs.edit();
        for (Map.Entry<String, String> entry : securePrefs.getAll().entrySet()) {
            editor.remove(entry.getKey());
        }
        editor.commit();
    }

    public static int getLastSchoolRank() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        return securePreferences.getInt(PREFERENCE_LAST_RANK, -1);
    }

    public static void setLastSchoolRank(int lastRank) {
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putInt(PREFERENCE_LAST_RANK, lastRank);
        editor.commit();
    }

    public static boolean getSchoolsLoaded() {
        SecurePreferences securePreferences = new SecurePreferences(context);
        return securePreferences.getBoolean(PREFERENCE_SCHOOLS_LOADED, false);
    }

    public static void setSchoolsLoaded(boolean loaded) {
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putBoolean(PREFERENCE_SCHOOLS_LOADED, loaded);
        editor.commit();
    }
}
