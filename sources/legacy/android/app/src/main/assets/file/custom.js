/*
This file is loaded automatically on $webcontainer.
Use it to modify the website with javascript.
*/

console.log("file://custom.js loaded");

const custom_event_dispatch = (name, detail) => {
  params = {bubbles: false, cancelable: false, detail: detail};
  event = new CustomEvent(name, params);
  window.dispatchEvent(event);
};
window.cusom_event_dispatch = custom_event_dispatch();

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
};
window.jasonelle = $jasonelle;


// ------------------------
// === OneSignal Helper ===
// ------------------------
console.log("Initializing OneSignal");
const $onesignal = {};

// --- login / logout ---
$onesignal.login = (externalid) => {
  $agent.trigger("onesignal.login", {externalid: externalid});
};
$onesignal.receive_login = (externalid) => {
  detail = {"externalid": externalid};
  custom_event_dispatch("onesignal_received_login",detail);
  $agent.response(externalid);
};
$onesignal.logout = () => {
  $agent.trigger("onesignal.logout");
};
$onesignal.info = () => {
  $agent.trigger("onesignal.info");
};
$onesignal.receive_info = (data) => {
  detail = {"userid": data.userid, "externalid": data.externalid};
  custom_event_dispatch("onesignal_received_info",detail);
  $agent.response(data);
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
  // alert("CanRequest: "+canrequest);
  detail = {"canrequest": canrequest};
  custom_event_dispatch("onesignal_received_canrequest",detail);
  $agent.response(canrequest);
};
$onesignal.receive_permission = (permission) => {
  // alert("Permission: " + permission);
  detail = {"permission": permission};
  custom_event_dispatch("onesignal_received_permission",detail);
  $agent.response(permission);
};
$onesignal.receive_prompt = (granted) => {
  // alert("Granted: " + granted);
  detail = {"granted": granted};
  custom_event_dispatch("onesignal_received_prompt",detail);
  $agent.response(granted);
};

// --- trigger ---
$onesignal.trigger = {};
$onesignal.trigger.add = (key, value) => {
  // alert("Add Trigger - Key: "+key+" - Value: "+value);
  $agent.trigger("onesignal.addtrigger", {key: key, value: value});
};
$onesignal.trigger.remove = (key) => {
  // alert("Remove Trigger - Key: "+key);
  $agent.trigger("onesignal.removetrigger", {key: key});
};

// --- tags ---
$onesignal.tags = {};
$onesignal.tags.add = (key, value) => {
  /* alert("Add Tag - Key: "+key+" - Value: "+value); */
  $agent.trigger("onesignal.addtag", {key: key, value: value});
};
$onesignal.tags.remove = (key) => {
  /* alert("Remove Tag - Key: "+key); */
  $agent.trigger("onesignal.removetag", {key: key});
};
$onesignal.tags.get = () => {
  $agent.trigger("onesignal.gettags");
};
$onesignal.receive_tags = (tags) => {
  // alert("Tags: "+JSON.stringify(tags));
  custom_event_dispatch("onesignal_received_tags",tags);
  $agent.response(tags);
};

// --- opt ---
$onesignal.optin = () => {
  $agent.trigger("onesignal.optin");
};
$onesignal.optout = () => {
  $agent.trigger("onesignal.optout");
};
$onesignal.status = () => {
  $agent.trigger("onesignal.status");
};
$onesignal.receive_status = (status) => {
  // alert("Status: "+status);
  $agent.response(status);
  detail = {"status": status};
  custom_event_dispatch("onesignal_received_status",detail);
};

// --- SMS ---
$onesignal.sms = {};
$onesignal.sms.add = (number) => {
  $agent.trigger("onesignal.addsms",{number: number});
};
$onesignal.sms.remove = (number) => {
  $agent.trigger("onesignal.removesms",{number: number});
};

// --- Email ---
$onesignal.email = {};
$onesignal.email.add = (email) => {
  $agent.trigger("onesignal.addemail",{email: email});
};
$onesignal.email.remove = (number) => {
  $agent.trigger("onesignal.removeemail",{email: email});
};

// --- init observers ---
$onesignal.addobservers = () => {
  $onesignal.addpermissionobserver();
  $onesignal.addsubscriptionobserver();
  $onesignal.addforegroundobserver();
  // alert('OneSignal Obervers Added');
};
$onesignal.addpermissionobserver = () => {
  $agent.trigger("onesignal.permissionobserver");
};
$onesignal.receive_permissions = (permissions) => {
  alert("Permission Status Changed to: "+permissions);
  detail = {"permissions": permissions};
  custom_event_dispatch("onesignal_received_permissions",detail);
  // $agent.trigger("onesignal.permissionobserver");
};
$onesignal.addsubscriptionobserver = () => {
  $agent.trigger("onesignal.subscriptionobserver");
};
$onesignal.receive_subscription = (status) => {
  alert("Subscription Status Changed to: "+status);
  detail = {"status": status};
  custom_event_dispatch("onesignal_received_subscription",detail);
  // $agent.trigger("onesignal.subscriptionobserver");
};
$onesignal.addforegroundobserver = () => {
  $agent.trigger("onesignal.foregroundobserver");
};
$onesignal.receive_notification = (notification) => {
  alert("New Notification: "+JSON.stringify(notification));
  detail = {"notification": notification};
  custom_event_dispatch("onesignal_received_subscription",detail);
  // $agent.trigger("onesignal.foregroundobserver");
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
};

// --- save in global window ---
window.qonversion = $qonversion;


