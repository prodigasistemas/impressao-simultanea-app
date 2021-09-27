package com.IS;

import util.LogUtil;
import android.support.v4.app.Fragment;

public abstract class FragmentLog extends Fragment {

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("Finalizou");
	}

	@Override
	public void onPause() {
		super.onPause();
		log("Saiu de");
	}

	@Override
	public void onResume() {
		super.onResume();
		log("Entrou em");
	}

	private void log(String msg) {
		msg = msg + " " + this.getClass().getName();
		LogUtil.salvarLog("TELA", msg);
	}
}
