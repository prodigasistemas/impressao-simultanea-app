package background;

import util.LogUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import business.ControladorRota;

public class ApagarRotaThread extends Thread {

	private static Context context;
	
	private Handler handler;

	public ApagarRotaThread(Handler handler, Context context) {
		this.handler = handler;
		ApagarRotaThread.context = context;
	}

	@Override
	public void run() {
		LogUtil.backup();
		ControladorRota.getInstancia().resetar(context);

		Bundle b = new Bundle();
		Message msg = handler.obtainMessage();
		b.putBoolean("rotaApagada", true);
		msg.setData(b);
		handler.sendMessage(msg);
	}
}
