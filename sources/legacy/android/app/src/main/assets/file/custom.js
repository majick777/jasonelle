/*
This file is loaded automatically on $webcontainer.
Use it to modify the website with javascript.
*/

console.log("file://custom.js loaded");

window.custom_event_dispatch = (name, detail) => {
  params = {bubbles: false, cancelable: false, detail: detail};
  event = new CustomEvent(name, params);
  window.dispatchEvent(event);
}

// ------------------------
// === Jasonelle Helper ===
// ------------------------
console.log("Initializing Jasonelle object");
const $jasonelle = {};
$jasonelle.oem = "google";
$jasonelle.read = true;
$jasonelle.version = "";
$jasonelle.getversion  = () => {
  $agent.trigger("agent.getversion");
};
$jasonelle.receive_version = (version) => {
  $jasonelle.version = version;
  detail = {"version": version};
  custom_event_dispatch("jasonelle_received_version",detail);
}
window.jasonelle = $jasonelle;


// ------------------------
// === OneSignal Helper ===
// ------------------------
console.log("Initializing OneSignal");
const $onesignal = {};

// --- login / logout ---
$onesignal.login = (external_id) => {
  alert(external_id);
  $agent.trigger("onesignal.login", {externalid: external_id});
  // "externalid": "{{$jason.data.externalid}}"
};
$onesignal.receive_login = (data) => {
  alert(JSON.Stringify(data));
};
$onesignal.logout = () => {
  $agent.trigger("onesignal.logout");
};

// --- permissions ---
$onesignal.canrequest = () => {
  $agent.trigger("onesignal.canrequest");
};
$onesignal.permission = () => {
  $agent.trigger("onesignal.permission");
};
$onesignal.prompt = () => {
  $agent.trigger("onesignal.prompt");
};

// --- permission receivers ---
$onesignal.receive_canrequest = (canrequest) => {
  detail = {"canrequest": canrequest};
  custom_event_dispatch("onesignal_received_canrequest",detail);
};
$onesignal.receive_permission = (permission) => {
  detail = {"permission": permission};
  custom_event_dispatch("onesignal_received_permission",detail);
};
$onesignal.receive_prompt = (granted) => {
  detail = {"granted": granted};
  custom_event_dispatch("onesignal_received_prompt",detail);
};

// --- tags ---
$onesignal.addtag = (key, value) => {
  $agent.trigger("onesignal.addtag", {key: key, value: value});
}
$onesignal.removetag = (key) => {
  $agent.trigger("onesignal.removetag", {key: key});
}
$onesignal.gettags = () => {
  $agent.trigger("onesignal.gettags");
}
$onesignal.receive_tags = (tags) => {
  detail = {"tags": tags};
  custom_event_dispatch("onesignal_received_tags",detail);
}

// --- opt ---
$onesignal.optin = () => {
  $agent.trigger("onesignal.optin");
}
$onesignal.optout = () => {
  $agent.trigger("onesignal.optout");
}
$onesignal.status = () => {
  $agent.trigger("onesignal.status");
}
$onesignal.receive_status = (status) => {
  detail = {"status": status};
  custom_event_dispatch("onesignal_received_status",detail);
}

// --- SMS ---
$onesignal.addsms = (number) => {
  $agent.trigger("onesignal.addsms",{number: number});
}
$onesignal.removesms = (number) => {
  $agent.trigger("onesignal.removesms",{number: number});
}

// --- init observers ---
$onesignal.addobservers = () => {
  $onesignal.addpermissionobserver();
  $onesignal.addsubscriptionobserver();
  $onesignal.addforegroundobserver();
}
$onesignal.addpermissionobserver = () => {
  $agent.trigger("onesignal.permissionobserver");
}
$onesignal.receive_permissions = (permissions) => {
  detail = {"permissions": permissions};
  custom_event_dispatch("onesignal_received_permissions",detail);
  $agent.trigger("onesignal.permissionobserver");
};
$onesignal.addsubscriptionobserver = () => {
  $agent.trigger("onesignal.subscriptionobserver");
}
$onesignal.receive_subscription = (subscription) => {
  detail = {"subscription": subscription};
  custom_event_dispatch("onesignal_received_subscription",detail);
  $agent.trigger("onesignal.subscriptionobserver");
};
$onesignal.addforegroundobserver = () => {
  $agent.trigger("onesignal.foregroundobserver");
}
$onesignal.receive_notification = (notification) => {
  detail = {"notification": notification};
  custom_event_dispatch("onesignal_received_subscription",detail);
  $agent.trigger("onesignal.foregroundobserver");
};

// --- ensure is saved in global window ---
window.onesignal = $onesignal;

// -------------------------
// === Qonversion Helper ===
// -------------------------
console.log("Initializing Qonversion");
const $qonversion = {};

// --- get entitlements ---
$qonversion.entitlements = () => {
	$agent.trigger("qonversion.entitlements");
}

// --- save in global window ---
window.qonversion = $qonversion;


