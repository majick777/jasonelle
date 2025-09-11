package com.jasonette.seed.Action;

import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.QonversionConfig;

import org.json.JSONObject;
import android.content.Context;
import com.jasonette.seed.Helper.JasonHelper;
import android.util.Log;

public class JasonQonversionAction {

	// Get Entitlements
	// https://documentation.qonversion.io/docs/check-permissions

	public Boolean entitlements(final JSONObject action, JSONObject data, final JSONObject event, final Context context ) {

		/*

		if (data.has("key")) {
			Qonversion.getSharedInstance().checkEntitlements(new EntitlementByKeyAction(data.key) {
				@Override
				protected void handleSuccess(QEntitlement entitlement) {
					try {
					  // if (entitlement != null && entitlement.isActive()) {}
					  if (entitlement != null) {
						active = entitlement.isActive();
						renewstate = entitlement.getRenewState();

						/* switch (renewstate()) {
						  case.NonRenewable:
						  // NonRenewable is the state of a consumable or non-consumable in-app purchase
						  break;
						case WillRenew:
						  // WillRenew is the state of an auto-renewable subscription
						  break;
						case BillingIssue:
						  // Prompt the user to update the payment method.
						  break;
						case Canceled:
						  // The user has turned off auto-renewal for the subscription, but the subscription has not expired yet.
						  // Prompt the user to resubscribe with a special offer.
						  break;
						  default: break;
					    } */

						/* JSONObject ret = new JSONObject();
						ret.put("active", active);
						ret.put("renewstate", renewstate);
						JasonHelper.next("success", action, ret, event, context);
					  } else {
						JSONObject err = new JSONObject();
						err.put("message", "Qonversion entitlements were null");
						JasonHelper.next("error", action, err, event, context);
					  }
					} catch (JSONException e) {
					  Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
					}
				}

				@Override
				protected void handleError(QonversionError error) {
					JSONObject err = new JSONObject(error);
					JasonHelper.next("error", action, err, event, context);
				}
			});


		}

		/* Boolean permission = OneSignal.getNotifications().getPermission(); */
		/* JSONObject ret = new JSONObject();
		  try {
			ret.put("permissions", permission);
			JasonHelper.next("success", action, ret, event, context);
		  } catch (JSONException e) {}
			return permission;
		  } */


    return null;
	}

	// Get Entitlements by Key
	/*
	public abstract class EntitlementByKeyAction implements QonversionEntitlementsCallback {
		private final String key;
		public EntitlementByKeyAction(String key) { this.key = key; }

		@Override
		public void onSuccess(@NotNull Map<String, QEntitlement> entitlements) {
			handleSuccess(entitlements.get(key));
		}

		@Override
		public void onError(@NotNull QonversionError error) {
			handleError(error);
		}

		protected abstract void handleSuccess(QEntitlement entitlement); // may be null
		protected void handleError(QonversionError error) {}
	}

	*/

}
