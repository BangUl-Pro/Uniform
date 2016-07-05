package com.songjin.usum.reservation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReservationPushBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent reservationServiceIntent = new Intent(context, ReservationPushService.class);
        context.startService(reservationServiceIntent);
    }
}
