package jp.gr.java_conf.mitchibu.lib.intervalservice;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class IntervalServiceWithNetwork extends IntervalService {
	private ConnectivityManager cm;
	private int[] types = null;

	@Override
	public void onCreate() {
		super.onCreate();
		cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(isAvailableNetwork()) {
			super.onHandleIntent(intent);
		} else {
			log("Skipped because the network is not a non-active");
		}
	}

	@SuppressWarnings("unused")
	protected void setNetworkTypes(int... types) {
		this.types = types.length == 0 ? null : types;
	}

	protected boolean isAvailableNetwork() {
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null || !ni.isConnected()) return false;
		if(types == null) return true;

		for(int type : types) if(ni.getType() == type) return true;
		return false;
	}
}
