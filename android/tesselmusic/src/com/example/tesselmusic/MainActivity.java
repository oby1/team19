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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

	protected static final String ACTION_GATT_SERVICES_DISCOVERED = null;

	BluetoothAdapter mBluetoothAdapter = null;
	String mCurrentArtist = null;
	String mCurrentSong = null;

    IntentFilter mMusicChangedIntentFilter;

    TextView mNameTextView;
    TextView mAvatarTextView;

    SharedPreferences mPrefs;
	
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

	private static JSONObject getJsonObjectFromMap(Map<String, String> params) throws JSONException {
	    JSONObject json = new JSONObject();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			json.put(entry.getKey(), entry.getValue());
		}
		return json;
	}
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
//				    android.util.Log.i("tesselmusic", "Device found!: " + device.getName() + " Signal strength: " + Integer.toString(rssi));
					if (device.getName() != null && device.getName().equals("Team19")) {
						//device.conn
					    android.util.Log.i("tesselmusic", "Team19 Beacon: " + device.getName() + " Signal strength: " + Integer.toString(rssi));
					    final DefaultHttpClient httpclient = new DefaultHttpClient();
					    final HttpPost httpost = new HttpPost("http://192.168.1.229:8080/api/v1/users");
					    Map<String, String> params = new HashMap<String, String>();
					    params.put("name", mNameTextView.getText().toString());
					    params.put("avatar_url", mAvatarTextView.getText().toString());
					    params.put("song_name", mCurrentSong);
					    params.put("song_artist", mCurrentArtist);
					    params.put("distance", Integer.toString(rssi));
					    JSONObject holder = null;
						try {
							holder = getJsonObjectFromMap(params);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    StringEntity se = null;
						try {
							se = new StringEntity(holder.toString());
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    httpost.setEntity(se);
					    httpost.setHeader("Accept", "application/json");
					    httpost.setHeader("Content-type", "application/json");
                        Log.d("tesselmusic", holder.toString());

					    final ResponseHandler responseHandler = new BasicResponseHandler();
						android.util.Log.i("tesselmusic", "Sending music data");

                        new Thread(new Runnable() {
                            public void run() {
                              try {
                                httpclient.execute(httpost, responseHandler);
                            } catch (ClientProtocolException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            }
                          }).start();

							//httpclient.execute(httpost, responseHandler);
					}   
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

        mMusicChangedIntentFilter = new IntentFilter();
        mMusicChangedIntentFilter.addAction("com.android.music.metachanged");
        mMusicChangedIntentFilter.addAction("com.android.music.playstatechanged");
        mMusicChangedIntentFilter.addAction("com.android.music.playbackcomplete");
        mMusicChangedIntentFilter.addAction("com.android.music.queuechanged");

        mNameTextView = (TextView)findViewById(R.id.name);
        mAvatarTextView = (TextView)findViewById(R.id.avatar);

        mPrefs = getSharedPreferences("com.opower.team19", Context.MODE_PRIVATE);

        final ImageView avatarImageView = (ImageView)findViewById(R.id.avatarImage);
        mAvatarTextView.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
//                    Uri avatarUri = Uri.parse(mAvatarTextView.getText().toString());
//                    avatarImageView.setImageURI(avatarUri);
                }
            }
        });

         mNameTextView.setText(mPrefs.getString("com.opower.team19.name", ""));
         mAvatarTextView.setText(mPrefs.getString("com.opower.team19.avatar", ""));

         ((Button)findViewById(R.id.save)).setOnClickListener(new Button.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mPrefs.edit().putString("com.opower.team19.name", mNameTextView.getText().toString()).commit();
                 mPrefs.edit().putString("com.opower.team19.avatar", mAvatarTextView.getText().toString()).commit();
             }
         });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mMusicChangedIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			Log.d("Music", artist + ":" + album + ":" + track);
			mCurrentSong = intent.getStringExtra("track");
			mCurrentArtist = intent.getStringExtra("artist");

            TextView textView = (TextView)findViewById(R.id.nowplaying);
            textView.setText(mCurrentSong);
		}
	};
}
