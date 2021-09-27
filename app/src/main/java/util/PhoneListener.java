package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class PhoneListener extends BroadcastReceiver {
	public PhoneListener() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		loadParams(intent);
	}

	private void loadParams(Intent intent) {
		Bundle extras = intent.getExtras();
		String action = intent.getAction();

		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);

			if (state != null) {
				if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
					log("Recebeu Ligação de >> " + phoneNumber);
				}
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				log("Power Pressionado");
			} else if (intent.getAction().intern().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
				log("Modo Aviao Alterado");
			} else if (action.equals(Intent.ACTION_SHUTDOWN)) {
				log("Desligando Dispositivo");
			} else if (action.equals(Intent.ACTION_CAMERA_BUTTON)) {
				log("Camera Acessada");
			} else if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
				log("Botoes de Media Acessados");
			} else if (action.equals(Intent.ACTION_APP_ERROR)) {
				log("Erro na Aplicacao");
			}
		}
	}

	private void log(String msg) {
		LogUtil.salvarLog("[RECEIVER]", msg);
	}
}
