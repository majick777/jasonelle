package com.jasonette.seed.Action;

import com.jasonette.seed.Core.OneSignalActivity;

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
import com.onesignal.notifications.INotificationLifecycleListener;
import com.onesignal.notifications.INotificationWillDisplayEvent;
import com.onesignal.user.subscriptions.IPushSubscription;
import com.onesignal.user.subscriptions.IPushSubscriptionObserver;
import com.onesignal.user.subscriptions.PushSubscriptionState;
import com.onesignal.user.state.IUserStateObserver;
import com.onesignal.user.state.UserChangedState;
import com.onesignal.user.state.UserState;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.jasonette.seed.R;
import com.jasonette.seed.Helper.JasonHelper;
import android.util.Log;

public class JasonOnesignalAction {

  // login action
  // https://documentation.onesignal.com/docs/aliases-external-id
  public void login(final JSONObject action, JSONObject data, final JSONObject event, final Context context) {

    /*
     *  This function let you set the external onesignal id for the actual user
     *
     *            "type": "$onesignal.login",
     *            "options": {
     *              "externalid": "%id%",
     *            }
     */

    Log.d("Debug", "OneSignal Login");
    try {
      if (action.has("options")) {
        JSONObject options = action.getJSONObject("options");
        if (options.has("externalid")){

          // REGISTER ID
          String externalid = options.getString("externalid");
          OneSignal.login(externalid);
          Log.d("Debug", "OneSignal Login " + externalid);
          JasonHelper.next("success", action, new JSONObject(), event, context);
        } else {
          JSONObject err = new JSONObject();
          err.put("message", "externalid is empty");
          JasonHelper.next("error", action, err, event, context);
        }
      } else {
        JSONObject err = new JSONObject();
        err.put("message", "$onesignal.login has no options defined");
        JasonHelper.next("error", action, err, event, context);
      }
    } catch (Exception e) {
      Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
    }
  }

  // logout action
  public void logout(final JSONObject action, JSONObject data, final JSONObject event, final Context context) {
    Log.d("Debug", "OneSignal Logout");
    OneSignal.logout();
    JasonHelper.next("success", action, new JSONObject(), event, context);
  }

  // check if can request permissions with getCanRequestPermission()
  public Boolean canrequest(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal getCanRequestPermission");
	Boolean canrequest = OneSignal.getNotifications().getCanRequestPermission();
    JSONObject ret = new JSONObject();
    try {
      ret.put("canrequest", canrequest);
      JasonHelper.next("success", action, ret, event, context);
      return canrequest;
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
    return null;
  }

  // get existing permission with getPermission()
  public Boolean permission(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal getPermission");
	Boolean permission = OneSignal.getNotifications().getPermission();
    JSONObject ret = new JSONObject();
    try {
      ret.put("permission", permission);
      JasonHelper.next("success", action, ret, event, context);
      return permission;
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
	 return null;
  }

  // permission prompt with requestPermission()
  // https://documentation.onesignal.com/docs/mobile-sdk-reference#push-permissions
  public void prompt(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal requestPermission");
    // try {
	  // if (this.canrequest(action, data, event, context)) {
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
          if (r.isSuccess()) {
            Boolean granted = false;
            if (r.getData()) {granted = true;}
            JSONObject ret = new JSONObject();
            try {
              ret.put("granted", granted);
              JasonHelper.next("success", action, ret, event, context);
            } catch (JSONException e) {
			  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
			}
          } else {
            // `requestPermission` completed unsuccessfully
            // check `r.getThrowable()` for more info on the failure reason
            JSONObject err = new JSONObject();
            try {
              err.put("message", "request permissions failed");
              JasonHelper.next("error", action, err, event, context);
            } catch (JSONException e) {
                Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
            }
          }
        }));
	  // }
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // add tag with addTag()
  public void addtag(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal addTag");
    try {
      if (action.has("options")) {
        JSONObject options = action.getJSONObject("options");
        if (options.has("key") && options.has("value")) {
          OneSignal.getUser().addTag(options.getString("key"), options.getString("value"));
		  /* TODO: rerun gettags? */
        } else {
		  JSONObject err = new JSONObject();
		  if (!options.has("key") && !options.has("value")) {
            err.put("message", "$onesignal.addtag has no option key or value defined");
		  } else if (!options.has("key")) {
            err.put("message", "$onesignal.addtag has no option key defined");
		  }	else if (!options.has("value")) {
            err.put("message", "$onesignal.addtag has no option value defined");
          }
		  JasonHelper.next("error", action, err, event, context);
		}
	  } else {
		JSONObject err = new JSONObject();
		err.put("message", "onesignal.addtag has no options defined");
		JasonHelper.next("error", action, err, event, context);
      }
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove tag with removeTag()
  public void removetag(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal removeTag");
    try {
      if (action.has("options")) {
	    JSONObject options = action.getJSONObject("options");
	    if (options.has("key")) {
		  	OneSignal.getUser().removeTag(options.getString("key" ));
			/* TODO: rerun gettags ? */
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removetag has no option key defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removetag has no options defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // get tags with getTags()
  public JSONObject gettags(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal getTags");
	// try {
      Map<String,String> tagsmap = OneSignal.getUser().getTags();
      JSONObject tags = new JSONObject(tagsmap);
      JasonHelper.next("success", action, tags, event, context);
      return tags;
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
    // return null;
  }

  // opt in with optIn()
  public void optin(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal optIn");
	// try {
  	  OneSignal.getUser().getPushSubscription().optIn();
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // opt out with optOut()
  public void optout(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal optOut");
    // try {
	  OneSignal.getUser().getPushSubscription().optOut();
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // opt status with optedIn()
  public Boolean status(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal optedIn");
    try {
	  Boolean optedin = OneSignal.getUser().getPushSubscription().getOptedIn();
      JSONObject ret = new JSONObject();
      ret.put("status", optedin);
      JasonHelper.next("success", action, ret, event, context);
	    return optedin;
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
    }
    return null;
  }

  // add sms with addSms()
  public void addsms(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      if (action.has("options")) {
	    JSONObject options = action.getJSONObject("options");
	    if (options.has("number")) {
		  	OneSignal.getUser().addSms(options.getString("number"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.addsms has no option number defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removesms has no options defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove sms with removeSms();
  public void removesms(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      if (action.has("options")) {
	    JSONObject options = action.getJSONObject("options");
	    if (options.has("number")) {
		  	OneSignal.getUser().removeSms(options.getString("number"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removesms has no option number defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removesms has no options defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // --- add permission observer ---
  public void permissionobserver(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	// try {
	  OneSignalActivity.OneSignalPermissionObserver observer = new OneSignalActivity.OneSignalPermissionObserver(action, data, event, context);
	  observer.start();
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // --- add subscription observer ---
  public void subscriptionobserver(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	// try {
	  OneSignalActivity.OneSignalSubscriptionObserver observer = new OneSignalActivity.OneSignalSubscriptionObserver(action, data, event, context);
	  observer.start();
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // --- add forefround observer ---
  public void foregroundobserver(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	// try {
	  OneSignalActivity.OneSignalForegroundObserver observer = new OneSignalActivity.OneSignalForegroundObserver(action, data, event, context);
	  observer.start();
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

}
