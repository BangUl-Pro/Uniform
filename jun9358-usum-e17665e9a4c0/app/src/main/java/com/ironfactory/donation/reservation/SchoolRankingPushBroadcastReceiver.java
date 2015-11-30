package com.ironfactory.donation.reservation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchoolRankingPushBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent schoolRankingPushService = new Intent(context, SchoolRankingPushService.class);
        context.startService(schoolRankingPushService);
    }
}
