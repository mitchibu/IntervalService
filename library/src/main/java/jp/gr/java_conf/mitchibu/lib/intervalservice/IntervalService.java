package jp.gr.java_conf.mitchibu.lib.intervalservice;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.PowerManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class IntervalService extends IntentService {
	private PowerManager.WakeLock wl;
	private AlarmManager am;

	public IntervalService() {
		super(IntervalService.class.getSimpleName());
		setIntentRedelivery(true);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake: " + getClass().getSimpleName());
		wl.acquire();

		am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.cancel(getOperation());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		long time = getNextTime();
		if(time >= 0) {
			log(String.format("Next start is after %d milliseconds", time - System.currentTimeMillis()));
			am.set(AlarmManager.RTC_WAKEUP, time, getOperation());
		} else {
			log("Not register the following start-up");
		}

		wl.release();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		process(intent);
	}

	protected Intent getIntent() {
		return new Intent(getApplicationContext(), getClass());
	}

	protected void log(String text) {
		android.util.Log.v(getPackageName(), text);
	}

	@SuppressWarnings("unused")
	protected void log(Exception e) {
		PrintStream ps = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ps = new PrintStream(out);
			e.printStackTrace(ps);
			log(out.toString());
		} finally {
			if(ps != null) ps.close();
		}
	}

	private PendingIntent getOperation() {
		Intent intent = getIntent();
		if(intent == null) throw new RuntimeException("getIntent() is must not return null!");
		return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	protected abstract void process(Intent intent);
	protected abstract long getNextTime();
}
