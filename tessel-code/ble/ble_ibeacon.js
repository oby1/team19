var tessel = require('tessel');
var bleLib = require('ble-ble113a');
var bleadvertise = require('bleadvertise');

var uuid = 'D9B9EC1F392543D080A91E39D4CEA95C'; // Apple's example UUID
var major = '01';
var minor = '10';

var iBeaconData = new Buffer(uuid+major+minor, 'hex'); // Create data Buffer

var packet = {
    flags: [0x04], // BLE only
    mfrData : iBeaconData
}

var ad = bleadvertise.serialize(packet);

var beacon = bleLib.use(tessel.port['A'], function(){
	beacon.setAdvertisingData(ad, function(){
		beacon.startAdvertising();
		console.log('Beaconing');
	    });
    });