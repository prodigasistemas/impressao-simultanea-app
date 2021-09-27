package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import business.ControladorRota;

public class LogUtil {

	private static String EXTENSAO = ".log";
	private static String NOME_BACKUP = "backup.zip";
	
	public static void salvarExceptionLog(Throwable erro) {
		try {
			File arquivo = criarArquivo(getNome());
			FileWriter fw = new FileWriter(arquivo, true);
			fw.write(DateUtil.getData() + " - [ERRO]\n");
			fw.write(escreverErro(erro) + "\n");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String escreverErro(Throwable erro) {
		Writer w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		erro.printStackTrace(p);
		return w.toString();
	}

	public static void salvarLog(String conteudo) {
		String linha = DateUtil.getData() + " - " + conteudo;
		gerarLog(linha);
	}

	public static void salvarLog(String tipo, String conteudo) {
		String linha = DateUtil.getData() + " - [" + tipo + "] - " + conteudo;
		gerarLog(linha);
	}

	public static File getDiretorio() {
		return new File(Util.getExternalStorageDirectory() + Constantes.DIRETORIO_LOGS);
	}

	private static void gerarLog(String linha) {
		try {
			File arquivo = criarArquivo(getNome());
			BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true));
			bw.append(linha);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			salvarExceptionLog(e);
		} catch (Exception e) {
			e.printStackTrace();
			salvarExceptionLog(e);
		}
	}

	public static String getNome() {
		List<String> dados = getDadosRota();

		String nome = "";
		if (dados != null && dados.size() > 0) {
			nome += dados.get(0);
			nome += dados.get(1);
			nome += dados.get(2);
			nome += dados.get(3);
			nome += dados.get(4);
		} else {
			nome += DateUtil.getData("yyyy_MM_dd");
		}

		return nome + EXTENSAO;
	}

	private static List<String> getDadosRota() {
		ControladorRota controlador = ControladorRota.getInstancia();
		boolean existeBanco = controlador.databaseExists();

		List<String> dados = null;
		if (existeBanco) {
			dados = controlador.getDataManipulator().selectDadosRota();
		}
		return dados;
	}

	private static File criarArquivo(String nome) throws IOException {
		File diretorio = getDiretorio();
		if (!diretorio.exists()) {
			diretorio.mkdir();
		}

		File arquivo = new File(diretorio, nome);
		if (!arquivo.exists()) {
			arquivo.createNewFile();
		}
		return arquivo;
	}
	
	public static void backup() {
		ZipUtil.criar(new File(getDiretorio(), NOME_BACKUP), EXTENSAO);
	}
}
