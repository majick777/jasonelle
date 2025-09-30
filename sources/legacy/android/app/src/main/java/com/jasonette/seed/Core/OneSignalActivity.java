package com.jasonette.seed.Core;

import com.onesignal.OneSignal;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.inAppMessages.IInAppMessageClickListener;
import com.onesignal.inAppMessages.IInAppMessageClickEvent;
import com.onesignal.inAppMessages.IInAppMessageDidDismissEvent;
import com.onesignal.inAppMessages.IInAppMessageDidDisplayEvent;
import com.onesignal.inAppMessages.IInAppMessageLifecycleListener;
import com.onesignal.debug.LogLevel;
import com.onesignal.inAppMessages.IInAppMessageWillDismissEvent;
import com.onesignal.inAppMessages.IInAppMessageWillDisplayEvent;
import com.onesignal.notifications.IDisplayableNotification;
import com.onesignal.notifications.IPermissionObserver;
import com.onesignal.notifications.INotificationLifecycleListener;
import com.onesignal.notifications.INotificationWillDisplayEvent;
import com.onesignal.user.subscriptions.IPushSubscription;
import com.onesignal.user.subscriptions.IPushSubscriptionObserver;
import com.onesignal.user.subscriptions.PushSubscriptionState;
import com.onesignal.user.subscriptions.PushSubscriptionChangedState;
import com.onesignal.user.state.IUserStateObserver;
import com.onesignal.user.state.UserChangedState;
import com.onesignal.user.state.UserState;
// import com.onesignal.sdktest.R;
// import com.onesignal.sdktest.constant.Tag;
// import com.onesignal.sdktest.constant.Text;
// import com.onesignal.sdktest.notification.OneSignalNotificationSender;
// import com.onesignal.sdktest.util.SharedPreferenceUtil;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jasonette.seed.R;
import com.jasonette.seed.Helper.JasonHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONException;
import android.util.Log;

// TODO: check this for examples
// ref: https://github.com/OneSignal/OneSignal-Android-SDK/blob/7b2b588ba0bddfbfd65a2fe9cae5f353ef966a0f/Examples/OneSignalDemo/app/src/main/java/com/onesignal/sdktest/application/MainApplication.java#L135

public class OneSignalActivity extends Activity {

	// --- OneSignal Permission Observer ---
	public static class OneSignalPermissionObserver implements IPermissionObserver {

      private final JSONObject action;
      private final JSONObject data;
      private final JSONObject event;
      private final Context context;
      private boolean registered = false;

	  public OneSignalPermissionObserver(JSONObject action, JSONObject data, JSONObject event, Context context) {
        this.action = action;
        this.data = data;
        this.event = event;
        this.context = context;
	  }

      public void start() {
        if (registered) return;
        OneSignal.getNotifications().addPermissionObserver(this);
        registered = true;
      }

      public void stop() {
        if (!registered) return;
        OneSignal.getNotifications().removePermissionObserver(this);
        registered = false;
      }

	  @Override
	  public void onNotificationPermissionChange(boolean granted) {
		String granted_string = granted ? "1" : "0";
		Log.d("Debug", "OneSignal Notification Permission Changed: "+granted_string);
        JSONObject res = new JSONObject();
        try {
          res.put("granted", granted_string);
          JasonHelper.next("success", this.action, res, this.event, this.context);
        } catch (JSONException e) {
          Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
      }

	  // @Override
	  protected void onDestroy() {
		this.stop();
	  }
	}

	// --- OneSignal Subcription Observer ---
	public static class OneSignalSubscriptionObserver implements IPushSubscriptionObserver {

      private final JSONObject action;
      private final JSONObject data;
      private final JSONObject event;
      private final Context context;
      private boolean registered = false;

	  public OneSignalSubscriptionObserver(JSONObject action, JSONObject data, JSONObject event, Context context) {
        this.action = action;
        this.data = data;
        this.event = event;
        this.context = context;
	  }

      public void start() {
        if (registered) return;
        OneSignal.getUser().getPushSubscription().addObserver(this);
        registered = true;
      }

      public void stop() {
        if (!registered) return;
        OneSignal.getUser().getPushSubscription().removeObserver(this);
        registered = false;
      }

	  @Override
	  public void onPushSubscriptionChange(PushSubscriptionChangedState changedState) {
		// $agent.trigger("onesignal.status");
		// Log.i("OneSignal", "current subscription ID: " + changedState.getCurrent().getId());
        // String user_id = changedState.getCurrent().getId();
		// String push_token = changedState.getCurrent().getToken();
		Boolean opted_in = changedState.getCurrent().getOptedIn();
		String status = opted_in ? "1" : "0";
		Log.d("Debug", "OneSignal Notification Permission Changed: "+status);

		JSONObject res = new JSONObject();
		try {
			res.put("status",status);
			// res.put("user_id", user_id);
			// res.put("push_token", push_token);
			// res.put("subscribed", subscribed);
			// res.put("permission_status", permission_status);
			// res.put("subscription_status", subscription_status);
			// JSONObject res = changedState.getCurrent().toJSONObject();
			JasonHelper.next("success", this.action, res, this.event, this.context);
        } catch (JSONException e) {
          Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }

	  }

	  // @Override
	  protected void onDestroy() {
		this.stop();
	  }

	}

    // --- OneSignal Foreground Observer ---
	public static class OneSignalForegroundObserver implements INotificationLifecycleListener {

      private final JSONObject action;
      private final JSONObject data;
      private final JSONObject event;
      private final Context context;
      private boolean registered = false;

	  public OneSignalForegroundObserver(JSONObject action, JSONObject data, JSONObject event, Context context) {
        this.action = action;
        this.data = data;
        this.event = event;
        this.context = context;
	  }

      public void start() {
        if (registered) return;
        OneSignal.getNotifications().addForegroundLifecycleListener(this);
        registered = true;
      }

      public void stop() {
        if (!registered) return;
        OneSignal.getNotifications().removeForegroundLifecycleListener(this);
        registered = false;
      }

      @Override
	  public void onWillDisplay(INotificationWillDisplayEvent event) {

        Log.d("OneSignal", "Foreground Notification Event");
        // prevent the notification from being displayed while in the foreground
        event.preventDefault();

        String id = event.getNotification().getNotificationId();
		String title = event.getNotification().getTitle();
		String body = event.getNotification().getBody();
		// Map<String, Object> additionalData = event.getAdditionalData();
		// if (additionalData != null && additionalData.containsKey("key")) {
		//	String calue = (String) additionalData.get("key");
		// }

        JSONObject res = new JSONObject();
        try {
          res.put("id", id);
          res.put("title", title);
          res.put("body", body);
		  Log.d("OneSignal", "Notification Data: " + res.toString());
          JasonHelper.next("success", this.action, res, this.event, this.context);
        } catch (JSONException e) {
          Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
		// return false;
	  }

	  // @Override
	  protected void onDestroy() {
		  this.stop();
	  }
	}

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

}
