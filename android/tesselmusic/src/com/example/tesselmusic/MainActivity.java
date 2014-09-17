package com.example.tesselmusic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

	protected static final String ACTION_GATT_SERVICES_DISCOVERED = null;

	BluetoothAdapter mBluetoothAdapter = null;
	String mCurrentArtist = null;
	String mCurrentSong = null;
	
	// Various callback methods defined by the BLE API.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				//intentAction = ACTION_GATT_CONNECTED;
				//mConnectionState = STATE_CONNECTED;
				//broadcastUpdate(intentAction);
				Log.i("tesselmusic", "Connected to GATT server.");
				Log.i("tesselmusic", "Attempting to start service discovery");

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				//intentAction = ACTION_GATT_DISCONNECTED;
				//mConnectionState = STATE_DISCONNECTED;
				Log.i("tesselmusic", "Disconnected from GATT server.");
				//broadcastUpdate(intentAction);
			}
		}

		@Override
		// New services discovered
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				//broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w("tesselmusic", "onServicesDiscovered received: " + status);
			}
		}

		@Override
		// Result of a characteristic read operation
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}
	};
	
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getName() != null && device.getName().equals("Team19")) {
						//device.conn
					    android.util.Log.i("tesselmusic", "Device found!: " + device.getName() + " Signal strength: " + Integer.toString(rssi));
					}   
					//device.connectGatt(getApplicationContext(), true, mGattCallback);
				}
			});
		}
	};
	
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
         // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged"); registerReceiver(mReceiver, iF);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { @Override
    	public void onReceive(Context context, Intent intent)
    	{
    	String action = intent.getAction();
    	String cmd = intent.getStringExtra("command");
    	Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
    	String artist = intent.getStringExtra("artist");
    	String album = intent.getStringExtra("album");
    	String track = intent.getStringExtra("track");
    	Log.d("Music",artist+":"+album+":"+track);
    	mCurrentSong = intent.getStringExtra("track");
        mCurrentArtist = intent.getStringExtra("artist");
    	}
    	};
}
