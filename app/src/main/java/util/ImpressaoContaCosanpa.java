package util;

import static model.Credito.CRED_BOLSA_AGUA;
import static model.Imovel.PERFIL_BOLSA_AGUA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Consumo;
import model.Conta;
import model.Credito;
import model.DadosCategoria;
import model.DadosFaturamentoFaixa;
import model.DadosQualidadeAgua;
import model.Debito;
import model.HistoricoConsumo;
import model.Imovel;
import model.Imposto;
import model.Medidor;
import model.Tarifa;
import model.TarifacaoMinima;
import ui.FileManager;
import views.MedidorAguaTab;
import android.util.Log;
import business.ControladorConta;
import business.ControladorImovel;
import business.ControladorRota;

import com.IS.Fachada;

public class ImpressaoContaCosanpa {

	private static String logType = "";

	private static Imovel imovel;

	private String numeroMedidor = "NAO MEDIDO";
	private String dataInstalacao = Constantes.NULO_STRING;
	private String situacaoAgua;
	private String situacaoEsgoto;
	private String leituraAnteriorInformada = Constantes.NULO_STRING;
	private  String leituraAtualInformada = Constantes.NULO_STRING;
	private String leituraAnteriorFaturada = Constantes.NULO_STRING;
	private String leituraAtualFaturada = Constantes.NULO_STRING;
	private String consumo = Constantes.NULO_STRING;
	private String diasConsumo = Constantes.NULO_STRING;
	private Consumo consumoAgua;
	private Consumo consumoEsgoto;
	private Medidor medidorAgua;
	private Medidor medidorPoco;
	private String dataLeituraAnteriorInformada = "";
	private String dataLeituraAtualInformada = "";
	private String dataLeituraAnteriorFaturada = "";
	private String dataLeituraAtualFaturada = "";
	private String media = "0";
	private int tipoConsumo = 0;
	private String economias = "";
	private String endereco = "";
	private String valorDebitos = "";
	private String valorCreditos = "";
	private String valorImpostos = "";
	private String descricaoAnoMesConta = "";

	// Linhas e molduras
	private String linesAndBoxes = "";

	// Historico consumo
	private String anoMesReferencia = "";
	private String historicoConsumo = "";
	private String hcMensagem = "7 0 50 579 ULTIMOS CONSUMOS\n";

	// Exigido Portaria 518/2004
	private int quantidadeCorExigidas;
	private int quantidadeTurbidezExigidas;
	private int quantidadeCloroExigidas;
	private int quantidadeFluorExigidas;
	private int quantidadeColiformesTotaisExigidas;
	private int quantidadeColiformesTermoTolerantesExigidas;

	// Analisado
	private int quantidadeCorAnalisadas;
	private int quantidadeTurbidezAnalisadas;
	private int quantidadeCloroAnalisadas;
	private int quantidadeFluorAnalisadas;
	private int quantidadeColiformesTotaisAnalisadas;
	private int quantidadeColiformesTermoTolerantesAnalisadas;

	// Conforme
	private int quantidadeCorConforme;
	private int quantidadeTurbidezConforme;
	private int quantidadeCloroConforme;
	private int quantidadeFluorConforme;
	private int quantidadeColiformesTotaisConforme;
	private int quantidadeColiformesTermoTolerantesConforme;

	private String anormalidadeConsumo = "";

	private String tarifacaoAgua = "";
	private String tarifacaoEsgoto = "";
	private String rateioAguaEsgoto = "";

	// Dados da conta
	private String dataVencimentoConta = "";
	private String valorConta = "";
	private String opcaoDebitoAutomatico = "";
	private String mensagens = "";
	private String mensagemBolsaAgua = "";
	private String matricula = "";
	private String referencia = "";
	private String dataVencimento = "";
	private String totalAPagar = "";
	private String repNumericaCodBarra = "";
	private String repCodigoBarrasSemDigitoVerificador = "";
	private String grupoFaturamento = "";

	private String txtConsumo = "";
	private String anormalidadeLeitura = "";

	//Linhas Boleto
	private String banco = "";
	private String bancoDigito = "";
	private String nossoNumero = "";
	private String numeroDocumento = "";


	private String montarComandoImpressaoFatura(int tipoImpressao) {

		String comando = "! 0 200 200 3200 1\n"+

				linesAndBoxes +

				//"T 0 0 201 47 " + Util.formatarCnpj(ControladorRota.getInstancia().getDadosGerais().getCnpjEmpresa().trim()) + "\n" + // TODO
				//"T 0 0 285 64 " + ControladorRota.getInstancia().getDadosGerais().getInscricaoEstadualEmpresa().trim() + "\n" + // TODO
				//"T 0 0 222 81 " + imovel.getGrupoFaturamento() + "\n" + // TODO

//    			"T 0 2 135 121 Versao: "+ Fachada.getAppVersion() + " - "+ MedidorAguaTab.getCurrentDateByGPS() + " /" + (imovel.getQuantidadeContasImpressas()+1) + "\n" + // TODO

				"T 7 1 70 115 " + imovel.getMatricula() + "\n" +
				"T 7 1 640 115 " + descricaoAnoMesConta +

//        		"T 0 2 52 199 \n " +

				"T 7 0 20 190 " + imovel.getNomeUsuario() + "\n" +
				"T 7 0 20 210 " + imovel.getCpfCnpjCliente() + "\n" +

				dividirLinha(0, 2, 400, 200, endereco, 55, 22) +

				"T 7 0 20 260 " + Util.formatarInscricao(imovel.getInscricao()) + "\n" +
				"T 7 0 350 260 " + imovel.getCodigoRota() + "\n" +
				"T 7 0 445 260 " + imovel.getSequencialRota() + "\n" +

				economias + // TODO

				"T 7 0 20 310 "+ numeroMedidor +"\n"+
				"T 7 0 255 310 "+ dataInstalacao +"\n"+
				"T 7 0 500 310 "+ situacaoAgua +"\n"+
				"T 7 0 688 310 "+ situacaoEsgoto +"\n"+

				"T 7 0 163 362 LEITURA\n"+
				"T 7 0 163 382 FATURADO\n" +
				"T 7 0 190 406 "+ leituraAnteriorFaturada +"\n"+
				"T 7 0 190 430 "+ leituraAtualFaturada + "\n" +
				"T 7 0 313 382 DATA\n"+
				"T 7 0 285 406 "+ dataLeituraAnteriorInformada +"\n"+
				"T 7 0 285 430 "+ dataLeituraAtualInformada + "\n"+

				gerarLinhaTelefoneAgenciaReguladora() + // TODO

				//"T 7 0 37 436 ANTERIOR\n" +
				//"T 7 0 37 460 ATUAL\n" +

				"T 5 0 37 460 "+ "GRUPO" + "\n" +
				"T 5 0 109 485 "+ grupoFaturamento + "\n" +
				"T 5 0 352 485 4\n"+
				"T 5 0 680 485 "+imovel.getMatricula()+"\n"+

				anormalidadeConsumo + // TODO
				anormalidadeLeitura + // TODO

				//"T 7 0 163 500 FATURADO\n" +
				//"T 7 0 190 524 "+ leituraAnteriorFaturada + "\n" +
				//"T 7 0 190 548 "+ leituraAtualFaturada + "\n" +
				//"T 7 0 313 500 DATA\n"+
				//"T 7 0 285 524 "+ dataLeituraAnteriorFaturada + "\n" +
				//"T 7 0 285 548 "+ dataLeituraAtualFaturada + "\n" +

				txtConsumo +

				"T 7 0 412 382 "+ consumo + "\n" +
				"T 7 0 745 382 DIAS\n"+
				"T 7 0 760 406 "+ diasConsumo +"\n" +
				"T 7 0 37 406 ANTERIOR\n"+
				"T 7 0 37 430 ATUAL\n"+

				"T "+ hcMensagem +

				anoMesReferencia +
				historicoConsumo +
				"T 7 0 75 752 MEDIA(m3):\n"+
				"T 7 0 195 752 "+ media + "\n";

		//==============================================

		if (tipoImpressao == Constantes.IMPRESSAO_FATURA){
			comando +=

					"T 7 0 425 565 QUALIDADE DA AGUA\n"+
							"T 7 0 645 565 Ref: \n"+
							"T 7 0 690 565 "+ Util.retornaDescricaoAnoMes(imovel.getAnoMesConta()) + "\n" +
							"T 7 0 287 590 PARAMETROS\n"+
							"T 7 0 418 590 PORT5 A.XX\n"+
							"T 7 0 545 590 ANALISADO\n"+
							"T 7 0 672 590 CONFORME\n"+
							"T 7 0 287 620 COR(uH)\n"+
							"T 7 0 287 640 TURBIDEZ(UT)\n"+
							"T 7 0 287 660 CLORO(mg/L)\n"+
							"T 7 0 287 680 FLUOR(mg/L)\n"+
							"T 7 0 287 700 COLIFORME TOTAL\n"+
							"T 7 0 287 720 Pres/Aus)\n"+
							"T 7 0 287 740 COLIFORME TERMO\n"+
							"T 7 0 287 760 TOLER.(Pres/Aus)\n"+
							"T 7 0 469 620 "+ quantidadeCorExigidas + "\n" +
							"T 7 0 469 640 "+ quantidadeTurbidezExigidas + "\n" +
							"T 7 0 469 660 "+ quantidadeCloroExigidas + "\n"+
							"T 7 0 469 680 "+ quantidadeFluorExigidas +"\n"+
							"T 7 0 469 700 "+ quantidadeColiformesTotaisExigidas + "\n"+
							"T 7 0 469 740 "+ quantidadeColiformesTermoTolerantesExigidas + "\n" +
							"T 7 0 582 620 "+ quantidadeCorAnalisadas + "\n" +
							"T 7 0 582 640 "+ quantidadeTurbidezAnalisadas + "\n" +
							"T 7 0 582 660 "+ quantidadeCloroAnalisadas + "\n" +
							"T 7 0 582 680 "+ quantidadeFluorAnalisadas + "\n" +
							"T 7 0 582 700 "+ quantidadeColiformesTotaisAnalisadas + "\n" +
							"T 7 0 582 740 "+ quantidadeColiformesTermoTolerantesAnalisadas + "\n"+
							"T 7 0 726 620 "+ quantidadeCorConforme + "\n" +
							"T 7 0 726 640 "+ quantidadeTurbidezConforme + "\n" +
							"T 7 0 726 660 "+ quantidadeCloroConforme + "\n" +
							"T 7 0 726 680 "+ quantidadeFluorConforme + "\n" +
							"T 7 0 726 700 "+ quantidadeColiformesTotaisConforme + "\n" +
							"T 7 0 726 740 "+ quantidadeColiformesTermoTolerantesConforme + "\n" +

							"T 7 0 53 788 DESCRICAO\n"+
							"T 7 0 571 788 CONSUMO\n"+
							"T 7 0 687 788 TOTAL(R$)\n"+

							tarifacaoAgua +
							tarifacaoEsgoto +
							rateioAguaEsgoto +
							valorDebitos +
							valorCreditos +
							valorImpostos +

							gerarLinhasAliquotasImpostos(imovel.getValorContaSemParcelamentoDebito()) +

							"T 7 1 20 1115 "+ dataVencimentoConta + "\n" +
							"T 4 0 640 1115 "+ valorConta + "\n" +

							"T 0 2 424 1270 OPCAO PELO DEB. AUTOMATICO: \n"+
							"T 5 0 649 1270 "+ opcaoDebitoAutomatico + "\n"+

							mensagens +
							mensagemBolsaAgua +

							"T 0 2 404 2606 "+ matricula + "\n" +
							"T 0 2 505 2606 "+ referencia + "\n" +
							"T 0 2 598 2606 "+ dataVencimento + "\n" +
							"T 0 2 730 2606 "+ totalAPagar + "\n" +

							banco+
							bancoDigito+

							"T 7 0 300 2821 PAGAVEL EM QUALQUER BANCO\n"+

							repNumericaCodBarra +

							"T 7 0 32 2897 PAGADOR:" + imovel.getNomeUsuario() +"\n"+
							getCpfCnpjUsuario()+

							"T 7 0 32 2925 NOSSO NUMERO\n" +
							"T 7 0 32 2945 "+ nossoNumero +"\n" +
							"T 7 0 251 2925 NUMERO DOCUMENTO\n" +
							"T 7 0 251 2945 "+ numeroDocumento +" \n" +
							"T 7 0 450 2925 VENCIMENTO\n" +
							"T 7 0 450 2945 "+ dataVencimentoConta + "\n" +
							"T 7 0 580 2925 VALOR DOCUMENTO\n" +
							"T 7 0 580 2945 "+ valorConta +"\n" +

							"T 7 0 32 2970 COMPANHIA DE SANEAMENTO DO PARA / CNPJ: 04.945.341/0001-90\n" +
							"T 7 0 32 2996 INFORMACOES DE RESPONSABILIDADE DO BENEFICIARIO\n" +
							"T 7 0 32 3016 EM CASO DE ATRASO, MULTAS, JUROS E CORRECAO\n" +
							"T 7 0 32 3036 SERAO COBRADOS NA PROXIMA FATURA\n"+
							repCodigoBarrasSemDigitoVerificador +

							"FORM\n"+
							"PRINT\n";

		}else if (tipoImpressao == Constantes.IMPRESSAO_EXTRATO_CONDOMINIAL){
			comando +=
					"T 7 0 200 700 EXTRATO DE CONSUMO DO MACROMEDIDOR \n"+
							"T 7 0 53 725 CONSUMO DO IMOVEL CONDOMINIO \n"+
							"T 7 0 571 725 "+ imovel.getConsumoAgua().getConsumoCobradoMes() + "\n" +
							"T 7 0 53 750 SOMA DOS CONSUMOS DOS IMOVEIS VINCULADOS \n"+
							"T 7 0 571 750 "+ imovel.getEfetuarRateioConsumoHelper().getConsumoLigacaoAguaTotal() + "\n" +
							"T 7 0 53 775 QUANTIDADE IMOVEIS VINCULADOS \n"+
							"T 7 0 571 775 "+ imovel.getEfetuarRateioConsumoHelper().getQuantidadeEconomiasAguaTotal() + "\n" +
							"T 7 0 53 800 VALOR RATEADO \n"+
							"T 7 0 571 800  R$ "+ Util.formatarDoubleParaMoedaReal(imovel.getEfetuarRateioConsumoHelper().getContaParaRateioAgua()) + "\n" +
							"T 7 0 53 825 VALOR RATEADO POR UNIDADE \n"+
							"T 7 0 571 825 R$ "+ Util.formatarDoubleParaMoedaReal(imovel.getEfetuarRateioConsumoHelper().getContaParaRateioAgua()
							/ imovel.getEfetuarRateioConsumoHelper().getQuantidadeEconomiasAguaTotal()) + "\n" +

							"T 7 0 367 850 IMPORTANTE \n"+
							"T 7 0 53 900 CASO O VALOR DO RATEIO ESTEJA ELEVADO \n"+
							"T 7 0 63 925 1. Confirme a leitura do macro \n"+
							"T 7 0 63 950 2. Verifique os reservatorios \n"+
							"T 7 0 63 975 3. Verifique se ha apartamento ligado clandestinamente\n"+
							"T 7 0 53 1025 QUALQUER IRREGULARIDADE COMUNIQUE A COSANPA ATRAVES DO \n"+
							"T 7 0 53 1050 SETOR DE ATENDIMENTO \n"+
							"T 7 0 53 1075 RATEIO: Obtido atraves da diferenca do consumo entre \n"+
							"T 7 0 53 1100 o macromedidor e os consumos dos apartamentos \n"+

							"T 0 2 344 1961 "+ matricula + "\n" +
							"T 5 0 109 1961 "+ grupoFaturamento + "\n" +
							"T 5 0 352 1961 4\n"+
							"FORM\n"+
							"PRINT\n";
		}

		logType = "montarComandoImpressaoFatura";

		LogUtil.salvarLog(logType, "Versao: " + Fachada.getAppVersion() + " | Data Atual do GPS: " + MedidorAguaTab.getCurrentDateByGPS());
		LogUtil.salvarLog(logType, "Imovel: " + matricula);
		LogUtil.salvarLog(logType, "Vezes que Conta foi Impressa: " + (imovel.getQuantidadeContasImpressas() + 1));
		LogUtil.salvarLog(logType, "Situacao Agua: " + situacaoAgua + " | Situacao Esgoto: " + situacaoEsgoto);
		LogUtil.salvarLog(logType, "Consumo: " + consumo + " | Dias de Consumo: " + diasConsumo);
		LogUtil.salvarLog(logType, "Data Leitura Anterior Informada: " + dataLeituraAnteriorInformada + " | Data Leitura Atual Informada: " + dataLeituraAtualInformada);
		LogUtil.salvarLog(logType, "Leitura Anterior Informada: " + leituraAnteriorInformada + " | Leitura Atual Informada: " + leituraAtualInformada);
		LogUtil.salvarLog(logType, "Data Leitura Anterior Faturada: " + dataLeituraAnteriorFaturada + " | Data Leitura Atual Faturada: " + dataLeituraAtualFaturada);
		LogUtil.salvarLog(logType, "Leitura Anterior Faturada: " + leituraAnteriorFaturada + " | Leitura Atual Faturada: " + leituraAtualFaturada);
		LogUtil.salvarLog(logType, "Valor: " + valorConta + " | Referencia: " + referencia+ " | Data de Vencimento: " + dataVencimento);
		LogUtil.salvarLog(logType, "Total a Pagar: " + totalAPagar);

		return comando;
	}

	public String getComandoImpressaoFatura(Imovel imovel, int impressaoTipo) {
		this.imovel = imovel;
		getDadosFatura(imovel, impressaoTipo);
		return montarComandoImpressaoFatura(impressaoTipo);
	}

	public void getDadosFatura(Imovel imovel, int tipoImpressao) {

		if (tipoImpressao == Constantes.IMPRESSAO_FATURA){
			linesAndBoxes = "BOX 32 405 802 452 1\n" +
					"LINE 720 385 720 425 1\n" +
					"LINE 403 385 403 447 1\n" +
					"BOX 32 361 802 405 1\n" +
					"LINE 278 385 278 447 1\n" +

					//"BOX 32 523 802 570 1\n" +
					//"LINE 720 503 720 543 1\n" +
					//"LINE 403 503 403 565 1\n" +
					//"BOX 32 499 802 523 1\n" +
					//"LINE 278 503 278 565 1\n" +

					"BOX 283 588 802 615 1\n"+
					"BOX 283 615 802 782 1\n"+
					"LINE 656 588 656 782 1\n"+
					"LINE 415 588 415 615 1\n"+
					"LINE 535 588 535 782 1\n" +

					//Linhas do Boleto
					"LINE 32 2840 802 2840 1\n" +
					"LINE 224 2820 224 2840 2\n" +
					"LINE 293 2820 293 2840 2 \n" +
					"LINE 32 2892 802 2892 1\n" +
					"LINE 32 2924 802 2924 1\n" +
					"LINE 241 2924 241 2968 2\n" +
					"LINE 446 2924 446 2968 2\n" +
					"LINE 575 2924 575 2968 2\n" +
					"LINE 32 2968 802 2968 1\n"+
					"";


		}else if (tipoImpressao == Constantes.IMPRESSAO_EXTRATO_CONDOMINIAL){
			linesAndBoxes = "BOX 32 435 802 482 1\n" +
					"BOX 32 411 802 435 1\n" +
					"LINE 720 415 720 455 1\n" +
					"LINE 403 415 403 477 1\n" +
					"LINE 290 415 290 477 1\n";
		}

		if (imovel.getAnoMesConta().compareTo(Constantes.NULO_STRING) != 0){
			descricaoAnoMesConta = Util.retornaDescricaoAnoMes(imovel.getAnoMesConta()) + "\n";

		}else{
			descricaoAnoMesConta = Util.retornaDescricaoAnoMes(ControladorRota.getInstancia().getDadosGerais().getAnoMesFaturamento()) + "\n";
		}

		List dc = imovel.getDadosCategoria();
		List quantidadeEconomias = categoriasEconomias(dc);

		situacaoAgua = imovel.getDescricaoSitLigacaoAgua(new Integer(imovel.getSituacaoLigAgua()));
		situacaoEsgoto = imovel.getDescricaoSitLigacaoEsgoto(new Integer(imovel.getSituacaoLigEsgoto()));
		consumoAgua = imovel.getConsumoAgua();
		consumoEsgoto = imovel.getConsumoEsgoto();;

		if (imovel.getEnderecoEntrega().trim().length() == 0) {
			endereco = imovel.getEndereco();
		} else {
			endereco = imovel.getEnderecoEntrega();
		}

		for (int i = 0; i < quantidadeEconomias.size(); i++) {
			Object[] dadosCategoria = (Object[]) quantidadeEconomias.get(i);
			economias += formarLinha(7, 0, 464, 260, dadosCategoria[0] + "", i * 85, 0);
			economias += formarLinha(7, 0, 710, 260, dadosCategoria[1] + "", i * 85, 0);
		}

		medidorAgua = imovel.getMedidor(Constantes.LIGACAO_AGUA);
		medidorPoco = imovel.getMedidor(Constantes.LIGACAO_POCO);

		if (medidorAgua != null) {

			numeroMedidor = medidorAgua.getNumeroHidrometro();
			media = String.valueOf(medidorAgua.getConsumoMedio());

			if (medidorAgua.getLeituraAnteriorInformada() != Constantes.NULO_INT){
				leituraAnteriorInformada = medidorAgua.getLeituraAnteriorInformada() + "";
			}
			dataLeituraAnteriorFaturada = Util.dateToString(medidorAgua.getDataLeituraAnteriorFaturada());
			dataLeituraAnteriorInformada = Util.dateToString(medidorAgua.getDataLeituraAnteriorInformada());
			dataLeituraAtualInformada = Util.dateToString(medidorAgua.getDataLeitura());
			dataInstalacao = Util.dateToString(medidorAgua.getDataInstalacao());

			if (consumoAgua != null) {

				tipoConsumo = consumoAgua.getTipoConsumo();

				if(medidorAgua.getLeituraAnteriorFaturamento() != Constantes.NULO_INT){
					leituraAnteriorFaturada = String.valueOf(medidorAgua.getLeituraAnteriorFaturamento());
				}

				if (medidorAgua.getLeituraAtualFaturamento() != Constantes.NULO_INT) {

					leituraAtualFaturada = String.valueOf(medidorAgua.getLeituraAtualFaturamento());
					dataLeituraAtualFaturada = Util.dateToString(medidorAgua.getDataLeituraAtualFaturamento());

//				    if (imovel.getIdImovelCondominio() != Constantes.NULO_INT ) {
//				    	consumo = (consumoAgua.getConsumoCobradoMes() - consumoAgua.getConsumoRateio()) + "";
//
//				    }else{
					consumo = consumoAgua.getConsumoCobradoMes() + "";

//				    }

					diasConsumo = Long.toString(medidorAgua.getQtdDiasAjustado());

				} else {

					leituraAtualFaturada = "";
					consumo = consumoAgua.getConsumoCobradoMes() + "";
					diasConsumo = consumoAgua.getDiasConsumo() + "";
				}

				if (medidorAgua.getLeitura()!= Constantes.NULO_INT &&
						consumoAgua.getLeituraAtual() != Constantes.NULO_INT) {

					leituraAtualInformada = medidorAgua.getLeitura() + "";
				} else {
					leituraAtualInformada = "";
					dataLeituraAtualInformada = "";
				}
			}

		} else if (medidorPoco != null) {

			media = String.valueOf(medidorPoco.getConsumoMedio());
			numeroMedidor = medidorPoco.getNumeroHidrometro();

			if (medidorPoco.getLeituraAnteriorInformada() != Constantes.NULO_INT){
				leituraAnteriorInformada = medidorPoco.getLeituraAnteriorInformada()+ "";
			}
			dataLeituraAnteriorFaturada = Util.dateToString(medidorPoco.getDataLeituraAnteriorFaturada());
			dataLeituraAnteriorInformada = Util.dateToString(medidorPoco.getDataLeituraAnteriorInformada());
			dataLeituraAtualInformada = Util.dateToString(medidorPoco.getDataLeitura());
			dataInstalacao = Util.dateToString(medidorPoco.getDataInstalacao());

			if (consumoEsgoto != null) {

				tipoConsumo = consumoEsgoto.getTipoConsumo();

				if(medidorPoco.getLeituraAnteriorFaturamento() != Constantes.NULO_INT){
					leituraAnteriorFaturada = String.valueOf(medidorPoco.getLeituraAnteriorFaturamento());
				}

				if (medidorPoco.getLeituraAtualFaturamento() != Constantes.NULO_INT) {

					leituraAtualFaturada = String.valueOf(medidorPoco.getLeituraAtualFaturamento());
					dataLeituraAtualFaturada = Util.dateToString(medidorPoco.getDataLeituraAtualFaturamento());

					consumo = consumoEsgoto.getConsumoCobradoMes() + "";
//				    }
					diasConsumo = Long.toString(medidorPoco.getQtdDiasAjustado());

				} else {

					leituraAtualFaturada = "";
					consumo = consumoEsgoto.getConsumoCobradoMes() + "";
					diasConsumo = consumoEsgoto.getDiasConsumo() + "";
				}

				if (medidorPoco.getLeitura()!= Constantes.NULO_INT &&
						consumoEsgoto.getLeituraAtual() != Constantes.NULO_INT) {

					leituraAtualInformada = medidorPoco.getLeitura() + "";
				} else {
					leituraAtualInformada = "";
					dataLeituraAtualInformada = "";
				}

			}
		} else if (medidorAgua == null && medidorPoco == null) {

			List<HistoricoConsumo> histConsumo = imovel.getHistoricosConsumo();

			if (histConsumo.size() > 0) {
				int sumConsumo = 0;
				for (int i = 0; i < histConsumo.size(); i++) {
					HistoricoConsumo reg3 = histConsumo.get(i);
					sumConsumo += Integer.parseInt(reg3.getConsumo());
				}
				media = (int) (sumConsumo / histConsumo.size()) + "";
			}else{
				media = "0";
			}


			if (consumoAgua != null) {

				//		    if (consumoAgua.getLeituraAtual() != Constantes.NULO_INT) {
				//		    	leituraAtual = consumoAgua.getLeituraAtual() + "";
				//		    } else {
				leituraAtualInformada = "";
				dataLeituraAtualInformada = "";
				//		    }

				consumo = consumoAgua.getConsumoCobradoMes() + "";
				// Numero de dias de consumo nunca deve ser ZERO mesmo para imoveis fixos.
				diasConsumo =  Long.toString(Util.quantidadeDiasMes(Calendar.getInstance())) + "";
			}
		}

		if (imovel.getConsumoAgua() != null){
			anormalidadeConsumo = Util.validarAnormalidadeConsumo(imovel.getConsumoAgua());
			if( anormalidadeConsumo != null){
				anormalidadeConsumo = formarLinha(0, 2, 430, 460, "ANORM. CONSUMO: " + anormalidadeConsumo, 0, 0);
			} else {
				anormalidadeConsumo = Constantes.NULO_STRING;
			}
		}

		if (imovel.getConsumoAgua() != null){
			if (imovel.getConsumoAgua().getAnormalidadeLeituraFaturada() != 0 &&
					imovel.getConsumoAgua().getAnormalidadeLeituraFaturada() != Constantes.NULO_INT	){

				try {
					anormalidadeLeitura += formarLinha(0, 2, 430, 374, "ANORM. LEITURA: " + FileManager.getAnormalidade(imovel.getConsumoAgua().getAnormalidadeLeituraFaturada()).getDescricao(), 0, 0);
				} catch (IOException e) {
					e.printStackTrace();
					LogUtil.salvarExceptionLog(e);
				}
			}
		}


		List<HistoricoConsumo> historicosConsumo = imovel.getHistoricosConsumo();
		int k = 0;
		hcMensagem += "LINE 115 605 115 745 1\n";
		for (HistoricoConsumo hc : historicosConsumo) {
			anoMesReferencia += formarLinha(0, 2, 44, 602, Util.getAnoBarraMesReferencia(hc.getAnoMesReferencia()) + "", 0, k * 25);

			String anormalidade = "";
			if (hc.getAnormalidadeLeitura() != Constantes.NULO_INT && hc.getAnormalidadeLeitura() != 0) {
				anormalidade = "A. Leit.:" + hc.getAnormalidadeLeitura() + "";
			} else if (hc.getAnormalidadeConsumo() != Constantes.NULO_INT && hc.getAnormalidadeConsumo() != 0) {
				anormalidade = "A. Cons.:" + hc.getAnormalidadeConsumo() + "";
			}

			historicoConsumo += formarLinha(0, 2, 127, 602, Util.verificarNuloInt(hc.getConsumo()) + " m3 " + anormalidade, 0, k*25);
			k++;
		}

		if (tipoImpressao == Constantes.IMPRESSAO_FATURA){

			// Exigido Portaria 518/2004
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCorExigidas() != Constantes.NULO_INT){
				quantidadeCorExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeCorExigidas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezExigidas() != Constantes.NULO_INT){
				quantidadeTurbidezExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezExigidas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCloroExigidas() != Constantes.NULO_INT){
				quantidadeCloroExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeCloroExigidas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeFluorExigidas() != Constantes.NULO_INT){
				quantidadeFluorExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeFluorExigidas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisExigidas() != Constantes.NULO_INT){
				quantidadeColiformesTotaisExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisExigidas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesExigidas() != Constantes.NULO_INT){
				quantidadeColiformesTermoTolerantesExigidas = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesExigidas();
			}

			// Analisado
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCorAnalisadas() != Constantes.NULO_INT){
				quantidadeCorAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeCorAnalisadas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezAnalisadas() != Constantes.NULO_INT){
				quantidadeTurbidezAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezAnalisadas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCloroAnalisadas() != Constantes.NULO_INT){
				quantidadeCloroAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeCloroAnalisadas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeFluorAnalisadas() != Constantes.NULO_INT){
				quantidadeFluorAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeFluorAnalisadas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisAnalisadas() != Constantes.NULO_INT){
				quantidadeColiformesTotaisAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisAnalisadas();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesAnalisadas() != Constantes.NULO_INT){
				quantidadeColiformesTermoTolerantesAnalisadas = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesAnalisadas();
			}

			//  Conforme
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCorConforme() != Constantes.NULO_INT){
				quantidadeCorConforme = DadosQualidadeAgua.getInstancia().getQuantidadeCorConforme();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezConforme() != Constantes.NULO_INT){
				quantidadeTurbidezConforme = DadosQualidadeAgua.getInstancia().getQuantidadeTurbidezConforme();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeCloroConforme() != Constantes.NULO_INT){
				quantidadeCloroConforme = DadosQualidadeAgua.getInstancia().getQuantidadeCloroConforme();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeFluorConforme() != Constantes.NULO_INT){
				quantidadeFluorConforme = DadosQualidadeAgua.getInstancia().getQuantidadeFluorConforme();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisConforme() != Constantes.NULO_INT){
				quantidadeColiformesTotaisConforme = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTotaisConforme();
			}
			if (DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesConforme() != Constantes.NULO_INT){
				quantidadeColiformesTermoTolerantesConforme = DadosQualidadeAgua.getInstancia().getQuantidadeColiformesTermoTolerantesConforme();
			}

			if (imovel.isImovelCondominio()) {
				txtConsumo += formarLinha(7, 0, 412, 382, "CONSUMO (m3)", 0, 0);

			} else {
				txtConsumo +=	formarLinha(7, 0, 412, 382, ControladorConta.getInstancia().getTipoConsumoToPrint(tipoConsumo), 0, 0);
			}

			int ultimaLinhaAgua = 0;
			int ultimaLinhaPoco = 0;
			int quantidadeLinhasAtual = 0;
			int quantidadeMaximaLinhas = 18;
			List linhaAgua = gerarLinhasTarifaAgua(consumoAgua);
			//	    Log.i("Linhas", ">>>" + ((String) linhaAgua.get(0) == null));
			tarifacaoAgua = (String) linhaAgua.get(0);
			ultimaLinhaAgua = (((Integer) linhaAgua.get(1)).intValue());
			if (ultimaLinhaAgua != 0) {
				quantidadeLinhasAtual = quantidadeLinhasAtual + ultimaLinhaAgua + 1;
			}
			ultimaLinhaAgua *= 25;
			List tarifasPoco = gerarLinhasTarifaPoco();
			ultimaLinhaPoco = ultimaLinhaAgua;
			for (int i = 0; i < tarifasPoco.size(); i++) {
				String[] tarifaPoco = (String[]) tarifasPoco.get(i);
				ultimaLinhaPoco = ultimaLinhaAgua + ((i + 1) * 25);
				quantidadeLinhasAtual++;
				int deslocaDireitaColuna;
				if (i == 0 || i == 1 || i == 2) {
					deslocaDireitaColuna = i;
				} else {
					deslocaDireitaColuna = 2;
				}
				if (tarifaPoco[0] != null) {
					tarifacaoEsgoto += formarLinha(7, 0, 53, 788, tarifaPoco[0], deslocaDireitaColuna * 10, (i + 1) * 25 + ultimaLinhaAgua);
				}
				if (tarifaPoco[1] != null) {
					tarifacaoEsgoto += formarLinha(7, 0, 571, 788, tarifaPoco[1], 0, (i + 1) * 25 + ultimaLinhaAgua);
				}
				if (tarifaPoco[2] != null) {
					tarifacaoEsgoto += formarLinha(7, 0, 697, 788, tarifaPoco[2], 0, (i + 1) * 25 + ultimaLinhaAgua);
				}
			}


			// Dados dos Valores de Rateio de Água e Esgoto
			List rateios = gerarLinhasRateioAguaEsgotoCobrados();
			int ultimaLinhaRateio = ultimaLinhaPoco;
			rateioAguaEsgoto = "";
			for (int i = 0; i < rateios.size(); i++) {
				String[] debito = (String[]) rateios.get(i);
				ultimaLinhaRateio = ultimaLinhaPoco + ((i + 1) * 25);
				quantidadeLinhasAtual++;
				if (debito[0] != null) {
					rateioAguaEsgoto += formarLinha(7, 0, 53, 788, debito[0], 0, (i + 1) * 25 + ultimaLinhaPoco);
				}
				if (debito[1] != null) {
					rateioAguaEsgoto += formarLinha(7, 0, 697, 788, debito[1], 0, (i + 1) * 25 + ultimaLinhaPoco);
				}
			}

			int indicadorDiscriminarDescricao = retornaIndicadorDiscriminar(quantidadeMaximaLinhas, quantidadeLinhasAtual, 'd');
			List debitos = this.gerarLinhasDebitosCobrados(indicadorDiscriminarDescricao);
			int ultimaLinhaDebito = ultimaLinhaRateio;
			for (int i = 0; i < debitos.size(); i++) {
				String[] debito = (String[]) debitos.get(i);
				ultimaLinhaDebito = ultimaLinhaRateio + ((i + 1) * 25);
				quantidadeLinhasAtual++;
				// int deslocaDireitaColuna;
				// if( i == 0 || i == 1 || i==2 ){
				// deslocaDireitaColuna = i;
				// } else {
				// deslocaDireitaColuna = 2;
				// }
				if (debito[0] != null) {
					valorDebitos += formarLinha(7, 0, 53, 788, debito[0], 0, (i + 1) * 25 + ultimaLinhaRateio);
				}
				if (debito[1] != null) {
					valorDebitos += formarLinha(7, 0, 571, 788, debito[1], 0, (i + 1) * 25 + ultimaLinhaRateio);
				}
				if (debito[2] != null) {
					valorDebitos += formarLinha(7, 0, 697, 788, debito[2], 0, (i + 1) * 25 + ultimaLinhaRateio);
				}
			}
			indicadorDiscriminarDescricao = retornaIndicadorDiscriminar(quantidadeMaximaLinhas, quantidadeLinhasAtual, 'c');
			List creditos = this.gerarLinhasCreditosRealizados(indicadorDiscriminarDescricao);
			int ultimaLinhaCredito = ultimaLinhaDebito;
			for (int i = 0; i < creditos.size(); i++) {
				String[] credito = (String[]) creditos.get(i);
				ultimaLinhaCredito = ultimaLinhaDebito + ((i + 1) * 25);
				// int deslocaDireitaColuna;
				// if( i == 0 || i == 1 || i==2 ){
				// deslocaDireitaColuna = i;
				// } else {
				// deslocaDireitaColuna = 2;
				// }
				if (credito[0] != null) {
					valorCreditos += formarLinha(7, 0, 53, 788, credito[0], 0, (i + 1) * 25 + ultimaLinhaDebito);
				}
				if (credito[1] != null) {
					valorCreditos += formarLinha(7, 0, 571, 788, credito[1], 0, (i + 1) * 25 + ultimaLinhaDebito);
				}
				if (credito[2] != null) {
					valorCreditos += formarLinha(7, 0, 697, 788, credito[2], 0, (i + 1) * 25 + ultimaLinhaDebito);
				}
			}
			List impostos = this.gerarLinhasImpostosRetidos();
			for (int i = 0; i < impostos.size(); i++) {
				String[] imposto = (String[]) impostos.get(i);
				int deslocaDireitaColuna;
				if (i == 0 || i == 1) {
					deslocaDireitaColuna = i;
				} else {
					deslocaDireitaColuna = 1;
				}
				if (imposto[0] != null) {
					valorImpostos += formarLinha(7, 0, 53, 788, imposto[0], deslocaDireitaColuna * 10, (i + 1) * 25 + ultimaLinhaCredito);
				}
				if (imposto[1] != null) {
					valorImpostos += formarLinha(7, 0, 571, 788, imposto[1], 0, (i + 1) * 25 + ultimaLinhaCredito);
				}
				if (imposto[2] != null) {
					valorImpostos += formarLinha(7, 0, 697, 788, imposto[2], 0, (i + 1) * 25 + ultimaLinhaCredito);
				}
			}

			dataVencimentoConta = Util.dateToString(imovel.getDataVencimento());

			valorConta = Util.formatarDoubleParaMoedaReal(imovel.getValorConta());

			opcaoDebitoAutomatico = imovel.getOpcaoDebitoAutomatico() == Constantes.NULO_INT ? "" : imovel.getOpcaoDebitoAutomatico()+"";
			Log.i("opcao debito automatico", opcaoDebitoAutomatico);

			if(Integer.parseInt(imovel.getCodigoPerfil()) == PERFIL_BOLSA_AGUA  && imovel.getValorConta() <= 0.0){
				mensagemBolsaAgua = dividirLinha(7, 0, 30, 1270, "PROGRAMA AGUA PARA, QUITADO PELO GOVERNO DO ESTADO DO PARA", 27, 20);
			}else if(Integer.parseInt(imovel.getCodigoPerfil()) == PERFIL_BOLSA_AGUA && imovel.getValorConta() > 0.0 ){
				mensagemBolsaAgua = dividirLinha(7, 0, 30, 1270, "PROGRAMA AGUA PARA, 20.000  LITROS QUITADOS PELO GOVERNO DO ESTADO DO PARA", 28, 20);
			}

			if (imovel.getMensagemQuitacaoAnual() != null && !imovel.getMensagemQuitacaoAnual().equals("")) {
				mensagens = dividirLinha(7, 0, 35, 1815, imovel.getMensagemQuitacaoAnual(), 60, 20);

			} else if (imovel.getMensagemEstouroConsumo1() != null && !imovel.getMensagemEstouroConsumo1().equals("")) {
				String mensagensEstouro = "";
				if (imovel.getMensagemEstouroConsumo1() != null){
					mensagensEstouro = imovel.getMensagemEstouroConsumo1();
				}

				if (imovel.getMensagemEstouroConsumo2() != null){
					mensagensEstouro += " " + imovel.getMensagemEstouroConsumo2();
				}

				if (imovel.getMensagemEstouroConsumo3() != null){
					mensagensEstouro += " " + imovel.getMensagemEstouroConsumo3();
				}

				if (mensagensEstouro != null){
					if (mensagensEstouro.length() > 240){
						mensagens = dividirLinha(7, 0, 35, 1815, mensagensEstouro.substring(0, 240), 60, 20);
					}else{
						mensagens = dividirLinha(7, 0, 35, 1815, mensagensEstouro, 60, 20);
					}
				}

			} else {

				String mensagensConta = "";
				if (imovel.getMensagemConta1() != null){
					mensagensConta = imovel.getMensagemConta1();
				}

				if (imovel.getMensagemConta2() != null){
					mensagensConta += " " + imovel.getMensagemConta2();
				}

				if (imovel.getMensagemConta3() != null){
					mensagensConta += " " + imovel.getMensagemConta3();
				}

				if (mensagensConta != null){
					if (mensagensConta.length() > 240){
						mensagens = dividirLinha(7, 0, 35, 1815, mensagensConta.substring(0, 240), 60, 20);
					}else{
						mensagens = dividirLinha(7, 0, 35, 1815, mensagensConta, 60, 20);
					}
				}
			}

			matricula = ""+imovel.getMatricula();
			referencia = Util.formatarAnoMesParaMesAno(imovel.getAnoMesConta());
			dataVencimento = Util.dateToString(imovel.getDataVencimento());
			totalAPagar = Util.formatarDoubleParaMoedaReal(imovel.getValorConta());

			// Situação criada para imprimir codigo de barras apenas quando o valor da conta for maior que o mínimo.
			if (imovel.getCodigoAgencia() == null || imovel.getCodigoAgencia().equals("")) {
				System.out.println("##COD AGENCIA DO IF: " + imovel.getCodigoAgencia());

				String cpfCnpf = ControladorImovel.getInstancia().getImovelSelecionado().getCpfCnpjCliente().trim();
				//String codigoConvenio = ControladorImovel.getInstancia().getImovelSelecionado().getCodigoConvenio().trim();
				String codigoConvenioImovel = imovel.getCodigoConvenio().trim();

				if(cpfCnpf.length() > 0 && codigoConvenioImovel.length() > 0){
					String representacaoNumericaCodBarraFormatada = "";
					String representacaoNumericaCodBarra = Util.obterRepresentacaoNumericaCodigoBarra(new Integer(3), imovel.getValorConta(), new Integer(Integer.parseInt(imovel.getInscricao().substring(0, 3))), new Integer(imovel.getMatricula()),
							Util.formatarAnoMesParaMesAnoSemBarra(imovel.getAnoMesConta()), new Integer(imovel.getDigitoVerificadorConta()), null, null, null, null, null, null, imovel);
					representacaoNumericaCodBarraFormatada = representacaoNumericaCodBarra;
					repNumericaCodBarra += formarLinha(5, 1, 80, 2849, representacaoNumericaCodBarraFormatada, 0, 0);
					String representacaoNumericaSemPontos = representacaoNumericaCodBarra.substring(0,5) + representacaoNumericaCodBarra.substring(6,11) + representacaoNumericaCodBarra.substring(12,17)
							+ representacaoNumericaCodBarra.substring(18,24) + representacaoNumericaCodBarra.substring(25,30) + representacaoNumericaCodBarra.substring(31,37) + representacaoNumericaCodBarra.substring(38,39)
							+ representacaoNumericaCodBarra.substring(40,54);
					String representacaoCodigoBarrasSemDigitoVerificador = representacaoNumericaSemPontos.substring(0, 4) + representacaoNumericaSemPontos.substring(32, 47) + representacaoNumericaSemPontos.substring(4, 9) + representacaoNumericaSemPontos.substring(10, 20)
							+ representacaoNumericaSemPontos.substring(21, 31);
					repCodigoBarrasSemDigitoVerificador += "B I2OF5 1 2 90 35 3076 " + representacaoCodigoBarrasSemDigitoVerificador + "\n";
					banco = "T 7 0 32 2821 BANCO DO BRASIL\n";
					bancoDigito = "T 7 0 230 2821 001-9\n";
					nossoNumero = getNossoNumero();
					numeroDocumento = imovel.getAnoMesConta() + imovel.getNumeroConta();

				}else{
					String representacaoNumericaCodBarraFormatada = "";
					String representacaoNumericaCodBarra = Util.obterRepresentacaoNumericaCodigoBarra(new Integer(3), imovel.getValorConta(), new Integer(Integer.parseInt(imovel.getInscricao().substring(0, 3))), new Integer(imovel.getMatricula()),
							Util.formatarAnoMesParaMesAnoSemBarra(imovel.getAnoMesConta()), new Integer(imovel.getDigitoVerificadorConta()), null, null, null, null, null, null, imovel);

					representacaoNumericaCodBarraFormatada = representacaoNumericaCodBarra.substring(0, 11).trim() + "-" + representacaoNumericaCodBarra.substring(11, 12).trim() + " " + representacaoNumericaCodBarra.substring(12, 23).trim() + "-"
							+ representacaoNumericaCodBarra.substring(23, 24).trim() + " " + representacaoNumericaCodBarra.substring(24, 35).trim() + "-" + representacaoNumericaCodBarra.substring(35, 36).trim() + " " + representacaoNumericaCodBarra.substring(36, 47).trim() + "-"
							+ representacaoNumericaCodBarra.substring(47, 48);
					repNumericaCodBarra += formarLinha(5, 1, 80, 2849, representacaoNumericaCodBarraFormatada, 0, 0);
					String representacaoCodigoBarrasSemDigitoVerificador = representacaoNumericaCodBarra.substring(0, 11) + representacaoNumericaCodBarra.substring(12, 23) + representacaoNumericaCodBarra.substring(24, 35) + representacaoNumericaCodBarra.substring(36, 47);
					repCodigoBarrasSemDigitoVerificador += "B I2OF5 1 2 95 35 3076 " + representacaoCodigoBarrasSemDigitoVerificador + "\n";
				}

			} else {
				repCodigoBarrasSemDigitoVerificador = formarLinha(4, 0, 182, 3036, "DEBITO AUTOMATICO", 0, 0);
			}

			grupoFaturamento = ""+imovel.getGrupoFaturamento();

		}
	}

	public String imprimirNotificacaoDebito(Imovel imovel) {
		ImpressaoContaCosanpa.imovel = imovel;
		getDadosAvisoDebito(ImpressaoContaCosanpa.imovel);

		String comando = "! 0 816 0 1720 1\n"+

				//Incluindo data de impressao, Versao do Leitura e Impressao Simultanea e
				// Data e hora de impressão na conta impressa
				"T 0 2 135 121 Versao: "+ Fachada.getAppVersion() + " - "+ MedidorAguaTab.getCurrentDateByGPS() + " /" + (imovel.getQuantidadeContasImpressas()+1) + "\n" +
				// Matricula do imovel e Ano mes de referencia da conta
				"T 7 1 464 90 "+ imovel.getMatricula() + "\n" + descricaoAnoMesConta +
				"T 0 0 201 47 "+ Util.formatarCnpj(ControladorRota.getInstancia().getDadosGerais().getCnpjEmpresa().trim()) + "\n" +
				"T 0 0 285 64 "+ ControladorRota.getInstancia().getDadosGerais().getInscricaoEstadualEmpresa().trim() + "\n" +
				// Grupo
				"T 0 0 222 81 "+ imovel.getGrupoFaturamento() + "\n" +
				"T 0 2 52 199 \n"+
				// Dados do cliente
				endereco +
				// Inscrição estadual
				"T 7 0 15 250 "+Util.formatarInscricao(imovel.getInscricao())+"\n"+
				// Codigo da Rota
				"T 7 0 315 250 "+imovel.getCodigoRota()+"\n"+
				// Sequencial da Rota
				"T 7 0 415 250 "+imovel.getSequencialRota()+"\n"+
				economias +
				// Numero do Hidrometro
				"T 7 0 48 301 "+ numeroMedidor +"\n"+
				// Data da Instalação
				"T 7 0 248 301 "+ dataInstalacao +"\n"+
				// Situacao da ligacao de Agua
				"T 7 0 446 301 "+ situacaoAgua +"\n"+
				// Situacao da ligacao de Esgoto
				"T 7 0 627 301 "+ situacaoEsgoto +"\n"+


				"T 7 1 300 350 AVISO DE DEBITO \n"+
				"T 7 0 37 390 Prezado Cliente, \n"+
				dividirLinha(7, 0, 37, 426, "Ate o presente momento nao registramos a confirmacao do Paga-mento da(s) conta(s) abaixo:", 61, 24)+
				"T 7 0 53 486 MES REFERENCIA \n"+
				"T 7 0 400 486 VENCIMENTO \n"+
				"T 7 0 697 486 VALOR(R$) \n";

		int totalLinhas = imovel.getContas().size();
		int qtdLinhasDebitoImpressas = 0;
		double subTotal = 0;

		for (int i = 0; i < totalLinhas; i++) {
			Conta conta = ((Conta) imovel.getContas().get(i));

			if (totalLinhas > 11){

				if ((totalLinhas - i) == 11){

					comando += formarLinha(7, 0, 83, 510, "OUTROS", 0, qtdLinhasDebitoImpressas * 24)+
							formarLinha(7, 0, 405, 510, "     --", 0, qtdLinhasDebitoImpressas * 24)+
							formarLinha(7, 0, 707, 510, Util.formatarDoubleParaMoedaReal(subTotal), 0, qtdLinhasDebitoImpressas * 24);
					qtdLinhasDebitoImpressas++;

				}

				if ( (totalLinhas - i) > 11 ){
					subTotal += conta.getValor();

				}else{
					comando += formarLinha(7, 0, 83, 510, Util.formatarAnoMesParaMesAno(conta.getAnoMes() + ""), 0, qtdLinhasDebitoImpressas * 24)+
							formarLinha(7, 0, 405, 510, Util.dateToString(conta.getDataVencimento()), 0, qtdLinhasDebitoImpressas * 24)+
							formarLinha(7, 0, 707, 510, Util.formatarDoubleParaMoedaReal(conta.getValor()), 0, qtdLinhasDebitoImpressas * 24);
					qtdLinhasDebitoImpressas++;

				}

			}else {
				comando += formarLinha(7, 0, 83, 510, Util.formatarAnoMesParaMesAno(conta.getAnoMes() + ""), 0, qtdLinhasDebitoImpressas * 24)+
						formarLinha(7, 0, 405, 510, Util.dateToString(conta.getDataVencimento()), 0, qtdLinhasDebitoImpressas * 24)+
						formarLinha(7, 0, 707, 510, Util.formatarDoubleParaMoedaReal(conta.getValor()), 0, qtdLinhasDebitoImpressas * 24);
				qtdLinhasDebitoImpressas++;
			}
		}

		comando += dividirLinha(7, 0, 37, (546 + (qtdLinhasDebitoImpressas*24)) , "Pertencente(s) ao imovel localizado na " + imovel.getEndereco() + ".", 61, 24)+

				dividirLinha(7, 0, 37, (606 + (qtdLinhasDebitoImpressas*24)) , "De acordo com a legislacao em vigor (Lei numero 11.445/07), o nao pagamento " +
						"deste debito nos autoriza a suspender o forne-cimento de agua/esgoto para o seu imovel", 61, 24)+

				dividirLinha(7, 0, 37, (690 + (qtdLinhasDebitoImpressas*24)) , "Estamos a sua disposicao em nossas lojas de atendimento, escri-torios regionais e " +
						"no telefone 0800-7071-195, ligacao gratuita, para esclarecer qualquer duvida.", 63, 24)+

				dividirLinha(7, 0, 37, (774 + (qtdLinhasDebitoImpressas*24)) , "Caso o(s) debito(s) ja esteja(m) quitado(s), pedimos desculpas e que desconsidere este aviso.", 62, 24)+

				formarLinha(0, 2, 155, 1265, "Numero do Documento de Notificacao de Debito: ", 0, 0)+
				formarLinha(5, 0, 555, 1265, imovel.getNumeroDocumentoNotificacaoDebito(), 0, 0)+
				"FORM\n" + "PRINT ";

		return comando;
	}

	public void getDadosAvisoDebito(Imovel imovel) {

		if (imovel.getAnoMesConta().compareTo(Constantes.NULO_STRING) != 0){
			descricaoAnoMesConta = Util.retornaDescricaoAnoMes(imovel.getAnoMesConta()) + "\n";

		}else{
			descricaoAnoMesConta = Util.retornaDescricaoAnoMes(ControladorRota.getInstancia().getDadosGerais().getAnoMesFaturamento()) + "\n";
		}

		List dc = imovel.getDadosCategoria();
		List quantidadeEconomias = categoriasEconomias(dc);

		situacaoAgua = imovel.getDescricaoSitLigacaoAgua(Integer.valueOf(imovel.getSituacaoLigAgua()));
		situacaoEsgoto = imovel.getDescricaoSitLigacaoEsgoto(Integer.valueOf(imovel.getSituacaoLigEsgoto()));
		consumoAgua = imovel.getConsumoAgua();
		consumoEsgoto = imovel.getConsumoEsgoto();;

		if (imovel.getEnderecoEntrega().trim().length() == 0){
			endereco = 	formarLinha(0, 2, 52, 172, imovel.getNomeUsuario().trim(), 0, 0) + formarLinha(0, 2, 52, 199, imovel.getCpfCnpjCliente(), 0, 0) + dividirLinha(0, 2, 434, 169, imovel.getEndereco(), 40, 27);
		}else{
			endereco =	formarLinha(0, 2, 52, 172, imovel.getNomeUsuario().trim(), 0, 0) + formarLinha(0, 2, 52, 199, imovel.getCpfCnpjCliente(), 0, 0) + dividirLinha(0, 2, 434, 169, imovel.getEnderecoEntrega(), 40, 27);
		}

		for (int i = 0; i < quantidadeEconomias.size(); i++) {
			Object[] dadosCategoria = (Object[]) quantidadeEconomias.get(i);
			economias += formarLinha(0, 0, 470, 254, dadosCategoria[0] + "", i * 85, 0);
			economias += formarLinha(7, 0, 539, 250, dadosCategoria[1] + "", i * 85, 0);
		}

		medidorAgua = imovel.getMedidor(Constantes.LIGACAO_AGUA);
		medidorPoco = imovel.getMedidor(Constantes.LIGACAO_POCO);

		if (medidorAgua != null) {
			numeroMedidor = medidorAgua.getNumeroHidrometro();
			dataInstalacao = Util.dateToString(medidorAgua.getDataInstalacao());

		} else if (medidorPoco != null) {

			numeroMedidor = medidorPoco.getNumeroHidrometro();
			dataInstalacao = Util.dateToString(medidorPoco.getDataInstalacao());
		}
	}

	private static String dividirLinha(int fonte, int tamanhoFonte, int x, int y, String texto, int tamanhoLinha, int deslocarPorLinha) {
		String retorno = "";
		int contador = 0;
		int i;

		for (i = 0; i < texto.length(); i += tamanhoLinha) {
			contador += tamanhoLinha;

			if (contador > texto.length()) {
				retorno += "T " + fonte + " " + tamanhoFonte + " " + x + " " + y + " " + texto.substring(i, texto.length()).trim() + "\n";

			} else {
				retorno += "T " + fonte + " " + tamanhoFonte + " " + x + " " + y + " " + texto.substring(i, contador).trim() + "\n";
			}
			y += deslocarPorLinha;
		}

		return retorno;
	}

	private String formarLinha(int font, int tamanhoFonte, int x, int y, String texto, int adicionarColuna, int adicionarLinha) {
		return "T " + font + " " + tamanhoFonte + " " + (x + adicionarColuna) + " " + (y + adicionarLinha) + " " + texto + "\n";
	}

	/**
	 * [SB0003] - Gerar Linhas da Tarifa de Agua
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	private List gerarLinhasTarifaAgua(Consumo consumoAgua) {
		String linhas = "";
		// Verificamos se o tipo de calculo é por categoria ou por subcategoria
		boolean tipoTarifaPorCategoria = ControladorImovel.getInstancia().tipoTarifaPorCategoria(imovel);
		int qtdLinhas = 0;

		boolean tarifaUnica = true;
		ArrayList<List<TarifacaoMinima>> tarifacoesMinimasPorCategoria = imovel.getTarifacoesMinimasPorCategoria();


		// 3
		for (int i = 0; i < imovel.getDadosCategoria().size(); i++) {
			DadosCategoria dadosEconomiasSubcategorias = imovel.getDadosCategoria().get(i);
			for (List<TarifacaoMinima> tarifa : tarifacoesMinimasPorCategoria) {
				if (!tarifa.isEmpty() && imovel.getValorCreditosBolsaAgua() > 0.0 && tarifa.size() > 1)
					tarifaUnica = false;
			}
			if (dadosEconomiasSubcategorias.getFaturamentoAgua() == null) {
				continue;
			}
			qtdLinhas++;
			if (linhas.equals("")) {
				// 2
				linhas += formarLinha(7, 0, 200, 788, "AGUA", 0, qtdLinhas * 25);
				qtdLinhas++;
			}
			// 3.1
			int quantidaEconomias = 0;
			// 3.1.1
			if (!Constantes.NULO_STRING.equals(dadosEconomiasSubcategorias.getFatorEconomiaCategoria().trim())) {
				quantidaEconomias = Integer.parseInt(dadosEconomiasSubcategorias.getFatorEconomiaCategoria());
				// 3.1.2
			} else {
				quantidaEconomias = dadosEconomiasSubcategorias.getQtdEconomiasSubcategoria();
			}
			// 3.3.1
			String descricao = "";
			if (tipoTarifaPorCategoria) {
				descricao = dadosEconomiasSubcategorias.getDescricaoCategoria() + " " + quantidaEconomias + " " + "UNIDADE(S)";
				// 3.3.1.1, 3.3.1.2 e 3.3.2.2, 3.3.3
				if (descricao.length() > 40) {
					linhas += formarLinha(7, 0, 53, 788, descricao.substring(0, 40), 0, qtdLinhas * 25);
				} else {
					linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
				}
			} else {
				descricao = dadosEconomiasSubcategorias.getDescricaoAbreviadaSubcategoria() + " " + quantidaEconomias + " " + "UNIDADE(S)";
				// 3.3.2.1, 3.3.1.2 e 3.3.2.2, 3.3.3
				if (descricao.length() > 40) {
					linhas += formarLinha(7, 0, 53, 788, descricao.substring(0, 40), 0, qtdLinhas * 25);
				} else {
					linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
				}
			}
			int consumoMinimo = 0;
			if (imovel.getConsumoMinAgua() > imovel.getConsumoMinimoImovel()) {
				consumoMinimo = imovel.getConsumoMinAgua();
			} else {
				consumoMinimo = imovel.getConsumoMinimoImovel();
			}
			// 3.4
			if (consumoAgua == null && dadosEconomiasSubcategorias.getFaturamentoAgua() != null && dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoFaturado() <= consumoMinimo) {
				qtdLinhas++;
				descricao = "";
				// 3.4.2
				descricao = "TARIFA MINIMA " + Util.formatarDoubleParaMoedaReal(dadosEconomiasSubcategorias.getFaturamentoAgua().getValorTarifaMinima() / quantidaEconomias) + " POR UNIDADE ";
				linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
				descricao = consumoMinimo + " m3";
				// 3.4.3
				linhas += formarLinha(7, 0, 571, 788, descricao, 0, qtdLinhas * 25);
				// 3.4.4
				linhas += formarLinha(7, 0, 697, 788, Util.formatarDoubleParaMoedaReal(dadosEconomiasSubcategorias.getFaturamentoAgua().getValorTarifaMinima()), 0, qtdLinhas * 25);
				// 3.5
			} else {
				// 3.5.1
				System.out.println("dadosEconomiasSubcategorias.getFaturamentoAgua(): " + dadosEconomiasSubcategorias.getFaturamentoAgua());
				System.out.println("dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas(): " + dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas());
				System.out.println("dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas().size(): " + dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas().size());
				if (dadosEconomiasSubcategorias.getFaturamentoAgua() != null && dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas() != null && dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas().size() > 0 && tarifaUnica == true) {
					qtdLinhas++;
					// 3.5.1.1
					descricao = "ATE " + ((int) dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoMinimo() / quantidaEconomias) + " m3 - " + Util.formatarDoubleParaMoedaReal(dadosEconomiasSubcategorias.getFaturamentoAgua().getValorTarifaMinima() / quantidaEconomias)
							+ " POR UNIDADE";
					linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
					// 3.5.1.2.3
					linhas += formarLinha(7, 0, 571, 788, dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoMinimo() + " m3", 0, qtdLinhas * 25);
					// 3.5.1.2.4
					linhas += formarLinha(7, 0, 697, 788, Util.formatarDoubleParaMoedaReal(dadosEconomiasSubcategorias.getFaturamentoAgua().getValorTarifaMinima()), 0, qtdLinhas * 25);
					// 3.5.1.2.5
					for (int j = 0; j < dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas().size(); j++) {
						qtdLinhas++;
						// 3.5.1.2.5
						DadosFaturamentoFaixa faixa = (DadosFaturamentoFaixa) dadosEconomiasSubcategorias.getFaturamentoAgua().getFaixas().get(j);
						// 3.5.1.2.5.1
						if (faixa.getLimiteFinalConsumo() == Constantes.LIMITE_SUPERIOR_FAIXA_FINAL) {
							// 3.5.1.2.5.1.2.1, 3.5.1.2.5.1.2.2,
							// 3.5.1.2.5.1.2.3, 3.5.1.2.5.1.2.4,
							// 3.5.1.2.5.1.2.5
							descricao = "ACIMA DE " + (faixa.getLimiteInicialConsumo() - 1) + " m3 - R$ " + Util.formatarDoubleParaMoedaReal(faixa.getValorTarifa()) + " POR m3";
							linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
							// 3.5.1.2.5.1.3.1
							linhas += formarLinha(7, 0, 571, 788, faixa.getConsumoFaturado() * quantidaEconomias + " m3 ", 0, qtdLinhas * 25);
							// 3.5.1.2.5.1.3.2
							// 3.5.1.2.5.1.4
							linhas += formarLinha(7, 0, 697, 788, Util.formatarDoubleParaMoedaReal(faixa.getValorFaturado() * quantidaEconomias), 0, qtdLinhas * 25);
							// 3.5.1.2.5.2
						} else {
							// 3.5.1.2.5.2.2.1, 3.5.1.2.5.2.2.2,
							// 3.5.1.2.5.2.2.3, 3.5.1.2.5.2.2.4,
							// 3.5.1.2.5.2.2.5, 3.5.1.2.5.2.2.6
							descricao = faixa.getLimiteInicialConsumo() + " m3 A " + faixa.getLimiteFinalConsumo() + " m3 - R$ " + Util.formatarDoubleParaMoedaReal(faixa.getValorTarifa()) + " POR M3 ";
							// 3.5.1.2.5.2.3.1
							linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
							// 3.5.1.2.5.1.3.2
							linhas += formarLinha(7, 0, 571, 788, faixa.getConsumoFaturado() * quantidaEconomias + " m3 ", 0, qtdLinhas * 25);
							// 3.5.1.2.5.1.4.1
							linhas += formarLinha(7, 0, 697, 788, Util.formatarDoubleParaMoedaReal(faixa.getValorFaturado() * quantidaEconomias), 0, qtdLinhas * 25);
						}
					}
					//			}
				} else {
					if (dadosEconomiasSubcategorias.getFaturamentoAgua() != null) {
						qtdLinhas++;
						descricao = "CONSUMO DE AGUA";
						// 3.5.1.1.2.1
						linhas += formarLinha(7, 0, 53, 788, descricao, 0, qtdLinhas * 25);
						linhas += formarLinha(7, 0, 571, 788, ((int) dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoFaturado()) + " m3", 0, qtdLinhas * 25);
						// 3.5.1.1.2.2
						linhas += formarLinha(7, 0, 697, 788, Util.formatarDoubleParaMoedaReal(dadosEconomiasSubcategorias.getFaturamentoAgua().getValorFaturado()), 0, qtdLinhas * 25);
					}
				}
			}
		}

		List retornar = new ArrayList();
		retornar.add(linhas);
		retornar.add(new Integer(qtdLinhas));
		return retornar;
	}

	/**
	 * [SB0004] - Gerar Linhas da Tarifa de Esgoto
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	@SuppressWarnings("rawtypes")
	private List gerarLinhasTarifaPoco() {
		List retorno = new ArrayList();
		// Os dados estão dividos em 3 partes
		// Descricao, de indice 0
		// Consumo, de indice 1
		// Valor, de indice 2
		String[] dados = new String[3];
		double valorEsgoto = 0d;
		int consumoAgua = 0;
		int consumoEsgoto = 0;
		double valorAgua = 0d;
		// 3
		if (imovel.getDadosCategoria() != null) {
			for (int i = 0; i < imovel.getDadosCategoria().size(); i++) {
				DadosCategoria dadosEconomiasSubcategorias = imovel.getDadosCategoria().get(i);
				// 1
				if (dadosEconomiasSubcategorias.getFaturamentoEsgoto() != null && dadosEconomiasSubcategorias.getFaturamentoEsgoto().getValorFaturado() != Constantes.NULO_DOUBLE) {
					valorEsgoto += dadosEconomiasSubcategorias.getFaturamentoEsgoto().getValorFaturado();
				}
				if (dadosEconomiasSubcategorias.getFaturamentoEsgoto() != null && dadosEconomiasSubcategorias.getFaturamentoEsgoto().getConsumoFaturado() != Constantes.NULO_INT) {
					consumoEsgoto += dadosEconomiasSubcategorias.getFaturamentoEsgoto().getConsumoFaturado();
				}
				if (dadosEconomiasSubcategorias.getFaturamentoAgua() != null) {
					if (dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoFaturado() != Constantes.NULO_INT) {
						consumoAgua += dadosEconomiasSubcategorias.getFaturamentoAgua().getConsumoFaturado();
					}
					if (dadosEconomiasSubcategorias.getFaturamentoAgua().getValorFaturado() != Constantes.NULO_DOUBLE) {
						valorAgua += dadosEconomiasSubcategorias.getFaturamentoAgua().getValorFaturado();
					}
				}
//				if(dadosEconomiasSubcategorias.getCodigoCategoria() == DadosCategoria.RESIDENCIAL && imovel.getIndcFaturamentoAgua() == ControladorImovel.SIM
//						&& imovel.getIndcFaturamentoEsgoto() == ControladorImovel.SIM && imovel.getValorCreditosBolsaAgua() > 0.0 && imovel.getValorEsgoto() == 0){
//					valorEsgoto += Util.arredondar(imovel.getValorAgua() * 0.6, 2);
//				}
			}
			dados = new String[3];
			if(imovel.getValorCreditosBolsaAgua() > 0.0){
				if (valorEsgoto != 0) {
					// 1.2.1
					dados[0] = "ESGOTO ";
					// 1.2.3
					dados[0] += Util.formatarDoubleParaMoedaReal(imovel.getPercentCobrancaEsgoto());
					// 1.2.3
					dados[0] += " % DO VALOR DE AGUA";
					// 1.4
					dados[2] = Util.formatarDoubleParaMoedaReal(valorEsgoto);
					retorno.add(dados);
				}
			} else if (consumoAgua == consumoEsgoto && valorAgua != 0) {
				if (valorEsgoto != 0) {
					// 1.2.1
					dados[0] = "ESGOTO ";
					// 1.2.3
					dados[0] += Util.formatarDoubleParaMoedaReal(imovel.getPercentCobrancaEsgoto());
					// 1.2.3
					dados[0] += " % DO VALOR DE AGUA";
					// 1.4
					dados[2] = Util.formatarDoubleParaMoedaReal(valorEsgoto);
					retorno.add(dados);
				}
			} else {
				if (valorEsgoto != 0) {
					// 1.2.1
					dados[0] = "ESGOTO ";
					// 1.3.1
					dados[1] = consumoEsgoto + "";
					// 1.3.2
					dados[1] += " m3";
					// 1.4
					dados[2] = Util.formatarDoubleParaMoedaReal(valorEsgoto);
					retorno.add(dados);
				}
			}
		}
		return retorno;
	}

	/**
	 * [SB0005] - Gerar Linhas da Debitos Cobrados
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	private List gerarLinhasRateioAguaEsgotoCobrados() {
		List retorno = new ArrayList();
		// Os dados estão dividos em 2 partes:
		// Descricao, de indice 0
		// Valor, de indice 2
		String[] dados = new String[2];

		// Valor do rateio de Água
		if (imovel.getConsumoAgua() != null && imovel.getValorRateioAgua() > 0) {

			dados = new String[2];
			dados[0] = "RATEIO DE AGUA DO CONDOMINIO";
			dados[1] = Util.formatarDoubleParaMoedaReal(imovel.getValorRateioAgua());
			retorno.add(dados);
		}

		// Valor do rateio de Esgoto
		if (imovel.getConsumoEsgoto() != null && imovel.getValorRateioEsgoto() > 0) {

			dados = new String[2];
			dados[0] = "RATEIO DE ESGOTO DO CONDOMINIO";
			dados[1] = Util.formatarDoubleParaMoedaReal(imovel.getValorRateioEsgoto());
			retorno.add(dados);
		}
		return retorno;
	}

	private List categoriasEconomias(List regsTipo2) {
		List retorno = new ArrayList();
		int quantidadeEconomiasResidencial = 0;
		int quantidadeEconomiasComercial = 0;
		int quantidadeEconomiasIndustrial = 0;
		int quantidadeEconomiasPublico = 0;
		String descricaoResidencial = "";
		String descricaoComercial = "";
		String descricaoIndustrial = "";
		String descricaoPublico = "";
		for (int i = 0; i < regsTipo2.size(); i++) {
			DadosCategoria categoria = (DadosCategoria) regsTipo2.get(i);
			String descricaoCategoria = categoria.getDescricaoCategoria();
			if (descricaoCategoria.length() > 8) {
				descricaoCategoria = descricaoCategoria.substring(0, 8);
			}
			switch (categoria.getCodigoCategoria()) {
				case DadosCategoria.RESIDENCIAL:
					quantidadeEconomiasResidencial += categoria.getQtdEconomiasSubcategoria();
					descricaoResidencial = descricaoCategoria;
					break;
				case DadosCategoria.COMERCIAL:
					quantidadeEconomiasComercial += categoria.getQtdEconomiasSubcategoria();
					descricaoComercial = descricaoCategoria;
					break;
				case DadosCategoria.INDUSTRIAL:
					quantidadeEconomiasIndustrial += categoria.getQtdEconomiasSubcategoria();
					descricaoIndustrial = descricaoCategoria;
					break;
				case DadosCategoria.PUBLICO:
					quantidadeEconomiasPublico += categoria.getQtdEconomiasSubcategoria();
					descricaoPublico = descricaoCategoria;
					break;
			}
		}
		if (quantidadeEconomiasResidencial > 0) {
			Object[] dadosResidencial = new Object[2];
			dadosResidencial[0] = descricaoResidencial;
			dadosResidencial[1] = new Integer(quantidadeEconomiasResidencial);
			retorno.add(dadosResidencial);
		}
		if (quantidadeEconomiasComercial > 0) {
			Object[] dadosComercial = new Object[2];
			dadosComercial[0] = descricaoComercial;
			dadosComercial[1] = new Integer(quantidadeEconomiasComercial);
			retorno.add(dadosComercial);
		}
		if (quantidadeEconomiasIndustrial > 0) {
			Object[] dadosIndustrial = new Object[2];
			dadosIndustrial[0] = descricaoIndustrial;
			dadosIndustrial[1] = new Integer(quantidadeEconomiasIndustrial);
			retorno.add(dadosIndustrial);
		}
		if (quantidadeEconomiasPublico > 0) {
			Object[] dadosPublico = new Object[2];
			dadosPublico[0] = descricaoPublico;
			dadosPublico[1] = new Integer(quantidadeEconomiasPublico);
			retorno.add(dadosPublico);
		}
		return retorno;
	}

	private static int retornaIndicadorDiscriminar(int quantidadeMaximaLinhas, int quantidadeLinhasAtual, char servicos) {
		int indicadorDiscriminarDescricao = 1;
		int linhasRestantesDescricao = 0;
		switch (servicos) {
			case 'd':
				// linhas que ainda aparecerão depois do débio (crédito e imposto)
				// linhas de crédito
				if (imovel.getCreditos(Constantes.SIM).size() > 0) {
					linhasRestantesDescricao = linhasRestantesDescricao + 1;
				}
				// linhas de imposto
				if (imovel.getImpostos().size() > 0) {
					linhasRestantesDescricao = linhasRestantesDescricao + 2;
				}
				// linhas de débito
				if (imovel.getDebitos(Constantes.SIM).size() > 0) {
					// linhasRestantesDescricao = linhasRestantesDescricao + 1;
					int limiteDescriminar = quantidadeMaximaLinhas - quantidadeLinhasAtual - linhasRestantesDescricao;
					int quantidadeDebitos = imovel.getDebitos(Constantes.SIM).size();
					if (quantidadeDebitos > limiteDescriminar) {
						indicadorDiscriminarDescricao = 2;
					}
				}
				break;
			case 'c':
				// linhas que ainda aparecerão depois do débio (crédito e imposto)
				// linhas de imposto
				if (imovel.getImpostos().size() > 0) {
					linhasRestantesDescricao = linhasRestantesDescricao + 2;
				}
				// linhas de credito
				if (imovel.getCreditos(Constantes.SIM).size() > 0) {
					// linhasRestantesDescricao = linhasRestantesDescricao + 1;
					int limiteDescriminar = quantidadeMaximaLinhas - quantidadeLinhasAtual - linhasRestantesDescricao;
					int quantidadeCreditos = imovel.getCreditos(Constantes.SIM).size();
					if (quantidadeCreditos > limiteDescriminar) {
						indicadorDiscriminarDescricao = 2;
					}
				}
				break;
		}
		return indicadorDiscriminarDescricao;
	}

	/**
	 * [SB0005] - Gerar Linhas da Debitos Cobrados
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	private List gerarLinhasDebitosCobrados(int indicadorDiscriminarDescricao) {
		List retorno = new ArrayList();
		// Os dados estão dividos em 3 partes
		// Descricao, de indice 0
		// Consumo, de indice 1
		// Valor, de indice 2
		String[] dados = new String[3];
		// 3
		if (imovel.getDebitos(Constantes.SIM).size() > 0) {
			// caso seja para discriminar os dados dos débitos
			if (indicadorDiscriminarDescricao == 1) {
				for (int i = 0; i < imovel.getDebitos(Constantes.SIM).size(); i++) {
					dados = new String[3];
					Debito dadosDebitosCobrados = imovel.getDebitos(Constantes.SIM).get(i);
					// 1.1.2
					dados[0] = dadosDebitosCobrados.getDescricao();
					// 1.1.3
					dados[2] = Util.formatarDoubleParaMoedaReal(dadosDebitosCobrados.getValor());
					retorno.add(dados);
				}
			} else {
				double soma = 0d;
				for (int i = 0; i < imovel.getDebitos(Constantes.SIM).size(); i++) {
					Debito dadosDebitosCobrados = imovel.getDebitos(Constantes.SIM).get(i);
					soma += dadosDebitosCobrados.getValor();
				}
				dados = new String[3];
				// 1.1.2
				dados[0] = "DEBITOS";
				// 1.1.3
				dados[2] = Util.formatarDoubleParaMoedaReal(soma);
				retorno.add(dados);
			}
		}
		return retorno;
	}

	/**
	 * [SB0006] - Gerar Linhas de Creditos Realizados
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	private List gerarLinhasCreditosRealizados(int indicadorDiscriminarDescricao) {
		List retorno = new ArrayList();
		// Os dados estão dividos em 3 partes
		// Descricao, de indice 0
		// Consumo, de indice 1
		// Valor, de indice 2
		String[] dados = new String[3];
		// 3
		if (imovel.getCreditos(Constantes.SIM).size() > 0) {
			// caso seja para discriminar os dados dos créditos
			if (indicadorDiscriminarDescricao == 1) {
				// caso o valor do crédito seja maior que o valor da conta sem o
				// crédito
				double valorContaSemCreditos = 0d;
				double valorContaResidual = 0d;
				boolean valorCreditoMaiorValorConta = false;
				boolean naoEmitirMaisCreditos = false;
				if (imovel.getValorResidualCredito() != 0d) {
					valorContaSemCreditos = imovel.getValorContaSemCreditos();
					valorCreditoMaiorValorConta = true;
				}
				for (int i = 0; i < imovel.getCreditos(Constantes.SIM).size(); i++) {
					if (!imovel.isCreditoBolsaAgua(i)){

						Credito dadosCreditosRealizado = imovel.getCreditos(Constantes.SIM).get(i);
					// caso o valor dos créditos n seja maior que o valor da
					// conta sem os créditos

					/**
					 * Foi excluida condicao de maximo consumo para bonus social
					 */
					//Verificar se o imovel deve ou nao considerar Bonus Social.
//	    	if( ( (imovel.getCreditos(Constantes.SIM).get(i))).getCodigo().equalsIgnoreCase(Imovel.CODIGO_BONUS_SOCIAL)  &&
//		    		Integer.parseInt(imovel.getCodigoPerfil()) == Imovel.PERFIL_BONUS_SOCIAL &&
//		    		imovel.getConsumoAgua() != null &&
//		    		imovel.getConsumoAgua().getConsumoCobradoMes() > 10 ){
//
//		    		continue;
//	    	}

					if (!valorCreditoMaiorValorConta) {
						dados = new String[3];
						// 1.1.2
						dados[0] = dadosCreditosRealizado.getDescricao();
						// 1.1.3
						dados[2] = Util.formatarDoubleParaMoedaReal(dadosCreditosRealizado.getValor());
						retorno.add(dados);

					}
					// //caso o valor dos créditos seja maior que o valor das
					// contas sem os créditos
					else {
						if (!naoEmitirMaisCreditos) {
							double valorCredito = dadosCreditosRealizado.getValor();
							valorContaResidual = valorContaSemCreditos - valorCredito;
							// emite as créditos até o valor dos creditos ser
							// menor que o valor da conta
							if (valorContaResidual < 0d) {
//				    	valorContaResidual = valorContaResidual * -1;
								naoEmitirMaisCreditos = true;

								dados = new String[3];
								// 1.1.2
								dados[0] = dadosCreditosRealizado.getDescricao();
								// 1.1.3
								//				    dados[2] = Util.formatarDoubleParaMoedaReal(valorCredito);
								dados[2] = Util.formatarDoubleParaMoedaReal(valorContaSemCreditos);

								retorno.add(dados);

							} else {

								valorContaSemCreditos = valorContaSemCreditos - valorCredito;

								dados = new String[3];
								// 1.1.2
								dados[0] = dadosCreditosRealizado.getDescricao();
								// 1.1.3
								dados[2] = Util.formatarDoubleParaMoedaReal(valorCredito);
								//	dados[2] = Util.formatarDoubleParaMoedaReal(valorContaSemCreditos);

								retorno.add(dados);
							}
						}
					}
				}
			}
			} else {
				double soma = imovel.getValorCreditos();
				// for ( int i = 0; i < imovel.getRegistros5().size(); i++ ){
				//
				// RegistroDescricaoValor dadosCreditosRealizado = (
				// RegistroDescricaoValor ) imovel.getRegistros5().elementAt( i
				// );
				// soma += dadosCreditosRealizado.getValor();
				// }
				dados = new String[3];
				// 1.1.2
				dados[0] = "CREDITOS";
				// 1.1.3
				dados[2] = Util.formatarDoubleParaMoedaReal(soma);
				retorno.add(dados);
			}
		}
		if (imovel.getValorCreditosBolsaAgua() > 0d) {
			dados = new String[3];
			// 1.1.2
			dados[0] = " CREDITO SUBSIDIO AGUA PARA";
			// 1.1.3
			dados[2] = Util.formatarDoubleParaMoedaReal(imovel.getValorCreditosBolsaAgua());

			retorno.add(dados);
		}

		return retorno;
	}

	/**
	 * [SB0007] - Gerar Linhas Impostos Retidos
	 *
	 * @return Os dados estão dividos em 3 partes Descricao, de indice 0
	 *         Consumo, de indice 1 Valor, de indice 2
	 */
	private List gerarLinhasImpostosRetidos() {
		List retorno = new ArrayList();
		// Os dados estão dividos em 3 partes
		// Descricao, de indice 0
		// Consumo, de indice 1
		// Valor, de indice 2
		String[] dados = new String[3];
		// 3
		if (imovel.getImpostos().size() > 0) {
			String dadosImposto = "";
			for (int i = 0; i < imovel.getImpostos().size(); i++) {
				Imposto imoReg6 = imovel.getImpostos().get(i);
				String descricaoImposto = imoReg6.getDescricaoImposto();
				String percentualAliquota = Util.formatarDoubleParaMoedaReal(imoReg6.getPercentualAlicota());
				dadosImposto += descricaoImposto + "-" + percentualAliquota + "% ";
			}
			dados = new String[3];
			// 1.1.2
			dados[0] = "DED. IMPOSTOS LEI FEDERAL N.9430 DE 27/12/1996";
			// 1.1.3
			dados[2] = Util.formatarDoubleParaMoedaReal(imovel.getValorImpostos());
			retorno.add(dados);
			dados = new String[3];
			// 1.1.2
			dados[0] = dadosImposto;
			retorno.add(dados);
		}
		return retorno;
	}

	private String gerarLinhasAliquotasImpostos(double valorConta) {
		String linha = "";
		int coluna = 30;

		String descricaoImposto = ControladorRota.getInstancia().getDadosGerais().getDescricaoAliquotaImposto();
		double aliquotaImposto = ControladorRota.getInstancia().getDadosGerais().getValorAliquotaImposto().doubleValue();

		double valorImposto = valorConta - (valorConta - (valorConta * aliquotaImposto/100));

		// Linha do cabecalho
		linha += formarLinha(0, 2, 140, 1100, "Tributos", coluna, 0);
		linha += formarLinha(0, 2, 340, 1100, "(%)", coluna, 0);
		linha += formarLinha(0, 2, 400, 1100, "Base calculo", coluna, 0);
		linha += formarLinha(0, 2, 520, 1100, "Valor (R$)", coluna, 0);
		// Linha dos valores
		linha += formarLinha(0, 2, 140, 1125, descricaoImposto, coluna, 0);
		linha += formarLinha(0, 2, 340, 1125, String.valueOf(aliquotaImposto).replace('.', ','), coluna, 0);
		linha += formarLinha(0, 2, 400, 1125, String.format("R$%.2f", valorConta), coluna, 0); // Valor conta
		linha += formarLinha(0, 2, 520, 1125, String.format("R$%.2f", valorImposto), coluna, 0); // Valor imposto

		if(ControladorRota.getInstancia().getDadosGerais().getValorAliquotaAgReguladora().doubleValue() != Constantes.NULO_DOUBLE) {

			String descricaoImpostoAgReguladora = ControladorRota.getInstancia().getDadosGerais().getDescricaoAliquotaAgReguladora();
			double aliquotaImpostoAgReguladora = ControladorRota.getInstancia().getDadosGerais().getValorAliquotaAgReguladora().doubleValue();

			double valorImpostoAgReguladora = valorConta - (valorConta - (valorConta * aliquotaImpostoAgReguladora/100));

			// Linha dos valores da Agencia Reguladora
			linha += formarLinha(0, 2, 140, 1145, descricaoImpostoAgReguladora, coluna, 0);
			linha += formarLinha(0, 2, 340, 1145, String.valueOf(aliquotaImpostoAgReguladora).replace('.', ','), coluna, 0);
			linha += formarLinha(0, 2, 400, 1145, String.format("R$%.2f", valorConta), coluna, 0); // Valor conta Agencia Reguladora
			linha += formarLinha(0, 2, 520, 1145, String.format("R$%.2f", valorImpostoAgReguladora), coluna, 0); // Valor imposto Agencia Reguladora
		}


		return linha;
	}

	public String gerarLinhaTelefoneAgenciaReguladora() {
		String linha = formarLinha(0, 2, 243, 100, String.format("Ag. reguladora (%s)", Constantes.nomeAgenciaReguladora), 0, 0);
		linha += formarLinha(0, 2, 243, 120, String.format("Telefone: %s", Constantes.numeroAgenciaReguladora), 0, 0);
		linha += formarLinha(0, 2, 243, 140, String.format("Email: %s", Constantes.emailAgenciaReguladora), 0, 0);

		return linha;
	}

	public String getCpfCnpjUsuario() {
		String linha = "";
		if (imovel.getCpfCnpjCliente().trim().length() > 11) {
			linha = "T 7 0 516 2897 CNPJ:" + imovel.getCpfCnpjCliente() + "\n";
		} else if (!imovel.getCpfCnpjCliente().trim().equals("")){
			linha = "T 7 0 516 2897 CPF:" + imovel.getCpfCnpjCliente() + "\n";
		}
		return linha;
	}

	public String getNossoNumero() {
		// G.05.1 - Identificação do convenio
		String identificacaoEmpresa = String.valueOf(imovel.getCodigoConvenio());
		identificacaoEmpresa = Util.adicionarZerosEsquerdaNumero(7, identificacaoEmpresa);

		// G.05.1 - Id tipo documento
		String idTipoDocumentoFichaCompensacao = "1";

		String numeroConta = String.valueOf(imovel.getNumeroConta());
		numeroConta = Util.adicionarZerosEsquerdaNumero(9, numeroConta);

		// Numero sem DV
		String nossoNumeroSemDv = "";
		nossoNumeroSemDv = identificacaoEmpresa + idTipoDocumentoFichaCompensacao + numeroConta;

		return nossoNumeroSemDv;
	}

}