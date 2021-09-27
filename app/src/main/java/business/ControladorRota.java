package business;

import static util.Constantes.REGISTRO_TIPO_ANORMALIDADE;
import static util.Constantes.REGISTRO_TIPO_CONTA;
import static util.Constantes.REGISTRO_TIPO_CREDITO;
import static util.Constantes.REGISTRO_TIPO_DADOS_CATEGORIA;
import static util.Constantes.REGISTRO_TIPO_DEBITO;
import static util.Constantes.REGISTRO_TIPO_GERAL;
import static util.Constantes.REGISTRO_TIPO_HISTORICO_CONSUMO;
import static util.Constantes.REGISTRO_TIPO_IMOVEL;
import static util.Constantes.REGISTRO_TIPO_IMPOSTO;
import static util.Constantes.REGISTRO_TIPO_MEDIDOR;
import static util.Constantes.REGISTRO_TIPO_TARIFACAO_COMPLEMENTAR;
import static util.Constantes.REGISTRO_TIPO_TARIFACAO_MINIMA;
import static util.Constantes.TABLE_CONTA;
import static util.Constantes.TABLE_DADOS_CATEGORIA;
import static util.Constantes.TABLE_HISTORICO_CONSUMO;
import static util.Constantes.TABLE_IMOVEL;
import static util.Constantes.TABLE_IMPOSTO;
import static util.Constantes.TABLE_MEDIDOR;
import static util.Constantes.TABLE_SITUACAO_TIPO;
import static util.Constantes.TABLE_TARIFACAO_COMPLEMENTAR;
import static util.Constantes.TABLE_TARIFACAO_MINIMA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Vector;

import model.DadosGerais;
import model.DadosQualidadeAgua;
import model.Imovel;
import model.SituacaoTipo;
import ui.FileManager;
import util.Constantes;
import util.LogUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.IS.Fachada;
import com.IS.ListaImoveis;

import dataBase.DataManipulator;

@SuppressLint("UseValueOf")
@SuppressWarnings({ "rawtypes", "static-access" })
public class ControladorRota {

	private static ControladorRota instancia;
	private DataManipulator data;

	private boolean permissionGranted = false;
	private int totalRegistros = 0;
	private static int totalLinhasLidas = 0;
	private static int totalImoveis = 0;
	private static int totalInformativos = 0;
	private static int isRotaCarregadaOk = Constantes.NAO;

	private static DadosGerais dadosGerais = new DadosGerais();
	private static Vector anormalidades = new Vector();

	public static ControladorRota getInstancia() {
		if (ControladorRota.instancia == null) {
			ControladorRota.instancia = new ControladorRota();
		}
		return ControladorRota.instancia;
	}

	public void cleanControladorRota() {
		instancia = null;
	}

	public void setDadosGerais(DadosGerais dadosGerais) {
		ControladorRota.dadosGerais = dadosGerais;
	}

	public void setAnormalidades(Vector anormalidades) {
		ControladorRota.anormalidades = anormalidades;
	}

	public DadosGerais getDadosGerais() {
		return ControladorRota.dadosGerais;
	}

	public Vector getAnormalidades() {
		return ControladorRota.anormalidades;
	}

	public String getBluetoothAddress() {
		return data.selectConfiguracaoElement("bluetooth_address");
	}

	public void carregar(String nomeArquivo, Handler handler, Context context) {
		try {
			BufferedReader input = FileManager.readAnyFile(nomeArquivo);

			if (input != null) {
				data.insertDadosRota(buildDadosRota(nomeArquivo));
				LogUtil.salvarLog("CARREGANDO ARQUIVO DE ROTA", nomeArquivo);

				Bundle bundle = new Bundle();
				String linha = "";
				totalLinhasLidas = 0;

				while ((linha = input.readLine()) != null) {
					if (totalLinhasLidas == 0) {
						totalRegistros = Integer.parseInt(linha);
						totalLinhasLidas++;
						continue;
					}

					totalLinhasLidas++;

					inserirRegistros(nomeArquivo, linha);

					if (totalLinhasLidas < totalRegistros) {
						sendMessage(handler, bundle);
					}
				}

				finalizarLog("FIM CARREGAMENTO ARQUIVO DE ROTA", nomeArquivo);
				validar(context);
				setRotaCarregamentoOk(Constantes.SIM);
				sendMessage(handler, bundle);
			}
		} catch (IOException e) {
			finalizarComErro(context, nomeArquivo, e);
		} catch (Exception e) {
			finalizarComErro(context, nomeArquivo, e);
		}
	}

	private void finalizarLog(String tipo, String nomeArquivo) {
		LogUtil.salvarLog(tipo, nomeArquivo + " - Total de Imoveis: " + totalImoveis);
		LogUtil.salvarLog(tipo, nomeArquivo + " - Total de Imoveis Informativos: " + totalInformativos);
		LogUtil.salvarLog(tipo, nomeArquivo + " - Total de Linhas: " + totalLinhasLidas);
	}

	private void inserirRegistros(String nomeArquivo, String linha) {
		int tipo = Integer.parseInt(linha.substring(0, 2));

		LogUtil.salvarLog("REGISTRO " + (tipo < 10 ? "0" + tipo : tipo), linha);

		switch (tipo) {

		case REGISTRO_TIPO_IMOVEL:
			boolean informativo = data.insertImovel(linha);
			data.insertSituacaoTipo(SituacaoTipo.getInstancia());
			data.insertDadosQualidadeAgua(DadosQualidadeAgua.getInstancia());

			totalImoveis++;
			if (informativo)
				totalInformativos++;

			break;

		case REGISTRO_TIPO_DADOS_CATEGORIA:
			data.insertDadosCategoria(linha);
			break;

		case REGISTRO_TIPO_HISTORICO_CONSUMO:
			data.insertHistoricoConsumo(linha);
			break;

		case REGISTRO_TIPO_DEBITO:
			data.insertDebito(linha);
			break;

		case REGISTRO_TIPO_CREDITO:
			data.insertCredito(linha);
			break;

		case REGISTRO_TIPO_IMPOSTO:
			data.insertImposto(linha);
			break;

		case REGISTRO_TIPO_CONTA:
			data.insertConta(linha);
			break;

		case REGISTRO_TIPO_MEDIDOR:
			data.insertMedidor(linha);
			break;

		case REGISTRO_TIPO_TARIFACAO_MINIMA:
			data.insertTarifacaoMinima(linha);
			break;

		case REGISTRO_TIPO_TARIFACAO_COMPLEMENTAR:
			data.insertTarifacaoComplementar(linha);
			break;

		case REGISTRO_TIPO_GERAL:
			data.insertDadosGerais(linha);
			break;

		case REGISTRO_TIPO_ANORMALIDADE:
			data.insertAnormalidade(linha);
			break;

		default:
			LogUtil.salvarLog("ERRO AO CARREGAR LINHA", linha);
			break;
		}
	}

	private void sendMessage(Handler handler, Bundle bundle) {
		Message msg = handler.obtainMessage();
		bundle.putInt("total", totalLinhasLidas);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	private void validar(Context context) {
		if (data.isRepetido(TABLE_IMOVEL, REGISTRO_TIPO_IMOVEL)
				|| data.isRepetido(TABLE_SITUACAO_TIPO, REGISTRO_TIPO_IMOVEL)
				|| data.isRepetido(TABLE_DADOS_CATEGORIA, REGISTRO_TIPO_DADOS_CATEGORIA)
				|| data.isRepetido(TABLE_HISTORICO_CONSUMO, REGISTRO_TIPO_HISTORICO_CONSUMO)
				|| data.isRepetido(TABLE_IMPOSTO, REGISTRO_TIPO_IMPOSTO)
				|| data.isRepetido(TABLE_CONTA, REGISTRO_TIPO_CONTA)
				|| data.isRepetido(TABLE_MEDIDOR, REGISTRO_TIPO_MEDIDOR)
				|| data.isRepetido(TABLE_TARIFACAO_MINIMA, REGISTRO_TIPO_TARIFACAO_MINIMA)
				|| data.isRepetido(TABLE_TARIFACAO_COMPLEMENTAR, REGISTRO_TIPO_TARIFACAO_COMPLEMENTAR)
		) {
			LogUtil.salvarLog("ERRO AO CARREGAR ROTA", "Registros duplicados");
			finalizarComErro(context);
		}
	}

	private void finalizarComErro(Context context) {
		finalizeDataManipulator();
		deleteDatabase();
		setPermissionGranted(false);
		setAlert(context);
	}

	private void setAlert(Context context) {
		Looper.prepare();

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Aviso");
		alert.setMessage("Erro ao carregar rota com registros duplicados. Tente novamente.");

		final Context c = context;

		alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent myIntent = new Intent(c, Fachada.class);
				c.startActivity(myIntent);
			}
		});

		alert.show();

		Looper.myLooper().loop();
		Looper.myLooper().quit();
	}

	private void finalizarComErro(Context context, String nomeArquivo, Exception e) {
		e.printStackTrace();
		LogUtil.salvarExceptionLog(e);
		finalizarComErro(context);
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setPermissionGranted(boolean state) {
		this.permissionGranted = state;
	}

	public boolean isPermissionGranted() {
		return this.permissionGranted;
	}

	public void initiateDataManipulator(Context context) {
		if (data == null) {
			data = new DataManipulator(context);
			data.open();
		}
	}

	public void finalizeDataManipulator() {
		if (data != null) {
			data.close();
			data = null;
		}
	}

	public DataManipulator getDataManipulator() {
		return data;
	}

	public boolean databaseExists(Context context) {
		File db = new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME);
		initiateDataManipulator(context);
		return (db.exists() && data.selectListaAnormalidades(false).size() > 0);
	}

	public boolean databaseExists() {
		File db = new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME);
		return db.exists();
	}

	public int isDatabaseRotaCarregadaOk() {
		if (new Integer(data.selectConfiguracaoElement("sucesso_carregamento")) == Constantes.SIM) {
			ControladorRota.isRotaCarregadaOk = Constantes.SIM;
		}

		return ControladorRota.isRotaCarregadaOk;
	}

	public void setRotaCarregamentoOk(int isRotaCarregadaOk) {
		data.updateConfiguracao("sucesso_carregamento", Constantes.SIM);
	}

	public void deleteDatabase() {
		String strDBFilePath = Constantes.DATABASE_PATH + Constantes.DATABASE_NAME;
		File file = new File(strDBFilePath);
		if (file.exists())
			file.delete();
	}

	public ArrayList<String> getAnormalidades(boolean apenasComIndicadorUso1) {
		ArrayList<String> listaAnormalidades = new ArrayList<String>();

		if (getDadosGerais().getCodigoEmpresaFebraban().equals(Constantes.CODIGO_FEBRABAN_COSANPA)) {
			listaAnormalidades = data.selectListaAnormalidades(apenasComIndicadorUso1);
		}

		return listaAnormalidades;
	}

	public void localizarImovelPendente() {
		ListaImoveis.tamanhoListaImoveis = data.getNumeroImoveis();

		Imovel imovelPendente = data.selectImovel("imovel_status = " + Constantes.IMOVEL_STATUS_PENDENTE, false);

		if (imovelPendente == null) {
			return;
		}

		ControladorImovel.getInstancia().setImovelSelecionadoByListPosition(Long.valueOf(imovelPendente.getId()).intValue() - 1);
	}

	private String[] buildDadosRota(String nomeArquivo) {
		return new String[] { nomeArquivo.substring(0, 4), nomeArquivo.substring(4, 7), nomeArquivo.substring(7, 10), nomeArquivo.substring(12, 14), nomeArquivo.substring(14, 20) };
	}
	
	public void resetar(Context context) {
		finalizeDataManipulator();
		deleteDatabase();
		setPermissionGranted(false);
		initiateDataManipulator(context);
	}
	
	@SuppressWarnings("resource")
	public void importarBanco(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				File diretorio = new File(sd + "/external_sd/" + Constantes.DIRETORIO_EXPORTACAO_BANCO);
				File original = new File(diretorio, Constantes.DATABASE_NAME);

				FileChannel origem = new FileInputStream(original).getChannel();
				FileChannel destino = new FileOutputStream(new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME)).getChannel();

				destino.transferFrom(origem, 0, origem.size());

				origem.close();
				destino.close();

				Toast.makeText(context, "Banco de Dados importado com sucesso.", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			LogUtil.salvarExceptionLog(e);
		}
	}
}