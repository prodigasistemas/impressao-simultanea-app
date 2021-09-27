package util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	private static ZipOutputStream zipOut;
	private static ZipEntry entry;
	private static byte[] buffer = new byte[1024];
	private static File tmp;

	public static void criarRetorno(File retorno) {
		try {
			inicializar(retorno.getAbsolutePath().replace(".txt", ".zip"));
			compactar(retorno);
			compactar(new File(LogUtil.getDiretorio(), LogUtil.getNome()), false);
			compactar(new File(LogUtil.getDiretorio(), DateUtil.getData("yyyy_MM_dd") + ".log"), false);
			finalizar();
			retorno.delete();
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}
	}

	public static void criar(File arquivoZip, String extensao) {
		try {
			descompactar(arquivoZip);
			inicializar(arquivoZip.getAbsolutePath());

			File[] arquivos = listar(arquivoZip.getParentFile());
			if (isTmpExistente()) {
				regerar(extensao, arquivos);
			} else {
				gerar(arquivos);
			}

			finalizar();
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}
	}

	private static void gerar(File[] arquivos) throws IOException {
		for (File arquivo : arquivos) {
			compactar(arquivo);
		}
	}

	private static void regerar(String extensao, File[] arquivos) throws IOException {
		File[] arquivosTmp = listar(tmp);
		for (File arquivo : arquivos) {
			String nome = arquivo.getName().replace(extensao, "");
			int quantidade = verificarRepeticoes(arquivosTmp, nome);
			
			if (quantidade > 0) {
				File arquivoRenomeado = renomear(extensao, arquivo, nome, quantidade);
				compactar(arquivoRenomeado);
			} else {
				compactar(arquivo);
			}
		}
	}

	private static File renomear(String extensao, File arquivo, String nome, int quantidade) throws IOException {
		File arquivoRenomeado = new File(arquivo.getParent(), nome + "-" + quantidade + extensao);
		arquivo.renameTo(arquivoRenomeado);
		return arquivoRenomeado;
	}

	private static int verificarRepeticoes(File[] arquivosTmp, String nome) throws IOException {
		int quantidade = 0;
		for (File arquivoTmp : arquivosTmp) {
			if (arquivoTmp.getName().contains(nome)) {
				quantidade++;
			}
			compactar(arquivoTmp);
		}
		return quantidade;
	}

	private static File[] listar(File diretorio) {
		return diretorio.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.getName().endsWith(".zip") && !pathname.isDirectory();
			}
		});
	}

	private static void inicializar(String caminho) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(caminho);
		zipOut = new ZipOutputStream(out);
	}

	private static void finalizar() throws IOException {
		zipOut.closeEntry();
		zipOut.close();
		
		if (isTmpExistente())
			tmp.delete();
	}

	private static boolean isTmpExistente() {
		return tmp != null && tmp.exists();
	}

	private static void compactar(File arquivo, boolean apagar) throws IOException {
		if (arquivo.exists()) {
			entry = new ZipEntry(arquivo.getName());
			zipOut.putNextEntry(entry);
			FileInputStream in = new FileInputStream(arquivo);

			int len;
			while ((len = in.read(buffer)) > 0)
				zipOut.write(buffer, 0, len);
			
			in.close();
			
			if (apagar)
				arquivo.delete();
		}
	}
	
	private static void compactar(File arquivo) throws IOException {
		compactar(arquivo, true);
	}
	
	private static void descompactar(File arquivo) {
		try {
			if (arquivo.exists()) {
				criarTmp(arquivo.getParent());
				ZipInputStream zipIn = new ZipInputStream(new FileInputStream(arquivo.getAbsolutePath()));
				entry = zipIn.getNextEntry();
				while (entry != null) {
					extrair(zipIn);
					entry = zipIn.getNextEntry();
				}
				
				zipIn.closeEntry();
				zipIn.close();
				arquivo.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}
	}

	private static void criarTmp(String diretorio) {
		tmp = new File(diretorio, "tmp");
		if (!tmp.exists())
			tmp.mkdir();
	}

	private static void extrair(ZipInputStream zipIn) throws FileNotFoundException, IOException {
		File arquivo = new File(tmp, entry.getName());
		FileOutputStream out = new FileOutputStream(arquivo);
		
		int len;
		while ((len = zipIn.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		
		out.close();
	}
}
