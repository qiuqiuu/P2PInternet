package z1223838293847182.x03849283784.p2pinternet;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import z1223838293847182.x03849283784.p2pinternet.net.WiFiDirectBroadcastReceiver;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends Activity {

	private Account acct;
	private Random rgen = new Random();
	private boolean netInitialized = false;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
	    startInternetService();
	}
	
	public void onStart()
	{
		super.onStart();
	}
	
	public void onPause()
	{
		super.onPause();
		suspendInternetService();
	}
	
	private Account selectAccount(Account[] accounts)
	{
		// TODO: Implement
		return accounts[0];
	}
	
	private String fetchName(Account acct)
	{
		// TODO: Implement
		return acct.name;
	}
	
	protected void onSaveInstanceState(Bundle b)
	{
		super.onSaveInstanceState(b);
		b.putString("accountName", acct.name);
		b.putString("accountType", acct.type);
	}
	
	private void startInternetService()
	{
		Map<String, String> record = new HashMap<String, String>();
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		acct = selectAccount(accounts);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    registerReceiver(mReceiver, mIntentFilter);
	}
	
	private void suspendInternetService()
	{
		unregisterReceiver(mReceiver);
	}
	
	public void updatePeers(WifiP2pDeviceList peers) {
		
	}
	
	public void wifiP2PEnabled(boolean enabled)
	{
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_wifi_on_)
			    .setContentTitle(enabled ? "WiFi On" : "WiFi Off")
			    .setContentText(enabled ? "P2P WiFi has been turned on." :
			    	                      "P2P WiFi has been turned off. Please check your settings.");
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
