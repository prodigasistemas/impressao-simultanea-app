package background;

import ui.ArquivoRetorno;
import util.Constantes;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GerarArquivoCompletoThread extends Thread {

	public final static int DONE = 2;
	public final static int RUNNING = 1;
	private static Context context;

	Handler handler;
	int state;
	int total;
	int increment;

	public GerarArquivoCompletoThread(Handler h, Context context, int increment) {
		this.handler = h;
		GerarArquivoCompletoThread.context = context;
		this.total = 0;
		this.increment = increment;
	}

	@Override
	public void run() {
		state = RUNNING;
		ArquivoRetorno.getInstancia().gerarArquivoRetorno(handler, context, increment, Constantes.TIPO_GERACAO_ARQUIVO_COMPLETO);
		state = DONE;

		Bundle b = new Bundle();
		Message msg = handler.obtainMessage();
		b.putBoolean("geracaoDoArquivoCompletoConcluido", true);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCustomizedState() {
		return state;
	}
}