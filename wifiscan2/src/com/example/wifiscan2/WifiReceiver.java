package com.example.wifiscan2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.wifiscan2.utils.Point;
import com.example.wifiscan2.utils.ScanState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class WifiReceiver extends BroadcastReceiver {

	public final static int ScanTimes = 10;
	private static int scanCount;
//	private static final String[] macs = { "c6:c6:c6:c6:c6:01",
//		"c6:c6:c6:c6:c6:05", "c6:c6:c6:c6:c6:06", "00:21:6a:bd:77:58",
//		"6c:6c:6c:6c:6c:09", "c6:c6:c6:c6:c6:11", "c6:c6:c6:c6:c6:13",
//		"c6:c6:c6:c6:c6:14", "c6:c6:c6:c6:c6:16", "c6:c6:c6:c6:c6:20"};
	private ArrayList<String> macs = new ArrayList<String>();
	private List<ArrayList<Integer>> signals = new ArrayList<ArrayList<Integer>>();
	private WifiManager wifiManager;
	private Point point;
	private Handler checkScanStateHandler;
	public WifiReceiver(WifiManager wifiManager,Point point,Handler checkScanStateHandler) {
		super();
		this.wifiManager = wifiManager;
		this.point = point;
		this.checkScanStateHandler = checkScanStateHandler;
		scanCount = 0;
		signals.clear();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		scanCount++;
		List<ScanResult> results = wifiManager.getScanResults();
		if (scanCount <= ScanTimes) {
			for (ScanResult result : results) {
				int index = find(result.BSSID);
				signals.get(index).add(result.level);
			}
		} else{
			//output
			String textWrite = "";
			for(int i = 0; i < macs.size(); i++){
				textWrite += macs.get(i);
				for(int j = 0; j < signals.get(i).size() ; j++){
					textWrite += " "+signals.get(i).get(j);
				}
				textWrite += "\n";
			}
			String fileName = String.format("%02d-%02d",point.x,point.y);
			File sdcardfile = Environment.getExternalStorageDirectory();//»ñÈ¡SD¿¨Ä¿Â¼
			File file = new File(sdcardfile,"/wifiscan/"+fileName+".txt");
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
			    os.write(textWrite.getBytes());
			    os.close();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			Message message = new Message();   
            message.what = ScanState.SCAN_FINISH;
            checkScanStateHandler.sendMessage(message);
		}
		wifiManager.startScan();
	}

	private int find(String target) {
		for (int i = 0; i < macs.size(); i++) {
			if (macs.get(i).equals(target)) {
				return i;
			}
		}
		macs.add(target);
		signals.add(new ArrayList<Integer>());
		return macs.size()-1;
	}

}
