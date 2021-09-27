package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import model.Configuracao;
import model.Consumo;
import model.DadosCategoria;
import model.DadosFaturamento;
import model.DadosFaturamentoFaixa;
import model.DadosGerais;
import model.Imovel;
import model.Imposto;
import model.Medidor;
import util.Constantes;
import util.LogUtil;
import util.Util;
import util.ZipUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import business.ControladorImovel;
import business.ControladorRota;

import com.IS.Fachada;

public class ArquivoRetorno {

	private static ArquivoRetorno instancia;
	private static StringBuffer arquivo;

	private StringBuffer tipo0 = null;
	private StringBuffer tipo1 = null;
	private StringBuffer tipo2e3 = null;
	private StringBuffer tipo4 = null;

	private static final short ARQUIVO_COMPLETO = 0;
	private static final short ARQUIVO_CONCLUIDOS_ATE_AGORA = 1;
	private static final short ARQUIVO_INCOMPLETO = 2;
	private static final short ARQUIVO_TODOS_OS_CALCULADOS = 3;

	private ArquivoRetorno() {
		super();
	}

	public static ArquivoRetorno getInstancia() {
		if (instancia == null) {
			instancia = new ArquivoRetorno();
		}
		return instancia;
	}

	public StringBuffer gerarDadosImovelParaEnvio(Imovel i) {
		arquivo = new StringBuffer();

		Imovel imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + i.getId(), true);

		gerarRegistroTipo1(imovel);
		gerarRegistroTipo2e3(imovel);
		gerarRegistroTipo4(imovel);

		return arquivo;
	}

	public void gerarDadosImoveisCondominioParaEnvio(List<Integer> listIds) {
		arquivo = new StringBuffer();

		for (int id : listIds) {
			Imovel imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + id, true);

			gerarRegistroTipo1(imovel);
			gerarRegistroTipo2e3(imovel);
			gerarRegistroTipo4(imovel);
		}
	}

	public void gerarArquivoRetorno(Handler handler, Context context, int increment, int tipoGeracao) {
		try {
			File retorno = null;
			if (tipoGeracao == Constantes.TIPO_GERACAO_ARQUIVO_COMPLETO) {
				retorno = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName());
			} else {
				retorno = new File(Util.getRetornoRotaDirectory(), Util.getNomeArquivoEnviarConcluidos());
			}

			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "Erro ao salvar no cartão de memória!", Toast.LENGTH_SHORT).show();
			}

			FileOutputStream os = new FileOutputStream(retorno);
			OutputStreamWriter out = new OutputStreamWriter(os);

			arquivo = new StringBuffer();

			@SuppressWarnings("rawtypes")
			List listIdImoveis = null;
			if (tipoGeracao == Constantes.TIPO_GERACAO_ARQUIVO_COMPLETO) {
				listIdImoveis = ControladorRota.getInstancia().getDataManipulator().selectIdImoveis(null);
			} else {
				listIdImoveis = ControladorRota.getInstancia().getDataManipulator().selectIdsImoveisConcluidosENaoEnviados();
			}

			gerarRegistroTipo0(Constantes.INDC_FINALIZAR_ROTEIRO_TODOS_IMOVEIS);
			arquivo.append(tipo0);

			for (int i = 0; i < listIdImoveis.size(); i++) {
				Imovel imovel = null;
				if (tipoGeracao == Constantes.TIPO_GERACAO_ARQUIVO_COMPLETO) {
					imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + (i + 1), true);
				} else {
					imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + listIdImoveis.get(i), true);
				}

				if (imovel.isImovelInformativo())
					continue;

				gerarRegistroTipo1(imovel);
				gerarRegistroTipo2e3(imovel);
				gerarRegistroTipo4(imovel);

				Bundle b = new Bundle();
				Message msg = handler.obtainMessage();
				b.putInt("arquivoCompleto" + String.valueOf(increment), (i + 1));
				msg.setData(b);
				handler.sendMessage(msg);
			}

			out.write(arquivo.toString());
			out.close();
			
			ZipUtil.criarRetorno(retorno);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}
	}

	public StringBuffer gerarDadosFinalizacaoRotaOnline(Handler handler, Context context, int increment) {
		arquivo = new StringBuffer();

		String filterCondition = "(imovel_enviado = " + Constantes.NAO + ")";

		ArrayList<String> listIdImoveis = (ArrayList<String>) ControladorRota.getInstancia().getDataManipulator().selectIdImoveis(filterCondition);

		for (int i = 0; i < listIdImoveis.size(); i++) {

			Bundle b = new Bundle();
			Message msg = handler.obtainMessage();
			b.putInt("finalizacao" + String.valueOf(increment), (i + 1));
			msg.setData(b);
			handler.sendMessage(msg);
		}

		return arquivo;
	}

	private void gerarRegistroTipo1(Imovel imovel) {
		tipo1 = new StringBuffer();

		String anormalidadeLeitura = "";
		Date dataLeitura = new Date();

		String anoMesFaturamento = ControladorRota.getInstancia().getDataManipulator().selectInformacoesRota().get(4);

		Medidor medidorAgua = imovel.getMedidor(Constantes.LIGACAO_AGUA);
		Medidor medidorPoco = imovel.getMedidor(Constantes.LIGACAO_POCO);

		Consumo consumoAgua = imovel.getConsumoAgua();
		Consumo consumoEsgoto = imovel.getConsumoEsgoto();
		boolean temAgua = false;
		boolean temEsgoto = false;

		if ((consumoAgua != null || (medidorAgua != null && !medidorAgua.equals(""))) && (consumoAgua != null && consumoAgua.getAnormalidadeConsumo() != 0)) {

			temAgua = true;

			String indicadorSituacao = "" + Constantes.LEITURA_REALIZADA;

			if (medidorAgua != null && medidorAgua.getLeitura() >= medidorAgua.getLeituraEsperadaInicial() && medidorAgua.getLeitura() <= medidorAgua.getLeituraEsperadaFinal()) {
				indicadorSituacao = "" + Constantes.LEITURA_REALIZADA;
			} else {
				if (medidorAgua != null) {
					indicadorSituacao = "" + Constantes.LEITURA_CONFIRMADA;
				}
			}

			if (medidorAgua == null && medidorPoco == null && imovel.getDataImpressaoNaoMedido() != null) {
				dataLeitura = imovel.getDataImpressaoNaoMedido();

			} else if (medidorAgua != null && !medidorAgua.equals("")) {
				anormalidadeLeitura = "" + medidorAgua.getAnormalidade();
				dataLeitura = medidorAgua.getDataLeitura();

			} else if (medidorPoco != null && !medidorPoco.equals("") && medidorPoco.getDataLeitura() != null) {
				dataLeitura = medidorPoco.getDataLeitura();
			}

			// Tipo de Registro
			tipo1.append("1");
			// Matricula
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
			// Tipo de Medição
			tipo1.append("1");
			// Ano/Mês do faturamento
			tipo1.append(Util.formatarAnoMesParaMesAnoSemBarra(anoMesFaturamento));
			// Número da Conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getNumeroConta() + ""));
			// Grupo de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(3, imovel.getGrupoFaturamento() + ""));
			// Código da rota
			tipo1.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getCodigoRota() + ""));
			// Leitura do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7, (imovel.getMedidor(Constantes.LIGACAO_AGUA) != null ? imovel.getMedidor(Constantes.LIGACAO_AGUA).getLeitura() + "" : ""), ' '));
			// Anormalidade de Leitura
			tipo1.append(Util.adicionarCharEsquerda(2, anormalidadeLeitura.equals(Constantes.NULO_INT + "") ? "0" : anormalidadeLeitura, ' '));
			// Data e hora da leitura
			tipo1.append(Util.adicionarCharEsquerda(26, (Util.formatarData(dataLeitura)), ' '));
			// Indicador de situação da leitura
			tipo1.append(indicadorSituacao);
			// Leitura de faturamento
			tipo1.append(Util.adicionarCharEsquerda(7, consumoAgua != null ? consumoAgua.getLeituraAtual() + "" : "", ' '));
			// Consumo Medido no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, consumoAgua != null ? consumoAgua.getConsumoMedidoMes() + "" : "", ' '));
			// Consumo Cobrado no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, consumoAgua != null ? consumoAgua.getConsumoCobradoMes() + "" : "", ' '));
			// Consumo Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, imovel.getConsumoRateioAgua() > 0 ? imovel.getConsumoRateioAgua() + "" : ""));
			// Valor Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, imovel.getValorRateioAgua() > 0 ? Util.formatarDoubleParaMoedaReal(imovel.getValorRateioAgua()) + "" : ""));
			// Consumo Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, imovel.getConsumoRateioEsgoto() > 0 ? imovel.getConsumoRateioEsgoto() + "" : ""));
			// Valor Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, imovel.getValorRateioEsgoto() > 0 ? Util.formatarDoubleParaMoedaReal(imovel.getValorRateioEsgoto()) + "" : ""));
			// Tipo de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, consumoAgua != null ? consumoAgua.getTipoConsumo() + "" : "", ' '));
			// Anormalidade de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, consumoAgua != null ? consumoAgua.getAnormalidadeConsumo() + "" : "", ' '));
			// Indicador de emissao de conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(1, imovel.getIndcImovelImpresso() + ""));
			// Inscricao do Imovel
			tipo1.append(imovel.getInscricao());
			// Indicador Conta Retida
			tipo1.append(imovel.getIndcGeracaoConta());
			// Consumo Imóveis MICRO Sem Rateio
			tipo1.append(Util.adicionarCharEsquerda(6, consumoAgua != null ? consumoAgua.getConsumoCobradoMesImoveisMicro() + "" : "", ' '));
			// Anormalidade de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(2, consumoAgua != null ? consumoAgua.getAnormalidadeLeituraFaturada() + "" : ""));
			// ID do documento de cobrança
			tipo1.append(Util.adicionarCharEsquerda(9, imovel.getNumeroDocumentoNotificacaoDebito(), ' '));
			// Leitura Anterior do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7, (medidorAgua != null ? medidorAgua.getLeituraAnterior() + "" : ""), ' '));
			// Latitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLatitude() != Constantes.NULO_DOUBLE ? imovel.getLatitude() + "" : ""));
			// Longitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLongitude() != Constantes.NULO_DOUBLE ? imovel.getLongitude() + "" : ""));
			// Versao do I.S. em uso
			tipo1.append(Util.adicionarCharEsquerda(12, Fachada.getAppVersion(), ' '));
			tipo1.append("\n");
		}

		if ((consumoEsgoto != null || (medidorPoco != null && !medidorPoco.equals(""))) && (consumoEsgoto != null && consumoEsgoto.getAnormalidadeConsumo() != 0)) {
			temEsgoto = true;

			String indicadorSituacao = "" + Constantes.LEITURA_REALIZADA;

			if (medidorPoco != null && medidorPoco.getLeitura() >= medidorPoco.getLeituraEsperadaInicial() && medidorPoco.getLeitura() <= medidorPoco.getLeituraEsperadaFinal()) {
				indicadorSituacao = "" + Constantes.LEITURA_REALIZADA;
			} else {
				if (medidorPoco != null) {
					indicadorSituacao = "" + Constantes.LEITURA_CONFIRMADA;
				}
			}

			if (medidorPoco != null && !medidorPoco.equals("")) {
				anormalidadeLeitura = "" + medidorPoco.getAnormalidade();
				dataLeitura = medidorPoco.getDataLeitura();
			} else {
				if (imovel.getAnormalidadeSemHidrometro() != Constantes.NULO_INT) {
					anormalidadeLeitura = "" + imovel.getAnormalidadeSemHidrometro();
				}
				if (medidorAgua != null && !medidorAgua.equals("") && medidorAgua.getDataLeitura() != null) {
					dataLeitura = medidorAgua.getDataLeitura();
				}
			}

			// Tipo de Registro
			tipo1.append("1");
			// Matricula
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
			// Tipo de Medição
			tipo1.append("2");
			// Ano/Mês do faturamento
			tipo1.append(Util.formatarAnoMesParaMesAnoSemBarra(anoMesFaturamento));
			// Número da Conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getNumeroConta() + ""));
			// Grupo de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(3, imovel.getGrupoFaturamento() + ""));
			// Código da rota
			tipo1.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getCodigoRota() + ""));
			// Leitura do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7, (medidorPoco != null ? medidorPoco.getLeitura() + "" : ""), ' '));
			// Anormalidade de Leitura
			tipo1.append(Util.adicionarCharEsquerda(2, anormalidadeLeitura.equals(Constantes.NULO_INT + "") ? "0" : anormalidadeLeitura, ' '));
			// Data e hora da leitura
			tipo1.append(Util.adicionarCharEsquerda(26, (Util.formatarData(dataLeitura)), ' '));
			// Indicador de situação da leitura
			tipo1.append(indicadorSituacao);
			// Leitura de faturamento
			tipo1.append(Util.adicionarCharEsquerda(7, consumoEsgoto != null ? consumoEsgoto.getLeituraAtual() + "" : "", ' '));
			// Consumo Medido no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, consumoEsgoto != null ? consumoEsgoto.getConsumoMedidoMes() + "" : "", ' '));
			// Consumo Cobrado no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, consumoEsgoto != null ? consumoEsgoto.getConsumoCobradoMes() + "" : "", ' '));
			// Consumo Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, imovel.getConsumoRateioAgua() > 0 ? imovel.getConsumoRateioAgua() + "" : ""));
			// Valor Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, imovel.getValorRateioAgua() > 0 ? Util.formatarDoubleParaMoedaReal(imovel.getValorRateioAgua()) + "" : ""));
			// Consumo Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, imovel.getConsumoRateioEsgoto() > 0 ? imovel.getConsumoRateioEsgoto() + "" : ""));
			// Valor Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, imovel.getValorRateioEsgoto() > 0 ? Util.formatarDoubleParaMoedaReal(imovel.getValorRateioEsgoto()) + "" : ""));
			// Tipo de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, consumoEsgoto != null ? consumoEsgoto.getTipoConsumo() + "" : "", ' '));
			// Anormalidade de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, consumoEsgoto != null ? consumoEsgoto.getAnormalidadeConsumo() + "" : "", ' '));
			// Indicador de emissao de conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(1, imovel.getIndcImovelImpresso() + ""));
			// Inscricao do Imovel
			tipo1.append(imovel.getInscricao());
			// Indicador Conta Retida
			tipo1.append(imovel.getIndcGeracaoConta());
			// Consumo Imóveis MICRO Sem Rateio
			tipo1.append(Util.adicionarCharEsquerda(6, consumoEsgoto != null ? consumoEsgoto.getConsumoCobradoMesImoveisMicro() + "" : "", ' '));
			// Anormalidade de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(2, consumoEsgoto != null ? consumoEsgoto.getAnormalidadeLeituraFaturada() + "" : ""));
			// ID do documento de cobrança
			tipo1.append(Util.adicionarCharEsquerda(9, imovel.getNumeroDocumentoNotificacaoDebito(), ' '));
			// Leitura Anterior do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7, (medidorPoco != null ? medidorPoco.getLeituraAnterior() + "" : ""), ' '));
			// Latitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLatitude() != Constantes.NULO_DOUBLE ? imovel.getLatitude() + "" : ""));
			// Longitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLongitude() != Constantes.NULO_DOUBLE ? imovel.getLongitude() + "" : ""));
			// Versao do I.S. em uso
			tipo1.append(Util.adicionarCharEsquerda(12, Fachada.getAppVersion(), ' '));
			tipo1.append("\n");

			System.out.println(tipo1);
		}

		// Caso nao tenha consumo nem de agua nem de esgoto
		// geramos um registro tipo 1 apenas com os débitos.
		if (!temAgua && !temEsgoto) {
			// Tipo de Registro
			tipo1.append("1");
			// Matricula
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
			// Tipo de Medição
			tipo1.append("1");
			// Ano/Mês do faturamento
			tipo1.append(Util.formatarAnoMesParaMesAnoSemBarra(anoMesFaturamento));
			// Número da Conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getNumeroConta() + ""));
			// Grupo de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(3, imovel.getGrupoFaturamento() + ""));
			// Código da rota
			tipo1.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getCodigoRota() + ""));
			// Leitura do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7, (medidorAgua != null ? medidorAgua.getLeitura() + "" : ""), ' '));
			// Anormalidade de Leitura
			tipo1.append(Util.adicionarCharEsquerda(2, (imovel.getAnormalidadeSemHidrometro() != Constantes.NULO_INT ? imovel.getAnormalidadeSemHidrometro() + ""
					: (medidorAgua != null ? (medidorAgua.getAnormalidade() == Constantes.NULO_INT ? "0" : medidorAgua.getAnormalidade()) + "" : "")), ' '));
			// Data e hora da leitura
			tipo1.append(Util.adicionarCharEsquerda(26, Util.formatarData(new Date()), ' '));
			// Indicador de situação da leitura
			tipo1.append(" ");
			// Leitura de faturamento
			tipo1.append(Util.adicionarCharEsquerda(7, (medidorAgua != null ? medidorAgua.getLeitura() + "" : ""), ' '));
			// Consumo Medido no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, " ", ' '));
			// Consumo Cobrado no Mes
			tipo1.append(Util.adicionarCharEsquerda(6, " ", ' '));
			// Consumo Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, "0"));
			// Valor Rateio Agua
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, "0"));
			// Consumo Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(6, "0"));
			// Valor Rateio Esgoto
			tipo1.append(Util.adicionarZerosEsquerdaNumero(8, "0"));
			// Tipo de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, " ", ' '));
			// Anormalidade de Consumo
			tipo1.append(Util.adicionarCharEsquerda(2, " ", ' '));
			// Indicador de emissao de conta
			tipo1.append(Util.adicionarZerosEsquerdaNumero(1, imovel.getIndcImovelImpresso() + ""));
			// Inscricao do Imovel
			tipo1.append(imovel.getInscricao());
			// Indicador Conta Retida
			tipo1.append(imovel.getIndcGeracaoConta());
			// Consumo Imóveis MICRO Sem Rateio
			tipo1.append(Util.adicionarCharEsquerda(6, "", ' '));
			// Anormalidade de faturamento
			tipo1.append(Util.adicionarZerosEsquerdaNumero(2, ""));
			// ID do documento de cobrança
			tipo1.append(Util.adicionarCharEsquerda(9, imovel.getNumeroDocumentoNotificacaoDebito(), ' '));
			// Leitura Anterior do Hidrômetro
			tipo1.append(Util.adicionarCharEsquerda(7,
					(medidorAgua != null ? (medidorAgua.getLeituraAnterior() == Constantes.NULO_INT ? "0" : medidorAgua.getLeituraAnterior()) + "" : ""), ' '));
			// Latitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLatitude() != Constantes.NULO_DOUBLE ? imovel.getLatitude() + "" : ""));
			// Longitude
			tipo1.append(Util.adicionarZerosDireitaNumero(20, imovel.getLongitude() != Constantes.NULO_DOUBLE ? imovel.getLongitude() + "" : ""));
			// Versao do I.S. em uso
			tipo1.append(Util.adicionarCharEsquerda(12, Fachada.getAppVersion(), ' '));
			tipo1.append("\n");
		}

		arquivo.append(tipo1);
	}

	private void gerarRegistroTipo2e3(Imovel imovel) {
		tipo2e3 = new StringBuffer();

		List<DadosCategoria> registrosTipoDadosCategoria = imovel.getDadosCategoria();

		if (registrosTipoDadosCategoria != null) {

			for (int i = 0; i < registrosTipoDadosCategoria.size(); i++) {

				DadosCategoria dadosCategoria = (DadosCategoria) registrosTipoDadosCategoria.get(i);

				DadosFaturamento faturamentoAgua = new DadosFaturamento();

				if (dadosCategoria.getFaturamentoAgua() != null) {
					faturamentoAgua = dadosCategoria.getFaturamentoAgua();
				}

				DadosFaturamento faturamentoEsgoto = new DadosFaturamento();

				if (dadosCategoria.getFaturamentoEsgoto() != null) {
					faturamentoEsgoto = dadosCategoria.getFaturamentoEsgoto();
				}

				// Tipo de registro (1)
				tipo2e3.append("2");
				// Matrícula (9);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
				// Código da categoria (1)
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(1, dadosCategoria.getCodigoCategoria() + ""));
				// Código da subcategoria (3)
				short indicadorTarifa = ControladorRota.getInstancia().getDadosGerais().getIndcTarifaCatgoria();
				String codigoSubcategoria = "";
				if (indicadorTarifa == DadosGerais.CALCULO_POR_CATEGORA) {
					codigoSubcategoria = Util.adicionarCharEsquerda(3, "0", ' ');
				} else {
					codigoSubcategoria = Util.adicionarCharEsquerda(3, dadosCategoria.getCodigoSubcategoria(), ' ');
				}

				tipo2e3.append(codigoSubcategoria);
				// Valor faturado de água na categoria (13,2)
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faturamentoAgua.getValorFaturado())));
				// Consumo faturado de água na categoria (6);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faturamentoAgua.getConsumoFaturado() + ""));
				// Valor da tarifa mínima de água da categoria (13,2);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faturamentoAgua.getValorTarifaMinima())));
				// Consumo mínimo de água da categoria (6);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faturamentoAgua.getConsumoMinimo() + ""));
				// Valor faturado de esgoto na categoria (13,2)
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faturamentoEsgoto.getValorFaturado())));
				// Consumo faturado de esgoto na categoria (6);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faturamentoEsgoto.getConsumoFaturado() + ""));
				// Valor da tarifa mínima de esgoto da categoria (13,2);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faturamentoEsgoto.getValorTarifaMinima())));
				// Consumo mínimo de esgoto da categoria (6);
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faturamentoEsgoto.getConsumoMinimo() + ""));
				// Numero de vezes que a conta foi impressa
				tipo2e3.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(imovel.getQuantidadeContasImpressas())));
				tipo2e3.append("\n");

				List faixasAgua = faturamentoAgua.getFaixas();
				List faixasEsgoto = faturamentoEsgoto.getFaixas();

				if (faixasAgua != null) {
					for (int j = 0; j < faixasAgua.size(); j++) {
						DadosFaturamentoFaixa faixaAgua = (DadosFaturamentoFaixa) faixasAgua.get(j);

						DadosFaturamentoFaixa faixaEsgoto = new DadosFaturamentoFaixa();

						if (faixasEsgoto != null && !faixasEsgoto.isEmpty()) {

							for (int k = 0; k < faixasEsgoto.size(); k++) {

								faixaEsgoto = (DadosFaturamentoFaixa) faixasEsgoto.get(k);

								if (faixaAgua.equals(faixaEsgoto)) {
									break;
								}
							}
						}

						// Tipo de registro (1)
						tipo2e3.append("3");
						// Matrícula (9);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
						// Código da categoria (1)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(1, dadosCategoria.getCodigoCategoria() + ""));
						// Código da subcategoria (3)
						tipo2e3.append(Util.adicionarCharEsquerda(3, dadosCategoria.getCodigoSubcategoria(), ' '));
						// Consumo faturado de água na faixa (6)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaAgua.getConsumoFaturado() + ""));
						// Valor faturado de água na faixa (13,2)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaAgua.getValorFaturado())));
						// Limite inicial de consumo na faixa (6);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaAgua.getLimiteInicialConsumo() + ""));
						// Limite final de consumo na faixa (6);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaAgua.getLimiteFinalConsumo() + ""));
						// Valor da tarifa de água na faixa (13,2);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaAgua.getValorTarifa())));
						// Valor da tarifa de esgoto na faixa (13,2);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaEsgoto.getValorTarifa())));
						// Consumo Faturado de esgoto na faixa (6)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaEsgoto.getConsumoFaturado() + ""));
						// Valor faturado de esgoto na faixa (13,2)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaEsgoto.getValorFaturado())));
						tipo2e3.append("\n");
					}
				} else if (faixasEsgoto != null) {

					DadosFaturamentoFaixa faixaEsgoto = new DadosFaturamentoFaixa();

					for (int k = 0; k < faixasEsgoto.size(); k++) {

						faixaEsgoto = (DadosFaturamentoFaixa) faixasEsgoto.get(k);

						// Tipo de registro (1)
						tipo2e3.append("3");
						// Matrícula (9);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
						// Código da categoria (1)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(1, dadosCategoria.getCodigoCategoria() + ""));
						// Código da subcategoria (3)
						tipo2e3.append(Util.adicionarCharEsquerda(3, dadosCategoria.getCodigoSubcategoria(), ' '));
						// Consumo faturado de água na faixa (6)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, ""));
						// Valor faturado de água na faixa (13,2)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, ""));
						// Limite inicial de consumo na faixa (6);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaEsgoto.getLimiteInicialConsumo() + ""));
						// Limite final de consumo na faixa (6);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaEsgoto.getLimiteFinalConsumo() + ""));
						// Valor da tarifa de água na faixa (13,2);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, ""));
						// Valor da tarifa de esgoto na faixa (13,2);
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaEsgoto.getValorTarifa())));
						// Consumo Faturado de esgoto na faixa (6)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(6, faixaEsgoto.getConsumoFaturado() + ""));
						// Valor faturado de esgoto na faixa (13,2)
						tipo2e3.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(faixaEsgoto.getValorFaturado())));
						tipo2e3.append("\n");
					}
				}
			}
		}

		arquivo.append(tipo2e3);

	}

	private void gerarRegistroTipo4(Imovel imovel) {

		tipo4 = new StringBuffer();

		List<Imposto> impostos = imovel.getImpostos();

		if (impostos != null) {
			for (int i = 0; i < impostos.size(); i++) {
				Imposto reg6 = impostos.get(i);

				// Tipo de registro (1)
				tipo4.append("4");
				// Matricula (9)
				tipo4.append(Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + ""));
				// Tipo de Imposto(1)
				tipo4.append(Util.adicionarZerosEsquerdaNumero(1, reg6.getTipoImposto() + ""));
				// Descrição do imposto (15);
				tipo4.append(Util.adicionarCharEsquerda(15, reg6.getDescricaoImposto(), ' '));
				// Percentual da Alíquota (6);
				tipo4.append(Util.adicionarZerosEsquerdaNumero(6, Util.formatarDoubleParaMoedaReal(reg6.getPercentualAlicota())));
				// Valor do Imposto (13,2)
				tipo4.append(Util.adicionarZerosEsquerdaNumero(16, Util.formatarDoubleParaMoedaReal(imovel.getValorContaSemImposto() * Util.arredondar((reg6.getPercentualAlicota() / 100), 7))));
				tipo4.append("\n");
			}
		}

		arquivo.append(tipo4);
	}

	private void gerarRegistroTipo0(int tipoFinalizacao) {
		tipo0 = new StringBuffer();
		List<String> infoRota = ControladorRota.getInstancia().getDataManipulator().selectInformacoesRota();

		// Tipo de registro (1)
		tipo0.append("0");
		// Tipo de finalização (1)
		tipo0.append(tipoFinalizacao + "");
		// Localidade(3)
		tipo0.append(Util.adicionarZerosEsquerdaNumero(3, infoRota.get(1) + ""));
		// Setor Comercial(3)
		tipo0.append(Util.adicionarZerosEsquerdaNumero(3, infoRota.get(2) + ""));
		// Rota (3);
		tipo0.append(Util.adicionarZerosEsquerdaNumero(7, infoRota.get(3) + ""));
		// Id da Rota
		if (ControladorRota.getInstancia().getDadosGerais().getIdRota() != 9999) {
			tipo0.append(Util.adicionarZerosEsquerdaNumero(4, ControladorRota.getInstancia().getDadosGerais().getIdRota() + ""));
		}
		// Indicador de rota dividida
		if (ControladorRota.getInstancia().getDadosGerais().getIndicadorRotaDividida() != 999999) {
			tipo0.append(Util.adicionarZerosEsquerdaNumero(2, ControladorRota.getInstancia().getDadosGerais().getIndicadorRotaDividida() + ""));
		} else {
			tipo0.append(Util.adicionarZerosEsquerdaNumero(2, "00"));
		}

		tipo0.append("\n");
	}

	/**
	 * Métodos gera o arquivo de retorno de todos os imóveis não enviados até o momento;
	 * 
	 * @param p Progress bar que irá mostrar o progresso do processo
	 * @return Vetor de objetos com dois objetos 1 - boolean com indicação de
	 *         erro na geração 2 - todas os id's dos imóveis que foram gerados
	 *         para envio
	 */
	public Object[] gerarArquivoRetorno(short tipoArquivoRetorno) {
		File fileArquivoCompleto = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName());

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(fileArquivoCompleto);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}
		OutputStreamWriter out = new OutputStreamWriter(os);

		Object[] retorno = new Object[2];
		Vector idsImoveisGerados = new Vector();

		arquivo = new StringBuffer();

		List<Integer> idsImoveisConcluidos = ControladorRota.getInstancia().getDataManipulator().selectIdsImoveisConcluidosENaoEnviados();
		int qtdImoveisCalculados = 0;

		// Sequencial de rota de marcacão
		StringBuffer sequencialRotaMarcacao = new StringBuffer();

		if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_COMPLETO || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS) {
			qtdImoveisCalculados = Configuracao.getInstancia().getQtdImoveis();
		} else if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_CONCLUIDOS_ATE_AGORA || tipoArquivoRetorno == ARQUIVO_INCOMPLETO) {
			qtdImoveisCalculados = idsImoveisConcluidos.size();

			if (tipoArquivoRetorno == ARQUIVO_COMPLETO) {
				this.gerarRegistroTipo0(Constantes.INDC_FINALIZAR_ROTEIRO);
			} else if (tipoArquivoRetorno == ARQUIVO_TODOS_OS_CALCULADOS) {
				this.gerarRegistroTipo0(Constantes.INDC_FINALIZAR_ROTEIRO_TODOS_IMOVEIS);
			} else if (tipoArquivoRetorno == ARQUIVO_INCOMPLETO) {
				this.gerarRegistroTipo0(Constantes.INDC_FINALIZAR_ROTEIRO_INCOMPLETO);
			}

			if (tipo0 != null) {
				arquivo = arquivo.append(tipo0);
			}

			for (int i = 0; i < qtdImoveisCalculados; i++) {
				int id = 0;

				if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_COMPLETO || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS) {
					id = i + 1;
				} else if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_CONCLUIDOS_ATE_AGORA || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_INCOMPLETO) {
					id = idsImoveisConcluidos.get(i);
				}

				Imovel imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + id, true);

				// Rota completa - checa se o imovel selecionado é informativo
				// ou nao.
				// Caso seja informativo, nao deve entrar no arquivo de retorno.
				// Guardamos todos os sequencias e matriculas, para a rota de
				// marcação
				if (!imovel.isImovelInformativo() || imovel.isImovelCondominio()) {
					System.out.println("Incluindo Imovel: " + imovel.getMatricula());
					if ((tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_COMPLETO || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS)
					// &&
					// Configuracao.getInstancia().getIndcRotaMarcacaoAtivada()
					// == Constantes.SIM
					) {

						if (imovel.getSequencialRotaMarcacao() == Constantes.NULO_INT) {

							int indicador = 0;// Configuracao.getInstancia().getSequencialAtualRotaMarcacao();

							sequencialRotaMarcacao.append("5" + Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + "")
									+ Util.adicionarZerosEsquerdaNumero(4, indicador + "") + "\n");

							imovel.setSequencialRotaMarcacao(indicador);
						} else {
							sequencialRotaMarcacao.append("5" + Util.adicionarZerosEsquerdaNumero(9, imovel.getMatricula() + "")
									+ Util.adicionarZerosEsquerdaNumero(4, imovel.getSequencialRotaMarcacao() + "") + "\n");
						}
					}

					/*
					 * Caso estejamos enviando apenas os lidos e não enviados
					 * até agora verificamos se o imovel deve ser enviado
					 * imediatamente. Como imóveis condominio podem ser
					 * impressos parcialmente, como no caso de conta menor do
					 * que o valor limite ou valor do crédito maior do que o
					 * valor da conta, não enviamos imóvel condominio para o
					 * arquivo de não enviados e lidos até agora. Esse
					 * procedimento só deve ser feito na finalização do roteiro.
					 * Caso estejamos enviando o imóvel completo, enviamos todos
					 * os calculados e não enviados até agora;
					 */
					// Condição 1
					if ((tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_CONCLUIDOS_ATE_AGORA && imovel.enviarAoImprimir() && !imovel.isImovelCondominio()) ||
					// Condição 2
							((tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_COMPLETO || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_INCOMPLETO) && imovel.enviarAoFinalizar()) ||
							// Condicao 3
							(tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS && imovel.getIndcImovelCalculado() == Constantes.SIM)) {

						System.out.println("vai enviar o movel: " + imovel.getMatricula());

						// Criamos os registros tipo 1
						this.gerarRegistroTipo1(imovel);

						// Criamos os registros tipo 2 e 3
						this.gerarRegistroTipo2e3(imovel);

						// Criamos os registros tipo 4
						this.gerarRegistroTipo4(imovel);

						arquivo.append(tipo1);
						arquivo.append(tipo2e3);
						arquivo.append(tipo4);

						idsImoveisGerados.addElement(new Long(imovel.getId()).intValue());

						imovel = null;
					} else if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS) {
						arquivo = null;

						retorno[0] = Boolean.TRUE;
						retorno[1] = null;

						ControladorImovel.getInstancia().setImovelSelecionado(imovel);
					}
				}
			}

			if (tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_COMPLETO || tipoArquivoRetorno == ArquivoRetorno.ARQUIVO_TODOS_OS_CALCULADOS) {
				arquivo.append(sequencialRotaMarcacao.toString());
			}

			try {
				out.write(arquivo.toString());
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.salvarExceptionLog(e);
			}

			retorno[0] = Boolean.FALSE;
			retorno[1] = idsImoveisGerados;
		}

		return retorno;
	}

	public List<Integer> gerarArquivoParaEnvio(Handler handler, Context context, int increment, int tipoEnvio) {
		List<Integer> listIdImoveis = null;
		OutputStreamWriter out = null;

		try {

			if (tipoEnvio == Constantes.TIPO_ENVIO_FINALIZAR_ROTA) {
				File fileArquivoCompleto = new File(Util.getRetornoRotaDirectory(), Util.getNomeArquivoEnviarConcluidos());

				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(context, "Erro ao salvar no cartão de memória!", Toast.LENGTH_SHORT).show();
					return new ArrayList<Integer>();
				}

				FileOutputStream os = new FileOutputStream(fileArquivoCompleto);
				out = new OutputStreamWriter(os);

			}
			arquivo = new StringBuffer();

			listIdImoveis = ControladorRota.getInstancia().getDataManipulator().selectIdsImoveisConcluidosENaoEnviados();

			gerarRegistroTipo0(Constantes.INDC_FINALIZAR_ROTEIRO);
			arquivo = arquivo.append(tipo0);
			int i = 0;

			for (Integer id : listIdImoveis) {

				Imovel imovel = ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + id, true);

				if (imovel.isImovelInformativo())
					continue;

				gerarRegistroTipo1(imovel);
				gerarRegistroTipo2e3(imovel);
				gerarRegistroTipo4(imovel);

				Bundle b = new Bundle();
				Message msg = handler.obtainMessage();
				b.putInt("finalizarRota" + String.valueOf(increment), (i + 1));
				b.putInt("imoveisNaoTransmitidos" + String.valueOf(increment), (i + 1));
				msg.setData(b);
				handler.sendMessage(msg);
				i++;
			}

			if (tipoEnvio == Constantes.TIPO_ENVIO_FINALIZAR_ROTA) {
				out.write(arquivo.toString());
				out.flush();
				out.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.salvarExceptionLog(e);
		}

		return listIdImoveis;

	}

	public static String getConteudoArquivoRetorno() {
		if (arquivo != null) {
			return arquivo.toString();
		} else {
			return null;
		}
	}

	public static Imovel getImovelSelecionado() {
		return ControladorImovel.getInstancia().getImovelSelecionado();
	}
}
