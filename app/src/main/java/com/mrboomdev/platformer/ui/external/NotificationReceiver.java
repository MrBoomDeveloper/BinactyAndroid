package com.mrboomdev.platformer.ui.external;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.react.ReactActivity;

public class NotificationReceiver extends FirebaseMessagingService {
	
	@Override
	public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
		
		Intent intent = new Intent(this, ReactActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "New Update!")
			.setSmallIcon(R.drawable.app_notification)
			.setContentTitle(message.getNotification().getTitle())
			.setContentText(message.getNotification().getBody())
			.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notificationBuilder.build());
	}
}