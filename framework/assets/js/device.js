/**
 * This represents the mobile device, and provides properties for inspecting the model, version, UUID of the
 * phone, etc.
 * @constructor
 */
function Device() {
    this.available = PhoneGap.available;
    this.platform = null;
    this.version = null;
    this.name = null;
    this.uuid = null;
    this.phonegap = null;         
    this.model = null;
    this.sdkVersion = null;
    this.simSerialNumber = null;

    var me = this;
    PhoneGap.execAsync(
        function(info) {
					  //console.log("initializing PhoneGap, simSerialNumber:" + info.simSerialNumber);
            me.available = true;
            me.platform = info.platform;
            me.version = info.version;
            me.uuid = info.uuid;
            me.phonegap = info.phonegap;  
            me.name = info.name;  
            me.model = info.model;          
            me.sdkVersion = info.sdkVersion;
            me.simSerialNumber = info.simSerialNumber;            
        },
        function(e) {
            me.available = false;
            console.log("Error initializing PhoneGap: " + e);
            alert("Error initializing PhoneGap: "+e);
        },
        "Device", "getDeviceInfo", []);
}

/*
 * This is only for Android.
 *
 * You must explicitly override the back button.
 */
Device.prototype.overrideBackButton = function() {
    BackButton.override();
}

/*
 * This is only for Android.
 *
 * This resets the back button to the default behaviour
 */
Device.prototype.resetBackButton = function() {
    BackButton.reset();
}

/*
 * This is only for Android.
 *
 * This terminates the activity!
 */
Device.prototype.exitApp = function() {
    BackButton.exitApp();
}

PhoneGap.addConstructor(function() {
    navigator.device = window.device = new Device();
});
