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

    Log.d("Debug", "OneSignal Login Action: " + action.toString());
    Log.d("Debug", "OneSignal Login Data: " + data.toString());
    try {
      // if (action.has("options")) {
      if (data.has("$jason")) {
        // JSONObject options = action.getJSONObject("options");
		// Log.d("Debug", "OneSignal Login options ", options );
        JSONObject jason = data.getJSONObject("$jason");
		Log.d("Debug", "OneSignal Login jason " + jason.toString());
        // if (options.has("externalid")){
        if (jason.has("externalid")){
          // String externalid = options.getString("externalid");
          String externalid = jason.getString("externalid");
          Log.d("Debug", "OneSignal Login with externalid " + externalid);
          OneSignal.login(externalid);
		  // JSONObject res = new JSONObject();
		  // res.put("externalid",externalid);
          JasonHelper.next("success", action, data, event, context);
        } else {
          JSONObject err = new JSONObject();
          err.put("message", "$onesignal.login externalid is missing");
		  Log.d("Debug", "$onesignal.login externalid is missing");
          JasonHelper.next("error", action, err, event, context);
        }
      } else {
        JSONObject err = new JSONObject();
        err.put("message", "$onesignal.login has no options defined");
		Log.d("Debug", "$onesignal.login has no options defined");
        JasonHelper.next("error", action, err, event, context);
      }
    } catch (Exception e) {
      Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
    }
  }

  // logout action
  public void logout(final JSONObject action, JSONObject data, final JSONObject event, final Context context) {
    Log.d("Debug", "OneSignal Logout");
    OneSignal.logout();
    JasonHelper.next("success", action, new JSONObject(), event, context);
  }

  // get external id
  public void info(final JSONObject action, JSONObject data, final JSONObject event, final Context context) {
	String userid = OneSignal.getUser().getOnesignalId();
	String externalid = OneSignal.getUser().getExternalId();
	// String user = OneSignal.getUser().toString();
    // Log.d("Debug", "OneSignal Info: " + user);
    try {
	  JSONObject ret = new JSONObject();
	  ret.put("userid", userid);
      ret.put("externalid", externalid);
      JasonHelper.next("success", action, ret, event, context);
    } catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // check if can request permissions with getCanRequestPermission()
  public void canrequest(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal getCanRequestPermission");
	Boolean result = OneSignal.getNotifications().getCanRequestPermission();
	String canrequest = result ? "1" : "0";
    JSONObject ret = new JSONObject();
    try {
      ret.put("canrequest", canrequest);
      JasonHelper.next("success", action, ret, event, context);
    } catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // get existing permission with getPermission()
  public void permission(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Boolean result = OneSignal.getNotifications().getPermission();
	String permission = result ? "1" : "0";
	Log.d("Debug", "OneSignal getPermission: " + permission);
    JSONObject ret = new JSONObject();
    try {
      ret.put("permission", permission);
      JasonHelper.next("success", action, ret, event, context);
      // return permission;
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
	// return null;
  }

  // permission prompt with requestPermission()
  // https://documentation.onesignal.com/docs/mobile-sdk-reference#push-permissions
  public void prompt(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal requestPermission");
    // try {
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
          if (r.isSuccess()) {
            String granted = "0";
            if (r.getData()) {
				Log.d("Debug", "Permission Result: "+r.getData());
				granted = "1";
			}
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
                Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
            }
          }
        }));
	// } catch (JSONException e) {
	  // Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	// }
  }

  // trigger with addTrigger()
  public void addtrigger(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal addTrigger");
    try {
      // if (action.has("options")) {
      if (data.has("$jason")) {
		// JSONObject options = action.getJSONObject("options");
        JSONObject jason = data.getJSONObject("$jason");
        if (jason.has("key") && jason.has("value")) {
		  OneSignal.getInAppMessages().addTrigger(jason.getString("key"), jason.getString("value"));
        } else {
		  JSONObject err = new JSONObject();
		  if (!jason.has("key") && !jason.has("value")) {
            err.put("message", "$onesignal.addtrigger has no key or value defined");
		  } else if (!jason.has("key")) {
            err.put("message", "$onesignal.addtrigger has no key defined");
		  }	else if (!jason.has("value")) {
            err.put("message", "$onesignal.addtrigger has no value defined");
          }
		  JasonHelper.next("error", action, err, event, context);
		}
	  } else {
		JSONObject err = new JSONObject();
		err.put("message", "onesignal.addtrigger has no data defined");
		JasonHelper.next("error", action, err, event, context);
      }
    } catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove tag with removeTag()
  public void removetrigger(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal removeTrigger");
    try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("key")) {
		  	OneSignal.getInAppMessages().removeTrigger(jason.getString("key" ));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removetrigger has no data key defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removetrigger has no data defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // add tag with addTag()
  public void addtag(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal addTag");
    try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
        // JSONObject options = action.getJSONObject("options");
		JSONObject jason = data.getJSONObject("$jason");
        if (jason.has("key") && jason.has("value")) {
          OneSignal.getUser().addTag(jason.getString("key"), jason.getString("value"));
		  Map<String,String> tagsmap = OneSignal.getUser().getTags();
	      Log.d("Debug", "New Tagsmap: "+tagsmap);
        } else {
		  JSONObject err = new JSONObject();
		  if (!jason.has("key") && !jason.has("value")) {
            err.put("message", "$onesignal.addtag has no key or value defined");
		  } else if (!jason.has("key")) {
            err.put("message", "$onesignal.addtag has no key defined");
		  }	else if (!jason.has("value")) {
            err.put("message", "$onesignal.addtag has no value defined");
          }
		  JasonHelper.next("error", action, err, event, context);
		}
	  } else {
		JSONObject err = new JSONObject();
		err.put("message", "onesignal.addtag has no data defined");
		JasonHelper.next("error", action, err, event, context);
      }
    } catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove tag with removeTag()
  public void removetag(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal removeTag");
    try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("key")) {
		  	OneSignal.getUser().removeTag(jason.getString("key" ));
			Map<String,String> tagsmap = OneSignal.getUser().getTags();
	        Log.d("Debug", "New Tagsmap: "+tagsmap);
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removetag has no data key defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removetag has no data defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // get tags with getTags()
  public void gettags(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal getTags");
	try {
      Map<String,String> tagsmap = OneSignal.getUser().getTags();
	  Log.d("Debug", "OneSignal Tagsmap: "+tagsmap);
      JSONObject tags = new JSONObject(tagsmap);
      JSONObject ret = new JSONObject();
	  ret.put("tags", tags);
	  Log.d("Debug", "OneSignal Tags Return: " + tags.toString());
      JasonHelper.next("success", action, ret, event, context);
	} catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
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
  public void status(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	Log.d("Debug", "OneSignal optedIn");
    try {
	  Boolean result = OneSignal.getUser().getPushSubscription().getOptedIn();
	  String status = result ? "1" : "0";
      JSONObject ret = new JSONObject();
      ret.put("status", status);
      JasonHelper.next("success", action, ret, event, context);
    } catch (JSONException e) {
	  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
    }
  }

  // add sms with addSms()
  public void addsms(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("number")) {
		  	OneSignal.getUser().addSms(jason.getString("number"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.addsms has no number defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removesms has no data defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove sms with removeSms();
  public void removesms(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("number")) {
		  	OneSignal.getUser().removeSms(jason.getString("number"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removesms has no number defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removesms has no options defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // add email with addEmail()
  public void addemail(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("email")) {
		  	OneSignal.getUser().addEmail(jason.getString("email"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.addemail has no email defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removeemail has no data defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
	}
  }

  // remove email with removeEmail();
  public void removeemail(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {
	try {
      // if (action.has("options")) {
	  if (data.has("$jason")) {
	    // JSONObject options = action.getJSONObject("options");
	    JSONObject jason = data.getJSONObject("$jason");
	    if (jason.has("email")) {
		  	OneSignal.getUser().removeEmail(jason.getString("email"));
		} else {
            JSONObject err = new JSONObject();
            err.put("message", "onesignal.removeemail has no email defined");
            JasonHelper.next("error", action, err, event, context);
		}
      } else {
          JSONObject err = new JSONObject();
          err.put("message", "onesignal.removeemail has no data defined");
          JasonHelper.next("error", action, err, event, context);
      }
	} catch (JSONException e) {
	  Log.d("Warning", "OneSignal " + e.getStackTrace()[0].getMethodName() + " : " + e.toString());
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
