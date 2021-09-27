package background;

import android.content.Context;
import android.os.Handler;
import business.ControladorRota;

public class CarregarRotaThread extends Thread {

	public final static int DONE = 0;
	public final static int RUNNING = 1;
	private static Context context;

	Handler handler;
	int state;
	int total;
	private String nomeArquivo;

	public CarregarRotaThread(Handler handler, String nomeArquivo, Context context) {
		this.handler = handler;
		CarregarRotaThread.context = context;
		this.total = 0;
		this.nomeArquivo = nomeArquivo;
	}

	@Override
	public void run() {
		state = RUNNING;
		ControladorRota.getInstancia().carregar(nomeArquivo, handler, context);
		state = DONE;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCustomizedState() {
		return state;
	}
}