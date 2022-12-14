package com.IS;

import util.LogUtil;
import android.app.ListActivity;
import android.view.KeyEvent;
import android.view.Menu;

public abstract class ListActivityLog extends ListActivity {

	@Override
	protected void onPause() {
		super.onPause();
		log("Saiu de");
	}

	@Override
	protected void onResume() {
		super.onResume();
		log("Entrou em");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log("Finalizou");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LogUtil.salvarLog("BOTAO DE NAVEGACAO", "Voltar");
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}

	private void log(String msg) {
		msg += " " + this.getClass().getName();
		LogUtil.salvarLog("TELA", msg);
	}
}
