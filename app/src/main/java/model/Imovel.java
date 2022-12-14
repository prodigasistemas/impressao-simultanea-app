package model;

import static model.Credito.CRED_BOLSA_AGUA;

import helper.EfetuarRateioConsumoHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import util.Constantes;
import util.LogUtil;
import util.Util;
import business.ControladorConta;
import business.ControladorRota;

public class Imovel {


	private static String logType = "";

	public static final String CODIGO_BONUS_SOCIAL = "11";

	public static final int PERFIL_GRANDE = 1;
	public static final int PERFIL_GRANDE_MES = 2;
	public static final int PERFIL_ESPECIAL = 3;
	public static final int PERFIL_BONUS_SOCIAL = 4;
	public static final int PERFIL_NORMAL = 5;
	public static final int PERFIL_CORPORATIVO = 6;
	public static final int PERFIL_GOVERNO_METROPOLITANO = 7;
	public static final int PERFIL_GOVERNO_INTERIOR = 8;
	public static final int PERFIL_CONDOMINIAL = 9;
	public static final int PERFIL_COLABORADOR = 10;
	public static final int PERFIL_BOLSA_AGUA = 11;

	// Situação da ligação de Água
	public final static int POTENCIAL = 1;
	public final static int FACTIVEL = 2;
	public final static int LIGADO = 3;
	public final static int EM_FISCALIZACAO = 4;
	public final static int CORTADO = 5;
	public final static int SUPRIMIDO = 6;
	public final static int SUPR_PARC = 7;
	public final static int SUPR_PARC_PEDIDO = 8;
	public final static int EM_CANCELAMENTO = 9;

	// Situação da ligação de Esgoto
	public final static int LIG_FORA_USO = 5;
	public final static int TAMPONADO = 6;
	public final static int CONVERSAO = 9;

	private long id;
	private int matricula;
	private String nomeGerenciaRegional;
	private String nomeEscritorio;
	private String nomeUsuario;
	private Date dataVencimento;
	private Date dataValidadeConta;
	private String inscricao;
	private String localidade;
	private String setor;
	private String quadra;
	private String lote;
	private String sublote;
	private String endereco;
	private String anoMesConta;
	private int digitoVerificadorConta;
	private String codigoResponsavel;
	private String nomeResponsavel;
	private String enderecoEntrega;
	private String situacaoLigAgua;
	private String situacaoLigEsgoto;
	private String descricaoBanco;
	private String codigoAgencia;
	private int matriculaCondominio;
	private int indcCondominio;
	private String codigoPerfil;
	private int consumoMedio;
	private int indcFaturamentoAgua;
	private int indcFaturamentoEsgoto;
	private int indcEmissaoConta;
	private int consumoMinAgua;
	private int consumoMinEsgoto;
	private double percentColetaEsgoto;
	private double percentCobrancaEsgoto;
	private int tipoPoco;
	private int codigoTarifa;
	private int consumoEstouro;
	private int altoConsumo;
	private int baixoConsumo;
	private double fatorMultEstouro;
	private double fatorMultMediaAltoConsumo;
	private double percentBaixoConsumo;
	private int consumoMaximo;
	private int grupoFaturamento;
	private int codigoRota;
	private int numeroConta;
	private int tipoCalculoTarifa;
	private String enderecoAtendimento;
	private String telefoneLocalidadeDDD;
	private int sequencialRota;
	private String mensagemConta1;
	private String mensagemConta2;
	private String mensagemConta3;
	private String mensagemQuitacaoAnual;
	private int consumoMinimoImovel;
	private int consumoMinimoImovelNaoMedido;
	private String numeroDocumentoNotificacaoDebito;
	private String numeroCodigoBarraNotificacaoDebito;
	private String cpfCnpjCliente;
	private Date dataLeituraAnteriorNaoMedido;
	private short indicadorAbastecimentoAgua;
	private short indicadorImovelSazonal;
	private int indicadorParalizarFaturamentoAgua = Constantes.NAO;
	private int indicadorParalizarFaturamentoEsgoto = Constantes.NAO;
	private int opcaoDebitoAutomatico = Constantes.NULO_INT;
	private double percentualAlternativoEsgoto;
	private int consumoPercentualAlternativoEsgoto;
	private Date dataEmissaoDocumento;
	private int enviarContaFisica;
	private String codigoConvenio;

	// ================= Estao no banco, mas nao estao na rota (.txt)
	// ===================
	private int quantidadeContasImpressas = 0;
	private int contagemValidacaoAgua;
	private int contagemValidacaoPoco;

	private int leituraGravadaAnterior;
	private int anormalidadeGravadaAnterior;
	private Date dataImpressaoNaoMedido;
	private double valorResidualCredito;

	private boolean indcAdicionouDadosIniciaisHelperRateio = false;
	private double valorRateioAgua;
	private double valorRateioEsgoto;
	private int consumoRateioAgua;
	private int consumoRateioEsgoto;
	private String mensagemEstouroConsumo1;
	private String mensagemEstouroConsumo2;
	private String mensagemEstouroConsumo3;
	private int imovelStatus;
	private int imovelEnviado;
	private int indcImovelEnviado = Constantes.NAO;
	private int indcImovelImpresso = Constantes.NAO;
	private int indcGeracaoConta = Constantes.SIM;
	private double latitude = Constantes.NULO_DOUBLE;
	private double longitude = Constantes.NULO_DOUBLE;

	// ===== Nao estao no banco de dados =====
	private Consumo consumoEsgoto;
	private Consumo consumoAgua;
	private SituacaoTipo situacaoTipo;
	private EfetuarRateioConsumoHelper efetuarRateioConsumoHelper;
	private int indcImovelCalculado = Constantes.NAO;
	private int anormalidadeSemHidrometro = Constantes.NULO_INT;
	private int indcImoveldo = Constantes.NAO;
	private int sequencialRotaMarcacao = Constantes.NULO_INT;
	private int indcImoveisVisitados = 0;

	private List<DadosCategoria> dadosCategoria = new ArrayList<DadosCategoria>();
	private List<HistoricoConsumo> historicoConsumo = new ArrayList<HistoricoConsumo>();
	private List<Debito> debitos = new ArrayList<Debito>();
	private List<Credito> creditos = new ArrayList<Credito>();
	private List<Imposto> impostos = new ArrayList<Imposto>();
	private List<Conta> contas = new ArrayList<Conta>();
	private List<Medidor> medidores = new ArrayList<Medidor>();
	private List<TarifacaoMinima> tarifacoesMinimas = new ArrayList<TarifacaoMinima>();
	private List<TarifacaoComplementar> tarifacoesComplementares = new ArrayList<TarifacaoComplementar>();

	private boolean tarifacaoComplementarNulaOuZerada = false;

	public void setImovelStatus(String imovelStatus) {
		this.imovelStatus = Util.verificarNuloInt(imovelStatus);
	}

	public void setImovelStatus(int imovelStatus) {
		this.imovelStatus = imovelStatus;
	}

	public void setImovelEnviado(String imovelEnviado) {
		this.imovelEnviado = Util.verificarNuloInt(imovelEnviado);
	}

	public boolean isIndcAdicionouDadosIniciaisHelperRateio() {
		return indcAdicionouDadosIniciaisHelperRateio;
	}

	public int getIndcAdicionouDadosIniciaisHelperRateio() {

		if (indcAdicionouDadosIniciaisHelperRateio) {
			return Constantes.SIM;
		} else {
			return Constantes.NAO;
		}
	}

	public int getImovelStatus() {
		return this.imovelStatus;
	}

	public int isImovelEnviado() {
		return this.imovelEnviado;
	}

	public double getValorRateioAgua() {
		return valorRateioAgua;
	}

	public void setValorRateioAgua(double valorRateioAgua) {
		this.valorRateioAgua = valorRateioAgua;
	}

	public int getConsumoRateioAgua() {
		return consumoRateioAgua;
	}

	public void setConsumoRateioAgua(int consumoRateioAgua) {
		this.consumoRateioAgua = consumoRateioAgua;
	}

	public double getValorRateioEsgoto() {
		return valorRateioEsgoto;
	}

	public void setValorRateioEsgoto(double valorRateioEsgoto) {
		this.valorRateioEsgoto = valorRateioEsgoto;
	}

	public int getConsumoRateioEsgoto() {
		return consumoRateioEsgoto;
	}

	public int getEnviarContaFisica() {
		return enviarContaFisica;
	}

	public void setEnviarContaFisica(int enviarContaFisica) {
		this.enviarContaFisica = enviarContaFisica;
	}

	public String getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(String codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public void setConsumoRateioEsgoto(int consumoRateioEsgoto) {
		this.consumoRateioEsgoto = consumoRateioEsgoto;
	}

	public void setIndcAdicionouDadosIniciaisHelperRateio(int indcAdicionouDadosIniciaisHelperRateio) {

		if (indcAdicionouDadosIniciaisHelperRateio == Constantes.SIM) {
			this.indcAdicionouDadosIniciaisHelperRateio = true;

		} else {
			this.indcAdicionouDadosIniciaisHelperRateio = false;
		}
	}

	public void setEfetuarRateioConsumoHelper(EfetuarRateioConsumoHelper efetuarRateioConsumoHelper) {
		this.efetuarRateioConsumoHelper = efetuarRateioConsumoHelper;
	}

	public EfetuarRateioConsumoHelper getEfetuarRateioConsumoHelper() {
		return efetuarRateioConsumoHelper;
	}

	public int getIndcImovelEnviado() {
		return indcImovelEnviado;
	}

	public void setIndcImovelEnviado(int indcImovelEnviado) {
		this.indcImovelEnviado = indcImovelEnviado;
	}

	public int getIndcImovelImpresso() {
		return indcImovelImpresso;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getQuantidadeContasImpressas() {
		return quantidadeContasImpressas;
	}

	public String getNumeroDocumentoNotificacaoDebito() {
		return numeroDocumentoNotificacaoDebito;
	}

	public void setNumeroDocumentoNotificacaoDebito(String numeroDocumentoNotificacaoDebito) {
		this.numeroDocumentoNotificacaoDebito = numeroDocumentoNotificacaoDebito;
	}

	public void setNumeroCodigoBarraNotificacaoDebito(String numeroCodigoBarraNotificacaoDebito) {
		this.numeroCodigoBarraNotificacaoDebito = numeroCodigoBarraNotificacaoDebito;
	}

	public String getNumeroCodigoBarraNotificacaoDebito() {
		return numeroCodigoBarraNotificacaoDebito;
	}

	public Imovel() {
		this.matricula = Constantes.NULO_INT;
	}

	public int getIndcImoveisVisitados() {
		return indcImoveisVisitados;
	}

	public void setIndcImoveisVisitados(int indcImoveisVisitados) {
		this.indcImoveisVisitados = indcImoveisVisitados;
	}

	public int getIndcImovelCalculado() {
		return indcImovelCalculado;
	}

	public void setIndcImovelCalculado(int indcImovelCalculado) {

		this.indcImovelCalculado = indcImovelCalculado;

		if (indcImovelCalculado == Constantes.SIM) {

			if (this.isImovelAlterado()) {
				this.indcImovelEnviado = Constantes.NAO;
				this.indcImovelImpresso = Constantes.NAO;
			}

			// Caso esteja incluido como imóvel a revisitar, remove
			if (Configuracao.getInstancia().getMatriculasRevisitar() != null && !Configuracao.getInstancia().getMatriculasRevisitar().isEmpty()
					&& Configuracao.getInstancia().getMatriculasRevisitar().contains(this.getMatricula() + "")) {

				int size = Configuracao.getInstancia().getMatriculasRevisitar().size();
				size--;

				Configuracao.getInstancia().getMatriculasRevisitar().removeElement(this.getMatricula() + "");
				Configuracao.getInstancia().getMatriculasRevisitar().setSize(size);
			}

		} else {

			if (!this.isImovelCondominio()) {
				// if (
				// !Configuracao.getInstancia().getIdsImoveisPendentes().contains(
				// new Integer( id ) ) ){
				// Configuracao.getInstancia().getIdsImoveisPendentes().addElement(
				// new Integer( id ) );
				// }
				// Configuracao.getInstancia().getIdsImoveisConcluidos().removeElement(
				// new Integer( id ) );
				//
				// // Ordena os vetores
				// Util.bubbleSort(
				// Configuracao.getInstancia().getIdsImoveisConcluidos() );
				// Util.bubbleSort(
				// Configuracao.getInstancia().getIdsImoveisPendentes() );
			}
		}

		// Repositorio.salvarObjeto( Configuracao.getInstancia() );
	}

	public int getIndcGeracaoConta() {
		return indcGeracaoConta;
	}

	public void setIndcGeracaoConta(int indcGeracaoConta) {
		this.indcGeracaoConta = indcGeracaoConta;
	}

	public void setIndcImovelImpresso(int imovelImpressao) {
		this.indcImovelImpresso = imovelImpressao;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setQuantidadeContasImpressas(int quantidadeImpressao) {
		this.quantidadeContasImpressas = quantidadeImpressao;
	}

	public Consumo getConsumoAgua() {
		return consumoAgua;
	}

	public void setConsumoAguaFromDB(Consumo consumoAgua) {
		this.consumoAgua = consumoAgua;
	}

	public void setConsumoEsgotoFromDB(Consumo consumoEsgoto) {
		this.consumoEsgoto = consumoEsgoto;
	}

	public void setConsumoAgua(Consumo consumoAgua) {

		DadosRelatorio dadosRelatorio = DadosRelatorio.getInstancia();

		// anormalidade anterior do imóvel
		int anormalidadeRelatorio = Constantes.NULO_INT;
		int leituraRelatorio = Constantes.NULO_INT;
		int anormalidade = Constantes.NULO_INT;

		String stringQuadra = Util.adicionarZerosEsquerdaNumero(4, quadra);
		boolean temConsumo = true;

		if (this.isImovelCondominio()) {
			/**
			 * Caso seja um imóvel condomínio que foi alterado para leitura e
			 * anormalidade em branco; são feitas as alterações necessárias nos
			 * valores do relatório.
			 */
			if (consumoAgua == null) {
				dadosRelatorio.idsLidosRelatorio.removeElement(id);
				temConsumo = false;
				if (!dadosRelatorio.idsNaoLidosRelatorio.contains(id)) {
					dadosRelatorio.idsNaoLidosRelatorio.addElement(id);

					if (this.getMedidor(Constantes.LIGACAO_AGUA) != null) {
						leituraRelatorio = this.getMedidor(Constantes.LIGACAO_AGUA).getLeituraRelatorio();
						anormalidadeRelatorio = this.getMedidor(Constantes.LIGACAO_AGUA).getAnormalidadeRelatorio();

						/**
						 * Caso o imóvel tenha anormalidade antes da alteração,
						 * será retirado uma unidade dos lidos com anormalidade.
						 */
						if (anormalidadeRelatorio != Constantes.NULO_INT && anormalidadeRelatorio != 0) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", true, false);

						}

						/**
						 * Caso o imóvel possua anteriormente leitura, é
						 * retirado uma unidade de lidos com leitura.
						 */
						if ((anormalidadeRelatorio == Constantes.NULO_INT || anormalidadeRelatorio == 0) && leituraRelatorio != Constantes.NULO_INT) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", false, true);
						}
					}

					this.getMedidor(Constantes.LIGACAO_AGUA).setLeituraRelatorio(Constantes.NULO_INT);
					this.getMedidor(Constantes.LIGACAO_AGUA).setAnormalidadeRelatorio(Constantes.NULO_INT);

				}

			}
		}

		if (dadosRelatorio.idsNaoLidosRelatorio != null) {

			if (temConsumo && this.getIndcImovelCalculado() == Constantes.SIM) {

				dadosRelatorio.idsNaoLidosRelatorio.removeElement(id);

				if (this.getMedidor(Constantes.LIGACAO_AGUA) != null) {
					anormalidade = this.getMedidor(Constantes.LIGACAO_AGUA).getAnormalidade();
					anormalidadeRelatorio = this.getMedidor(Constantes.LIGACAO_AGUA).getAnormalidadeRelatorio();
					leituraRelatorio = this.getMedidor(Constantes.LIGACAO_AGUA).getLeituraRelatorio();
				}

				if (!dadosRelatorio.idsLidosRelatorio.contains(id)) {
					dadosRelatorio.idsLidosRelatorio.addElement(id);

					if (this.getMedidor(Constantes.LIGACAO_AGUA) != null) {

						anormalidade = this.getMedidor(Constantes.LIGACAO_AGUA).getAnormalidade();

						/**
						 * Caso o imóvel tenha sido calculado com anormalidade
						 * será inserido como lido com anormalidade nas
						 * informações do relatório
						 */
						if (anormalidade != Constantes.NULO_INT) {
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", true, false);

						} else {
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}

					} else {

						/**
						 * Caso o imóvel seja não medido, o mesmo será inserido
						 * como lido com leitura nas informações do relatório
						 */
						if (this.getMedidor(Constantes.LIGACAO_POCO) == null) {
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}
					}

				} else {

					// Caso o imovel seja lido com leitura e posteriormente seja
					// lido novamente colocando uma anormalidade
					// o mesmo deve ser retirado do relatorio como lido com
					// leitura e inserido como lido com anormalidade
					if (this.getMedidor(Constantes.LIGACAO_AGUA) != null) {

						if (anormalidade != 0 && anormalidadeRelatorio == 0) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", false, true);

							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", true, false);
						}

						// Caso o imovel seja lido com leitura e posteriormente
						// seja lido novamente colocando uma anormalidade
						// o mesmo deve ser retirado do relatorio como lido com
						// leitura e inserido como lido com anormalidade
						if (anormalidade == 0 && anormalidadeRelatorio != 0) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", true, false);

							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}

					}

				}
			}
		}

		this.consumoAgua = consumoAgua;
	}

	public Consumo getConsumoEsgoto() {
		return consumoEsgoto;
	}

	public void setConsumoEsgoto(Consumo consumoEsgoto) {

		DadosRelatorio dadosRelatorio = DadosRelatorio.getInstancia();

		int anormalidade = Constantes.NULO_INT;
		int anormalidadeRelatorio = Constantes.NULO_INT;
		int leituraRelatorio = Constantes.NULO_INT;

		String stringQuadra = Util.adicionarZerosEsquerdaNumero(4, quadra);
		boolean temConsumo = true;

		if (this.isImovelCondominio()) {
			/**
			 * Caso seja um imóvel condomínio que foi alterado para leitura e
			 * anormalidade em branco; são feitas as alterações necessárias nos
			 * valores do relatório.
			 */
			if (consumoEsgoto == null) {
				dadosRelatorio.idsLidosRelatorio.removeElement(id);
				temConsumo = false;
				if (!dadosRelatorio.idsNaoLidosRelatorio.contains(id)) {
					dadosRelatorio.idsNaoLidosRelatorio.addElement(id);

					if (this.getMedidor(Constantes.LIGACAO_POCO) != null && this.getMedidor(Constantes.LIGACAO_AGUA) == null) {
						leituraRelatorio = this.getMedidor(Constantes.LIGACAO_POCO).getLeituraRelatorio();
						anormalidadeRelatorio = this.getMedidor(Constantes.LIGACAO_POCO).getAnormalidadeRelatorio();

						/**
						 * Caso o imóvel tenha anormalidade antes da alteração,
						 * será retirado uma unidade dos lidos com anormalidade.
						 */
						if (anormalidadeRelatorio != Constantes.NULO_INT && anormalidadeRelatorio != 0) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", true, false);

						}

						/**
						 * Caso o imóvel possua anteriormente leitura, é
						 * retirado uma unidade de lidos com leitura.
						 */
						if ((anormalidadeRelatorio == Constantes.NULO_INT || anormalidadeRelatorio == 0) && leituraRelatorio != Constantes.NULO_INT) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", false, true);
						}
					}

					this.getMedidor(Constantes.LIGACAO_POCO).setLeituraRelatorio(Constantes.NULO_INT);
					this.getMedidor(Constantes.LIGACAO_POCO).setAnormalidadeRelatorio(Constantes.NULO_INT);

				}

			}
		}

		if (dadosRelatorio.idsNaoLidosRelatorio != null) {

			if (temConsumo && this.getIndcImovelCalculado() == Constantes.SIM) {

				dadosRelatorio.idsNaoLidosRelatorio.removeElement(id);

				if (this.getMedidor(Constantes.LIGACAO_POCO) != null) {
					anormalidade = this.getMedidor(Constantes.LIGACAO_POCO).getAnormalidade();
					anormalidadeRelatorio = this.getMedidor(Constantes.LIGACAO_POCO).getAnormalidadeRelatorio();
					leituraRelatorio = this.getMedidor(Constantes.LIGACAO_POCO).getLeituraRelatorio();
				}

				if (!dadosRelatorio.idsLidosRelatorio.contains(id)) {
					dadosRelatorio.idsLidosRelatorio.addElement(id);

					if (this.getMedidor(Constantes.LIGACAO_POCO) != null && this.getMedidor(Constantes.LIGACAO_AGUA) == null) {

						anormalidade = this.getMedidor(Constantes.LIGACAO_POCO).getAnormalidade();

						/**
						 * Caso o imóvel tenha sido calculado com anormalidade
						 * será inserido como lido com anormalidade nas
						 * informações do relatório
						 */
						if (anormalidade != Constantes.NULO_INT) {
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", true, false);

						} else {
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}

					} else {
						if (this.getMedidor(Constantes.LIGACAO_AGUA) == null && this.getConsumoAgua() == null) {

							/**
							 * Caso o imóvel seja não medido, o mesmo será
							 * inserido como lido com leitura nas informações do
							 * relatório
							 */
							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}
					}

				} else {

					// Caso o imovel seja lido com leitura e posteriormente seja
					// lido novamente colocando uma anormalidade
					// o mesmo deve ser retirado do relatorio como lido com
					// leitura e inserido como lido com anormalidade
					if (this.getMedidor(Constantes.LIGACAO_POCO) != null && this.getMedidor(Constantes.LIGACAO_AGUA) == null) {
						if (anormalidade != 0 && anormalidadeRelatorio == 0) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", false, true);

							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", true, false);
						}

						// Caso o imovel seja lido com leitura e posteriormente
						// seja lido novamente colocando uma anormalidade
						// o mesmo deve ser retirado do relatorio como lido com
						// leitura e inserido como lido com anormalidade
						if (anormalidade == Constantes.NULO_INT && anormalidadeRelatorio != Constantes.NULO_INT) {

							Util.inserirValoresStringRelatorioConsumoNulo("(" + stringQuadra + ")", true, false);

							Util.inserirValoresStringRelatorio("(" + stringQuadra + ")", false, true);
						}

					}

				}
			}
		}

		this.consumoEsgoto = consumoEsgoto;
	}

	public void setMatricula(String matricula) {
		this.matricula = Util.verificarNuloInt(matricula);
	}

	public void setNomeGerenciaRegional(String nomeGerenciaRegional) {
		this.nomeGerenciaRegional = Util.verificarNuloString(nomeGerenciaRegional);
	}

	public void setNomeEscritorio(String nomeEscritorio) {
		this.nomeEscritorio = Util.verificarNuloString(nomeEscritorio);
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = Util.verificarNuloString(nomeUsuario);
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = Util.getData(Util.verificarNuloString(dataVencimento));
	}

	public void setDataValidadeConta(String dataValidadeConta) {
		this.dataValidadeConta = Util.getData(Util.verificarNuloString(dataValidadeConta));
	}

	public void setInscricao(String inscricao) {
		this.inscricao = Util.verificarNuloString(inscricao.trim());

		if (this.inscricao != Constantes.NULO_STRING) {

			if (this.inscricao.length() == 17) {
				localidade = inscricao.substring(0, 3);
				setor = inscricao.substring(3, 6);
				quadra = inscricao.substring(6, 10);
				lote = inscricao.substring(10, 14);
				sublote = inscricao.substring(14, 17);
			} else if (this.inscricao.length() == 16) {
				localidade = inscricao.substring(0, 3);
				setor = inscricao.substring(3, 6);
				quadra = "0" + inscricao.substring(6, 9);
				lote = inscricao.substring(9, 13);
				sublote = inscricao.substring(13, 16);
			}
		}
	}

	public void setEndereco(String endereco) {
		this.endereco = Util.verificarNuloString(endereco);
	}

	public void setAnoMesConta(String anoMesConta) {
		this.anoMesConta = Util.verificarNuloString(anoMesConta);
	}

	public void setDigitoVerificadorConta(String digitoVerificadorConta) {
		this.digitoVerificadorConta = Util.verificarNuloInt(digitoVerificadorConta);
	}

	public void setCodigoResponsavel(String codigoResponsavel) {
		this.codigoResponsavel = Util.verificarNuloString(codigoResponsavel);
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = Util.verificarNuloString(nomeResponsavel);
	}

	public void setEnderecoEntrega(String enderecoEntrega) {
		this.enderecoEntrega = Util.verificarNuloString(enderecoEntrega);
	}

	public void setSituacaoLigAgua(String situacaoLigAgua) {
		this.situacaoLigAgua = Util.verificarNuloString(situacaoLigAgua);
	}

	public void setSituacaoLigEsgoto(String situacaoLigEsgoto) {
		this.situacaoLigEsgoto = Util.verificarNuloString(situacaoLigEsgoto);
	}

	public void setDescricaoBanco(String descricaoBanco) {
		this.descricaoBanco = Util.verificarNuloString(descricaoBanco);
	}

	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = Util.verificarNuloString(codigoAgencia);
	}

	public void setMatriculaCondominio(String matriculaCondominio) {
		this.matriculaCondominio = Util.verificarNuloInt(matriculaCondominio);
	}

	public void setIndcCondominio(String indcCondominio) {
		this.indcCondominio = Util.verificarNuloInt(indcCondominio);
	}

	public void setCodigoPerfil(String codigoPerfil) {
		this.codigoPerfil = Util.verificarNuloString(codigoPerfil);
	}

	public void setConsumoMedio(String consumoMedio) {
		this.consumoMedio = Util.verificarNuloInt(consumoMedio);
	}

	public void setIndcFaturamentoAgua(String indcFaturamentoAgua) {
		this.indcFaturamentoAgua = Util.verificarNuloInt(indcFaturamentoAgua);
	}

	public void setIndcFaturamentoEsgoto(String indcFaturamentoEsgoto) {
		this.indcFaturamentoEsgoto = Util.verificarNuloInt(indcFaturamentoEsgoto);
	}

	public void setIndcEmissaoConta(String indcEmissaoConta) {
		this.indcEmissaoConta = Util.verificarNuloInt(indcEmissaoConta);
	}

	public void setConsumoMinAgua(String consumoMinAgua) {
		this.consumoMinAgua = Util.verificarNuloInt(consumoMinAgua);
	}

	public void setConsumoMinEsgoto(String consumoMinEsgoto) {
		this.consumoMinEsgoto = Util.verificarNuloInt(consumoMinEsgoto);
	}

	public void setPercentColetaEsgoto(String percentColetaEsgoto) {
		this.percentColetaEsgoto = Util.verificarNuloDouble(percentColetaEsgoto);
	}

	public void setPercentCobrancaEsgoto(String percentCobrancaEsgoto) {
		this.percentCobrancaEsgoto = Util.verificarNuloDouble(percentCobrancaEsgoto);
	}

	public void setTipoPoco(String tipoPoco) {
		this.tipoPoco = Util.verificarNuloInt(tipoPoco);
	}

	public void setCodigoTarifa(String codigoTarifa) {
		this.codigoTarifa = Util.verificarNuloInt(codigoTarifa);
	}

	public void setConsumoEstouro(String consumoEstouro) {
		this.consumoEstouro = Util.verificarNuloInt(consumoEstouro);
	}

	public void setAltoConsumo(String altoConsumo) {
		this.altoConsumo = Util.verificarNuloInt(altoConsumo);
	}

	public void setBaixoConsumo(String baixoConsumo) {
		this.baixoConsumo = Util.verificarNuloInt(baixoConsumo);
	}

	public void setFatorMultEstouro(String fatorMultEstouro) {
		this.fatorMultEstouro = Util.verificarNuloDouble(fatorMultEstouro);
	}

	public void setFatorMultMediaAltoConsumo(String fatorMultMediaAltoConsumo) {
		this.fatorMultMediaAltoConsumo = Util.verificarNuloDouble(fatorMultMediaAltoConsumo);
	}

	public void setPercentBaixoConsumo(String percentBaixoConsumo) {
		this.percentBaixoConsumo = Util.verificarNuloDouble(percentBaixoConsumo);
	}

	public void setConsumoMaximo(String consumoMaximo) {
		this.consumoMaximo = Util.verificarNuloInt(consumoMaximo);
	}

	public void setNumeroConta(String numeroConta) {
		this.numeroConta = Util.verificarNuloInt(numeroConta);
	}

	public void setTipoCalculoTarifa(String tipoCalculoTarifa) {
		this.tipoCalculoTarifa = Util.verificarNuloInt(tipoCalculoTarifa);
	}

	public void setGrupoFaturamento(String grupoFaturamento) {
		this.grupoFaturamento = Util.verificarNuloInt(grupoFaturamento);
	}

	public void setCodigoRota(String codigoRota) {
		this.codigoRota = Util.verificarNuloInt(codigoRota);
	}

	public void setEnderecoAtendimento(String enderecoAtendimento) {
		this.enderecoAtendimento = Util.verificarNuloString(enderecoAtendimento);
	}

	public void setTelefoneLocalidadeDDD(String telefoneLocalidadeDDD) {
		this.telefoneLocalidadeDDD = Util.verificarNuloString(telefoneLocalidadeDDD);
	}

	public void setSequencialRota(String sequencialRota) {
		this.sequencialRota = Util.verificarNuloInt(sequencialRota);
	}

	public void setMensagemConta1(String mensagemConta1) {
		this.mensagemConta1 = Util.verificarNuloString(mensagemConta1);
	}

	public void setMensagemConta2(String mensagemConta2) {
		this.mensagemConta2 = Util.verificarNuloString(mensagemConta2);
	}

	public void setMensagemConta3(String mensagemConta3) {
		this.mensagemConta3 = Util.verificarNuloString(mensagemConta3);
	}

	public void setMensagemQuitacaoAnual(String mensagemQuitacaoAnual) {
		this.mensagemQuitacaoAnual = Util.verificarNuloString(mensagemQuitacaoAnual);
	}

	public void setConsumoMinimoImovel(String consumoMinimoImovel) {
		this.consumoMinimoImovel = Util.verificarNuloInt(consumoMinimoImovel);
	}

	public void setConsumoMinimoImovelNaoMedido(String consumoMinimoImovelNaoMedido) {
		this.consumoMinimoImovelNaoMedido = Util.verificarNuloInt(consumoMinimoImovelNaoMedido);
	}

	public void setIndicadorParalizarFaturamentoAgua(String indicadorParalizarFaturamentoAgua) {
		this.indicadorParalizarFaturamentoAgua = Util.verificarNuloInt(indicadorParalizarFaturamentoAgua);
	}

	public void setIndicadorParalizarFaturamentoEsgoto(String indicadorParalizarFaturamentoEsgoto) {
		this.indicadorParalizarFaturamentoEsgoto = Util.verificarNuloInt(indicadorParalizarFaturamentoEsgoto);
	}

	public String getInscricao() {
		if (inscricao.length() == 16) {
			inscricao = inscricao + " ";
		}
		return inscricao;
	}

	public String getEndereco() {
		return endereco;
	}

	public String getAnoMesConta() {
		return anoMesConta;
	}

	public int getDigitoVerificadorConta() {
		return digitoVerificadorConta;
	}

	public String getCodigoResponsavel() {
		return codigoResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public String getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public String getSituacaoLigAgua() {
		return situacaoLigAgua;
	}

	public String getSituacaoLigEsgoto() {
		return situacaoLigEsgoto;
	}

	public String getDescricaoBanco() {
		return descricaoBanco;
	}

	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	public int getMatriculaCondominio() {
		return matriculaCondominio;
	}

	public int getIndcCondominio() {
		return indcCondominio;
	}

	public String getCodigoPerfil() {
		return codigoPerfil;
	}

	public int getConsumoMedio() {
		return consumoMedio;
	}

	public int getIndcFaturamentoAgua() {
		return indcFaturamentoAgua;
	}

	public int getIndcFaturamentoEsgoto() {
		return indcFaturamentoEsgoto;
	}

	public int getIndcEmissaoConta() {
		return indcEmissaoConta;
	}

	public int getConsumoMinAgua() {
		return consumoMinAgua;
	}

	public int getConsumoMinEsgoto() {
		return consumoMinEsgoto;
	}

	public double getPercentColetaEsgoto() {
		return percentColetaEsgoto;
	}

	public double getPercentCobrancaEsgoto() {
		return percentCobrancaEsgoto;
	}

	public int getTipoPoco() {
		return tipoPoco;
	}

	public int getCodigoTarifa() {
		return codigoTarifa;
	}

	public int getConsumoEstouro() {
		return consumoEstouro;
	}

	public int getAltoConsumo() {
		return altoConsumo;
	}

	public int getBaixoConsumo() {
		return baixoConsumo;
	}

	public double getFatorMultEstouro() {
		return fatorMultEstouro;
	}

	public double getFatorMultMediaAltoConsumo() {
		return fatorMultMediaAltoConsumo;
	}

	public double getPercentBaixoConsumo() {
		return percentBaixoConsumo;
	}

	public int getConsumoMaximo() {
		return consumoMaximo;
	}

	public int getNumeroConta() {
		return numeroConta;
	}

	public int getTipoCalculoTarifa() {
		return tipoCalculoTarifa;
	}

	public int getGrupoFaturamento() {
		return grupoFaturamento;
	}

	public int getCodigoRota() {
		return codigoRota;
	}

	public String getEnderecoAtendimento() {
		return enderecoAtendimento;
	}

	public String getTelefoneLocalidadeDDD() {
		return telefoneLocalidadeDDD;
	}

	public int getSequencialRota() {
		return sequencialRota;
	}

	public String getMensagemConta1() {
		return mensagemConta1;
	}

	public String getMensagemConta2() {
		return mensagemConta2;
	}

	public String getMensagemConta3() {
		return mensagemConta3;
	}

	public String getMensagemQuitacaoAnual() {
		return mensagemQuitacaoAnual;
	}

	public int getConsumoMinimoImovel() {
		return consumoMinimoImovel;
	}

	public int getconsumoMinimoImovelNaoMedido() {
		return consumoMinimoImovelNaoMedido;
	}

	public String getMensagemEstouroConsumo1() {
		return mensagemEstouroConsumo1;
	}

	public void setMensagemEstouroConsumo1(String mensagemEstouroConsumo1) {
		this.mensagemEstouroConsumo1 = mensagemEstouroConsumo1;
	}

	public String getMensagemEstouroConsumo2() {
		return mensagemEstouroConsumo2;
	}

	public void setMensagemEstouroConsumo2(String mensagemEstouroConsumo2) {
		this.mensagemEstouroConsumo2 = mensagemEstouroConsumo2;
	}

	public String getMensagemEstouroConsumo3() {
		return mensagemEstouroConsumo3;
	}

	public void setMensagemEstouroConsumo3(String mensagemEstouroConsumo3) {
		this.mensagemEstouroConsumo3 = mensagemEstouroConsumo3;
	}

	public void adicionaRegistro5(Credito reg) {
		if (this.creditos == null) {
			this.creditos = new Vector();
		}

		// Controle do valor do Bonus Social
		if ((reg.getCodigo().equalsIgnoreCase(CODIGO_BONUS_SOCIAL)) && (reg.getValor() > 4.2)) {

			reg.setValor(String.valueOf(4.2));
		}
		this.creditos.add(reg);
	}

	public String getNumeroHidrometro(int tipoMedicao) {

		String retorno = null;

		if (this.medidores != null) {
			int tamanho = this.medidores.size();

			for (int i = 0; i < tamanho; i++) {
				Medidor medidor = (Medidor) this.medidores.get(i);
				if (medidor.getTipoMedicao() == tipoMedicao) {
					retorno = medidor.getNumeroHidrometro();
				}
			}
		}

		return retorno;
	}

	public int getQuantidadeEconomiasTotal() {
		int tamanho = this.dadosCategoria.size();
		int retorno = 0;

		for (int i = 0; i < tamanho; i++) {

			retorno = retorno + ((DadosCategoria) this.dadosCategoria.get(i)).getQtdEconomiasSubcategoria();
		}

		return retorno;
	}

	public Medidor getMedidor(int tipoMedicao) {
		Medidor retorno = null;

		if (this.medidores != null) {
			int tamanho = this.medidores.size();

			for (int i = 0; i < tamanho; i++) {

				Medidor medidor = (Medidor) this.medidores.get(i);
				if (medidor.getTipoMedicao() == tipoMedicao) {
					retorno = medidor;
				}
			}
		}

		return retorno;
	}

	public boolean isHistoricoFaturamentoEmpty() {
		boolean isEmpty = false;
		if (this.historicoConsumo == null || this.historicoConsumo.size() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	public HistoricoConsumo getHistoricoFaturamento(int anoMes) {
		HistoricoConsumo retorno = null;

		if (this.historicoConsumo != null) {
			int tamanho = this.historicoConsumo.size();
			for (int i = 0; i < tamanho; i++) {
				HistoricoConsumo historicoFaturamento = (HistoricoConsumo) this.historicoConsumo.get(i);

				int anoMesReferencia = historicoFaturamento.getAnoMesReferencia();

				if (anoMesReferencia == anoMes) {
					retorno = historicoFaturamento;
				}
			}
		}
		return retorno;
	}

	public HistoricoConsumo getHistoricoConsumo(int anoMes, int idAnormalidadeConsumo) {
		int tamanho = 0;

		if (this.historicoConsumo != null && !this.historicoConsumo.isEmpty()) {
			tamanho = this.historicoConsumo.size();
		}

		HistoricoConsumo retorno = null;

		for (int i = 0; i < tamanho; i++) {
			HistoricoConsumo reg3 = (HistoricoConsumo) this.historicoConsumo.get(i);

			int anoMesReferencia = reg3.getAnoMesReferencia();

			if (anoMes == anoMesReferencia && reg3.getAnormalidadeConsumo() == idAnormalidadeConsumo) {
				retorno = reg3;
			}
		}

		return retorno;
	}

	public int getQuantidadeEconomias(int codigoCategoria, String codigoSubcategoria) {
		int tamanho = this.dadosCategoria.size();
		int retorno = 0;
		DadosCategoria dadosCategoria = null;

		for (int i = 0; i < tamanho; i++) {
			dadosCategoria = (DadosCategoria) this.dadosCategoria.get(i);

			if (dadosCategoria.getCodigoCategoria() == codigoCategoria && dadosCategoria.getCodigoSubcategoria().equals(codigoSubcategoria)) {

				i = tamanho;
				retorno = dadosCategoria.getQtdEconomiasSubcategoria();
			}
		}

		return retorno;
	}

	public boolean isLeituraRealizada() {
		boolean ret = true;
		int size = this.medidores.size();

		for (int i = 0; i < size && ret; i++) {
			Medidor medidor = (Medidor) this.medidores.get(i);

			if (medidor.getAnormalidade() == Constantes.NULO_INT && medidor.getLeitura() == Constantes.LEITURA_INVALIDA) {
				ret = false;
			}
		}

		return ret;
	}

	public int getQuadra() {
		return Integer.parseInt(quadra);
	}

	public List<Medidor> getMedidores() {
		return medidores;
	}

	public Medidor getMedidorPorTipoMedicao(int tipoMedicao) {
		for (Medidor m : medidores) {
			if (m.getTipoMedicao() == tipoMedicao)
				return m;
		}

		return null;
	}

	public List<DadosCategoria> getDadosCategoria() {
		return dadosCategoria;
	}

	public SituacaoTipo getSituacaoTipo() {
		return situacaoTipo;
	}

	public List<HistoricoConsumo> getHistoricosConsumo() {
		return historicoConsumo;
	}

	public List<Debito> getDebitos(int indcUso, int indicadorIncluirCalculoImposto) {

		if (debitos != null) {
			ArrayList<Debito> tempDebito = new ArrayList<Debito>();

			for (int i = 0; i < debitos.size(); i++) {

				Debito debito = (Debito) debitos.get(i);

				if (debito.getIndcUso() == indcUso && debito.getIndicadorIncluirCalculoImposto() == indicadorIncluirCalculoImposto) {
					tempDebito.add(debito);
				}
			}

			return tempDebito;
		} else {
			return null;
		}
	}

	public double getValorDebitosParaImposto() {
		double soma = 0d;

		if (this.getDebitos(Constantes.SIM, Constantes.SIM) != null) {
			for (int i = 0; i < this.getDebitos(Constantes.SIM, Constantes.SIM).size(); i++) {
				soma += ((Debito) (this.getDebitos(Constantes.SIM, Constantes.SIM).get(i))).getValor();
			}
		}

		return Util.arredondar(soma, 2);
	}

	public double getValorContaSemImpostoSemParcelamento() {

		double valorContaSem = (this.getValorAgua() + this.getValorEsgoto() + this.getValorDebitosParaImposto() + this.getValorRateioAgua() + this.getValorRateioEsgoto());

		if (valorContaSem < 0d) {
			valorContaSem = 0d;
		}
		return Util.arredondar(valorContaSem, 2);
	}

	public double getValorContaSemParcelamentoDebito() {

		double valorConta = this.getValorContaSemImpostoSemParcelamento() - this.getValores();

		if (valorConta < 0d) {
			valorConta = 0d;
		}

		return Util.arredondar(valorConta, 2);
	}




	public List<Debito> getDebitos(int indcUso) {

		if (debitos != null) {
			ArrayList<Debito> tempDebito = new ArrayList<Debito>();

			for (int i = 0; i < debitos.size(); i++) {

				Debito debito = (Debito) debitos.get(i);

				if (debito.getIndcUso() == indcUso) {
					tempDebito.add(debito);
				}
			}

			return tempDebito;
		} else {
			return null;
		}
	}

	public List<Debito> getDebitos() {
		return debitos;
	}

	public List<Credito> getCreditos(int indcUso) {

		if (creditos != null) {

			ArrayList<Credito> tempCreditos = new ArrayList<Credito>();

			for (int i = 0; i < creditos.size(); i++) {

				Credito credito = (Credito) creditos.get(i);

				if (credito.getIndcUso() == indcUso) {
					tempCreditos.add(credito);
				}
			}

			return tempCreditos;
		} else {
			return null;
		}
	}

	public List<Credito> getCreditos() {
		return creditos;
	}

	public List<Imposto> getImpostos() {
		return impostos;
	}

	public List<Conta> getContas() {
		return contas;
	}

	/**
	 * Calcula a tarifa de consumo por categoria ou subcategoria
	 */
	public TarifacaoMinima pesquisarDadosTarifaImovel(boolean tipoTarifaPorCategoria, String codigo, int codigoTarifa) {

		TarifacaoMinima retorno = null;

		for (int i = 0; i < this.tarifacoesMinimas.size(); i++) {

			TarifacaoMinima registro = (TarifacaoMinima) this.tarifacoesMinimas.get(i);

			if (tipoTarifaPorCategoria) {

				if ((Integer.parseInt(codigo) == registro.getCodigoCategoria()) && (codigoTarifa == registro.getCodigo())) {

					retorno = registro;
					break;
				}
			} else {

				if ((codigo.equals(registro.getCodigoSubcategoria() + "")) && (codigoTarifa == registro.getCodigo())) {

					retorno = registro;
					break;
				}
			}
		}

		return retorno;
	}

	/**
	 * seleciona as faixas para calcular o valor faturado
	 */
	public Vector selecionarFaixasCalculoValorFaturado(boolean tipoTarifaPorCategoria, String codigo) {

		Vector retorno = new Vector();

		for (int i = 0; i < this.tarifacoesComplementares.size(); i++) {
			TarifacaoComplementar registro = (TarifacaoComplementar) this.tarifacoesComplementares.get(i);

			if (tipoTarifaPorCategoria) {

				if (Integer.parseInt(codigo) == registro.getCodigoCategoria()) {
					retorno.addElement(registro);
				}

			} else {
				if (Integer.parseInt(codigo) == registro.getCodigoSubcategoria()) {

					retorno.addElement(registro);
				}
			}
		}

		return retorno;
	}

	public double getValorAgua() {
		double soma = 0d;

		for (int i = 0; i < this.dadosCategoria.size(); i++) {

			if ((DadosFaturamento) ((DadosCategoria) this.dadosCategoria.get(i)).getFaturamentoAgua() != null) {
				soma += ((DadosFaturamento) ((DadosCategoria) this.dadosCategoria.get(i)).getFaturamentoAgua()).getValorFaturado();
			}
		}

		return Util.arredondar(soma, 2);
	}

	public double getValorEsgoto() {
		double soma = 0d;

		for (int i = 0; i < this.dadosCategoria.size(); i++) {

			if ((DadosFaturamento) ((DadosCategoria) this.dadosCategoria.get(i)).getFaturamentoEsgoto() != null) {
				soma += ((DadosFaturamento) ((DadosCategoria) this.dadosCategoria.get(i)).getFaturamentoEsgoto()).getValorFaturado();
			}
		}

		return Util.arredondar(soma, 2);
	}

	public double getValorDebitos() {
		double soma = 0d;

		if (this.getDebitos(Constantes.SIM) != null) {
			for (int i = 0; i < this.getDebitos(Constantes.SIM).size(); i++) {
				soma += ((Debito) (this.getDebitos(Constantes.SIM).get(i))).getValor();
			}
		}

		return Util.arredondar(soma, 2);
	}

	public double getValorCreditos() {

		double soma = 0d;

		if (this.getCreditos(Constantes.SIM) != null) {

			for (int i = 0; i < this.getCreditos(Constantes.SIM).size(); i++) {

				if (!isCreditoBolsaAgua(i)) {
					soma += ((Credito) (this.getCreditos().get(i))).getValor();
				}
			}
		}

	/*	if (valorResidualCredito != 0d) {
			soma = soma - this.valorResidualCredito;
		}*/

		return Util.arredondar(soma, 2);
	}

	public boolean isCreditoBolsaAgua(int i) {
		return ((Credito) (this.getCreditos(Constantes.SIM).get(i))).getCodigo().equalsIgnoreCase(CRED_BOLSA_AGUA);
	}

	public double getValorCreditosSemBolsaAgua() {

		double soma = 0d;

		if (this.getCreditos(Constantes.SIM) != null) {

			for (int i = 0; i < this.getCreditos(Constantes.SIM).size(); i++) {

				if (!isCreditoBolsaAgua(i)){
					soma += ((Credito) (this.getCreditos().get(i))).getValor();
				}
			}
		}

		if (valorResidualCredito != 0d) {
			soma = soma - this.valorResidualCredito;
		}

		return Util.arredondar(soma, 2);
	}


	public double getValorContaSemImposto() {

		double valorContaSem = (this.getValorAgua() + this.getValorEsgoto() + this.getValorDebitos() + this.getValorRateioAgua() + this.getValorRateioEsgoto())
				- this.getValorCreditos() - this.getValorCreditosBolsaAgua();

		if (valorContaSem < 0d) {
			valorContaSem = 0d;
		}
		return Util.arredondar(valorContaSem, 2);
	}

	public double getValorCreditosBolsaAgua() {
		double soma = 0d;

		if (this.getCreditos(Constantes.SIM) != null) {

			for (int i = 0; i < this.getCreditos(Constantes.SIM).size(); i++) {

				if (isCreditoBolsaAgua(i)) {
					double valorCredito = ((Credito) (this.getCreditos().get(i))).getValor();
					soma += valorCredito;

				}
			}
		}

		if (valorResidualCredito != 0d) {
			soma = soma - this.valorResidualCredito;
		}

		return Util.arredondar(soma, 2);
	}


	public double getValores() {
		double soma = 0d;

		if (impostos != null) {

			for (int i = 0; i < this.impostos.size(); i++) {
				double percentualAlicota = ((Imposto) (this.impostos.get(i))).getPercentualAlicota();
				double valor = this.getValorContaSemImposto() * Util.arredondar((percentualAlicota / 100), 7);
				soma += valor;
			}
		}

		return Util.arredondar(soma, 2);
	}

	public double getValorConta() {

		double valorConta = this.getValorContaSemImposto() - this.getValores();

		if (valorConta < 0d) {
			valorConta = 0d;
		}

		return Util.arredondar(valorConta, 2);
	}

	public double getValorContaSemCreditos() {

		double valorContaSemCreditos = (this.getValorAgua() + this.getValorEsgoto() + this.getValorDebitos() + this.getValorRateioAgua() + this.getValorRateioEsgoto() - this
				.getValores());

		return Util.arredondar(valorContaSemCreditos, 2);
	}

	public String getLocalidade() {
		return localidade;
	}

	public String getSetorComercial() {
		return setor;
	}

	public String getInscricaoFormatada() {
		String inscricaoFormatada = this.inscricao.trim();
		localidade = inscricaoFormatada.substring(0, 3);
		setor = inscricaoFormatada.substring(3, 6);
		quadra = inscricaoFormatada.substring(6, 10);
		lote = inscricaoFormatada.substring(10, 14);
		sublote = inscricaoFormatada.substring(14, 17);

		inscricaoFormatada = localidade + "." + setor + "." + quadra + "." + lote + "." + sublote;

		return inscricaoFormatada;
	}

	public void setId(long imovelId) {
		this.id = imovelId;
	}

	public long getId() {
		return id;
	}

	public int getAnormalidadeSemHidrometro() {
		return anormalidadeSemHidrometro;
	}

	public void setAnormalidadeSemHidrometro(int anormalidadeSemHidrometro) {
		this.anormalidadeSemHidrometro = anormalidadeSemHidrometro;
	}

	/**
	 * Calcula a tarifa de consumo por categoria ou subcategoria
	 *
	 * @param tipoTarifaPorCategoria
	 *            informa se devemos pesquisar por categoria ou por sub
	 * @param codigo
	 *            codigo da categiria ou da sub
	 * @return tarifa de consumo calculada;
	 */
	public TarifacaoMinima pesquisarDadosTarifaMinimaImovel(boolean tipoTarifaPorCategoria, String codigoCategoria, String codigoSubCategoria, int codigoTarifa, Date dataInicioVigencia) {
		logType = "pesquisarDadosTarifaMinimaImovel";

		LogUtil.salvarLog(logType, "Tipo Tarifa Por Categoria: " + tipoTarifaPorCategoria + " | Codigo Tarifa: " + codigoTarifa + " | Data Inicio Vigencia: " + dataInicioVigencia);

		TarifacaoMinima retorno = null;

		for (int i = 0; i < tarifacoesMinimas.size(); i++) {

			TarifacaoMinima tarifacaoMinima = (TarifacaoMinima) tarifacoesMinimas.get(i);

			if (tipoTarifaPorCategoria) {

				if (Util.compararData(dataInicioVigencia, tarifacaoMinima.getDataVigencia()) == 0 && codigoTarifa == tarifacaoMinima.getCodigo()
						&& Integer.parseInt(codigoCategoria) == tarifacaoMinima.getCodigoCategoria()
						&& (tarifacaoMinima.getCodigoSubcategoria() == Constantes.NULO_INT || tarifacaoMinima.getCodigoSubcategoria() == 0)) {

					retorno = tarifacaoMinima;
					break;
				}
			} else {

				if (Util.compararData(dataInicioVigencia, tarifacaoMinima.getDataVigencia()) == 0 && codigoTarifa == tarifacaoMinima.getCodigo()
						&& Integer.parseInt(codigoCategoria) == tarifacaoMinima.getCodigoCategoria()
						&& Integer.parseInt(codigoSubCategoria) == tarifacaoMinima.getCodigoSubcategoria()) {

					retorno = tarifacaoMinima;
					break;
				}
			}
		}

		if (retorno != null) {
			LogUtil.salvarLog(logType, "Tarifa Minima Categoria: " + retorno.getTarifaMinimaCategoria() + " | Consumo Minimo Subcategoria: " + retorno.getConsumoMinimoSubcategoria());
		} else {
			LogUtil.salvarLog(logType, "Tarifa Minima NULA");
		}

		return retorno;
	}

	/**
	 * Seleciona as faixas para calcular o valor faturado
	 */
	public Vector selecionarFaixasCalculoValorFaturado(boolean tipoTarifaPorCategoria, String codigo, int codigoTarifa, Date dataInicioVigencia) {

		Vector retorno = new Vector();

		for (int i = 0; i < tarifacoesComplementares.size(); i++) {

			TarifacaoComplementar tarifacaoComplementar = (TarifacaoComplementar) tarifacoesComplementares.get(i);

			if (tipoTarifaPorCategoria) {

				if (Util.compararData(dataInicioVigencia, tarifacaoComplementar.getDataInicioVigencia()) == 0 && codigoTarifa == tarifacaoComplementar.getCodigo()
						&& Integer.parseInt(codigo) == tarifacaoComplementar.getCodigoCategoria()
						&& (tarifacaoComplementar.getCodigoSubcategoria() == Constantes.NULO_INT || tarifacaoComplementar.getCodigoSubcategoria() == 0)) {

					retorno.addElement(tarifacaoComplementar);
				}
			} else {

				if (Util.compararData(dataInicioVigencia, tarifacaoComplementar.getDataInicioVigencia()) == 0 && codigoTarifa == tarifacaoComplementar.getCodigo()
						&& Integer.parseInt(codigo) == tarifacaoComplementar.getCodigoCategoria() && Integer.parseInt(codigo) == tarifacaoComplementar.getCodigoSubcategoria()) {

					retorno.addElement(tarifacaoComplementar);
				}
			}
		}

		return retorno;
	}

	public String getDescricaoSitLigacaoAgua(int situacaoLigAgua) {
		String descricaoSitLigacaoAgua = "";

		if (situacaoLigAgua != 0) {

			switch (situacaoLigAgua) {

				case POTENCIAL:
					descricaoSitLigacaoAgua = "POTENCIAL";
					break;
				case FACTIVEL:
					descricaoSitLigacaoAgua = "FACTIVEL";
					break;
				case LIGADO:
					descricaoSitLigacaoAgua = "LIGADO";
					break;
				case EM_FISCALIZACAO:
					descricaoSitLigacaoAgua = "LIGADO EM ANALISE.";
					break;
				case CORTADO:
					descricaoSitLigacaoAgua = "CORTADO";
					break;
				case SUPRIMIDO:
					descricaoSitLigacaoAgua = "SUPRIMIDO";
					break;
				case SUPR_PARC:
					descricaoSitLigacaoAgua = "SUPR. PARC.";
					break;
				case SUPR_PARC_PEDIDO:
					descricaoSitLigacaoAgua = "SUP. PARC. PED.";
					break;
				case EM_CANCELAMENTO:
					descricaoSitLigacaoAgua = "EM CANCEL.";
					break;
			}
		}
		return descricaoSitLigacaoAgua;
	}

	public String getDescricaoSitLigacaoEsgoto(int situacaoLigEsgoto) {
		String descricaoSitLigacaoEsgoto = "";

		if (situacaoLigEsgoto != 0) {
			switch (situacaoLigEsgoto) {

				case POTENCIAL:
					descricaoSitLigacaoEsgoto = "POTENCIAL";
					break;
				case FACTIVEL:
					descricaoSitLigacaoEsgoto = "FACTIVEL";
					break;
				case LIGADO:
					descricaoSitLigacaoEsgoto = "LIGADO";
					break;
				case EM_FISCALIZACAO:
					descricaoSitLigacaoEsgoto = "EM FISCAL.";
					break;
				case LIG_FORA_USO:
					descricaoSitLigacaoEsgoto = "LIG. FORA DE USO";
					break;
				case TAMPONADO:
					descricaoSitLigacaoEsgoto = "TAMPONADO";
					break;
				case CONVERSAO:
					descricaoSitLigacaoEsgoto = "CONVERSAO";
					break;

			}
		}
		return descricaoSitLigacaoEsgoto;
	}

	public int getIndcImoveldo() {
		return indcImoveldo;
	}

	public void setIndcImoveldo(int indcImoveldo) {
		this.indcImoveldo = indcImoveldo;
	}

	public double getValorDebitosAnteriores() {

		double soma = 0d;

		if (this.contas != null) {

			for (int i = 0; i < this.contas.size(); i++) {
				soma += ((Conta) (this.contas.get(i))).getValor();
			}
		}

		return Util.arredondar(soma, 2);
	}

	public String toString() {
		return this.getMatricula() + " - " + this.getEndereco();
	}

	public String getCpfCnpjCliente() {
		return cpfCnpjCliente;
	}

	public void setCpfCnpjCliente(String cpfCnpjCliente) {
		this.cpfCnpjCliente = cpfCnpjCliente;
	}

	public double getValorResidualCredito() {
		return valorResidualCredito;
	}

	public void setValorResidualCredito(double valorResidualCredito) {
		this.valorResidualCredito = valorResidualCredito;
	}

	/**
	 * Método que verifica se o imóvel deve ser enviado logo após sua Impressao ou apenas no final do roteiro
	 **/
	public boolean enviarAoImprimir() {
		// Verificamos o valor mínimo da conta
		boolean enviarContaValorMaiorPermitido = isValorContaAcimaDoMinimo();

		/*
		 * Será necessário reenviar caso haja alteração na leitura de agua ou
		 * anormalidade de agua ou na leitura de poco ou anormalidade de poco ou
		 * na anormalidade sem hidrometro. Imóveis que possuem débito do tipo
		 * cortado de água, com esgoto à 30%, devem ser enviados apenas no final
		 */
		if (this.indcImovelCalculado == Constantes.SIM && this.indcImovelEnviado == Constantes.NAO
				&& (this.indcImovelImpresso == Constantes.SIM || this.indcGeracaoConta == Constantes.NAO) && this.valorResidualCredito == 0d
				&& this.getDebito(Debito.TARIFA_CORTADO_DEC_18_251_94) == null && enviarContaValorMaiorPermitido) {

			return true;

		} else {
			return false;
		}
	}

	/**
	 * Método que verifica se o imóvel deve ser enviado ao finalizar o processo.
	 **/
	public boolean enviarAoFinalizar() {
		return this.indcImovelCalculado == Constantes.SIM && this.indcImovelEnviado == Constantes.NAO;
	}

	public boolean isImovelCondominio() {
		return (indcCondominio == Constantes.SIM || (indcCondominio == Constantes.NAO && matriculaCondominio != Constantes.NULO_INT));
	}

	public boolean isImovelMicroCondominio() {
		return (indcCondominio == Constantes.NAO && matriculaCondominio != Constantes.NULO_INT);
	}

	public int getIndiceImovelCondominio() {
		return (int) (id - (getIdImovelCondominio() - 1));
	}

	public int getIdImovelCondominio() {

		if (isImovelMicroCondominio()) {
			return ControladorRota.getInstancia().getDataManipulator().getListaIdsCondominio(matriculaCondominio).get(0);
		} else {
			// Macro-medidor
			return ControladorRota.getInstancia().getDataManipulator().getListaIdsCondominio(matricula).get(0);
		}
	}

	public int getQuantidadeImoveisCondominio() {
		return ControladorRota.getInstancia().getDataManipulator().selectQuantidadeImoveisCondominio(efetuarRateioConsumoHelper.getMatriculaMacro());
	}

	public void setSituacaoTipo(SituacaoTipo situacaoTipo) {
		this.situacaoTipo = situacaoTipo;
	}

	public Date getDataLeituraAnteriorNaoMedido() {
		return dataLeituraAnteriorNaoMedido;
	}

	public void setDataLeituraAnteriorNaoMedido(String dataLeituraAnteriorNaoMedido) {
		this.dataLeituraAnteriorNaoMedido = Util.getData(Util.verificarNuloString(dataLeituraAnteriorNaoMedido));
	}

	public Date getDataImpressaoNaoMedido() {
		return dataImpressaoNaoMedido;
	}

	public int getMatricula() {
		return matricula;
	}

	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}

	public String getNomeGerenciaRegional() {
		return nomeGerenciaRegional;
	}

	public String getNomeEscritorio() {
		return nomeEscritorio;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public Date getDataValidadeConta() {
		return dataValidadeConta;
	}

	public String getSituacaoLigAguaString() {
		return situacaoLigAgua.equals(Constantes.LIGADO) ? "Ligado" : "Desligado";
	}

	public String getSituacaoLigEsgotoString() {
		return situacaoLigEsgoto.equals(Constantes.LIGADO) ? "Ligado" : "Desligado";
	}

	public int getContagemValidacaoAgua() {
		return contagemValidacaoAgua;
	}

	public void setContagemValidacaoAgua(int contagemValidacaoAgua) {
		this.contagemValidacaoAgua = contagemValidacaoAgua;
	}

	public int getContagemValidacaoPoco() {
		return contagemValidacaoPoco;
	}

	public void setContagemValidacaoPoco(int contagemValidacaoPoco) {
		this.contagemValidacaoPoco = contagemValidacaoPoco;
	}

	public void setDataImpressaoNaoMedido(String dataImpressaoNaoMedido) {
		this.dataImpressaoNaoMedido = Util.getData(Util.verificarNuloString(dataImpressaoNaoMedido));
	}

	public short getIndicadorAbastecimentoAgua() {
		return indicadorAbastecimentoAgua;
	}

	public void setIndicadorAbastecimentoAgua(String indicadorAbastecimentoAgua) {
		this.indicadorAbastecimentoAgua = Util.verificarNuloShort(indicadorAbastecimentoAgua);
	}

	public short getIndicadorImovelSazonal() {
		return indicadorImovelSazonal;
	}

	public void setIndicadorImovelSazonal(String indicadorImovelSazonal) {
		this.indicadorImovelSazonal = Util.verificarNuloShort(indicadorImovelSazonal);
	}

	public int pesquisarPrincipalCategoria() {
		int idCateoria = 0;
		int maiorQuantidadeEconomias = 0;

		for (int i = 0; i < this.dadosCategoria.size(); i++) {

			DadosCategoria dadosCategoria = (DadosCategoria) this.dadosCategoria.get(i);

			if (dadosCategoria.getCodigoCategoria() != idCateoria) {

				int quantidadeEconomias = this.getQuantidadeEconomias(dadosCategoria.getCodigoCategoria(), dadosCategoria.getCodigoSubcategoria());

				if (maiorQuantidadeEconomias < quantidadeEconomias) {

					maiorQuantidadeEconomias = quantidadeEconomias;
					idCateoria = dadosCategoria.getCodigoCategoria();
				}
			}

		}

		return idCateoria;
	}

	/**
	 * Esse metodo verifica se a instancia do imovel está com valores iguais aos
	 * que foram informados nas abas. Esse método deve ser utilizado para julgar
	 * se algo mudou na entrada do objeto.
	 */
	public boolean verificarAlteracaoDadosImovel() {
		return true;
	}

	public boolean isImovelAlterado() {
		boolean isImovelAlterado = false;

		if (this.getMedidor(Constantes.LIGACAO_AGUA) != null || this.getMedidor(Constantes.LIGACAO_POCO) != null) {

			isImovelAlterado = true;
		}

		if (this.getMedidor(Constantes.LIGACAO_AGUA) == null && this.getMedidor(Constantes.LIGACAO_POCO) == null) {

			if (indcImovelImpresso == Constantes.NAO) {
				isImovelAlterado = true;
			}
		}

		return isImovelAlterado;
	}

	/**
	 * Julga se é necessário zerar os consumos, pois o usuário apagou os dados
	 * de de leitura e/ou anormalidade do imovel selecionado
	 */
	public boolean verificarLeituraAnormalidadeZeradas() {
		return true;
	}

	public int getAnormalidadeGravadaAnterior() {
		return anormalidadeGravadaAnterior;
	}

	public void setAnormalidadeGravadaAnterior(int anormalidadeGravadaAnterior) {
		this.anormalidadeGravadaAnterior = anormalidadeGravadaAnterior;
	}

	public int getLeituraGravadaAnterior() {
		return leituraGravadaAnterior;
	}

	public void setLeituraGravadaAnterior(int leituraGravadaAnterior) {
		this.leituraGravadaAnterior = leituraGravadaAnterior;
	}

	/**
	 * Metodo que atualiza o resumo necessário para o rateio do imóvel
	 * condominio
	 *
	 * @author Bruno Barros
	 * @date 11/03/2010
	 * @param consumoAgua
	 *            Consumo de agua novo, caso esteja atualizando o consumo de
	 *            esgoto, setar nulo
	 * @param consumoEsgoto
	 *            Consumo de esgoto novo, caso esteja atualizando o consumo de
	 *            agua, setar nulo
	 */
	public void atualizarResumoEfetuarRateio(Consumo consumoAgua, Consumo consumoEsgoto) {
		// Verificamos se é um imóvel micro
		if (this.isImovelMicroCondominio()) {

			// Apenas adicionamos a quantidade de economias, se ela não houver sido adicionada anteriormente
			if ((!indcAdicionouDadosIniciaisHelperRateio)) {

				indcAdicionouDadosIniciaisHelperRateio = true;

				// Verificamos se o imóvel é faturado de agua
				if ((this.getIndcFaturamentoAgua() == Constantes.SIM) || (this.getIndcFaturamentoAgua() == Constantes.NAO && isImovelMicroCondominio())) {

					// Calculamos a quantidade de economias total
					int quantidadeEconomiasImovel = this.getQuantidadeEconomiasTotal();

					efetuarRateioConsumoHelper.setQuantidadeEconomiasAguaTotal(efetuarRateioConsumoHelper.getQuantidadeEconomiasAguaTotal() + quantidadeEconomiasImovel);
				}

				// Verifica se o imóvel é faturado de esgoto
				if ((this.getIndcFaturamentoEsgoto() == Constantes.SIM) || (this.getIndcFaturamentoEsgoto() == Constantes.NAO && this.isImovelMicroCondominio())) {

					// Calculamos a quantidade de economias total
					int quantidadeEconomiasImovel = this.getQuantidadeEconomiasTotal();

					efetuarRateioConsumoHelper.setQuantidadeEconomiasEsgotoTotal(efetuarRateioConsumoHelper.getQuantidadeEconomiasEsgotoTotal() + quantidadeEconomiasImovel);

				}
			}

			indcGeracaoConta = Constantes.SIM;
			if (consumoAgua != null) {

				boolean valorContaMaiorPermitido = this.isValorContaMaiorPermitido();

				if (this.consumoAgua != null) {
					// Removemos do total o consumo calculado anteriormente, para logo mais abaixo, adicionamos o novo consumo
					efetuarRateioConsumoHelper.setConsumoLigacaoAguaTotal(efetuarRateioConsumoHelper.getConsumoLigacaoAguaTotal() - this.consumoAgua.getConsumoCobradoMesOriginal());
				}

				// Adicionamos o consumo de agua total
				efetuarRateioConsumoHelper.setConsumoLigacaoAguaTotal(efetuarRateioConsumoHelper.getConsumoLigacaoAguaTotal() + consumoAgua.getConsumoCobradoMesOriginal());

				// Verifica se houve anormalidade de consumo para reter conta.
				if (consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ALTO_CONSUMO || consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ESTOURO_MEDIA
						|| consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ESTOURO || consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_HIDR_SUBST_INFO) {

					indcGeracaoConta = Constantes.NAO;
				}

				// Verifica se houve anormalidade de leitura para reter conta.
				if (consumoAgua.getAnormalidadeLeituraFaturada() == ControladorConta.ANORM_HIDR_LEITURA_IMPEDIDA_CLIENTE
						|| consumoAgua.getAnormalidadeLeituraFaturada() == ControladorConta.ANORM_HIDR_PORTAO_FECHADO) {

					indcGeracaoConta = Constantes.NAO;
				}
			}

			if (consumoEsgoto != null) {

				if (this.consumoEsgoto != null) {
					// Removemos do total o consumo calculado anteriormente, para logo mais abaixo, adicionarmos o novo consumo
					efetuarRateioConsumoHelper.setConsumoLigacaoEsgotoTotal(efetuarRateioConsumoHelper.getConsumoLigacaoEsgotoTotal()
							- this.consumoEsgoto.getConsumoCobradoMesOriginal());
				}

				// Adicionamos o consumo de esgoto total
				efetuarRateioConsumoHelper.setConsumoLigacaoEsgotoTotal(efetuarRateioConsumoHelper.getConsumoLigacaoEsgotoTotal() + consumoEsgoto.getConsumoCobradoMesOriginal());
			}

			// Caso seja Hidrometro Macro
		} else if (this.indcCondominio == Constantes.SIM && this.matriculaCondominio == Constantes.NULO_INT) {

			if (consumoAgua != null) {

				// Verifica se houve anormalidade de consumo para reter conta.
				if (consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ALTO_CONSUMO || consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ESTOURO_MEDIA
						|| consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_ESTOURO || consumoAgua.getAnormalidadeConsumo() == Consumo.CONSUMO_ANORM_HIDR_SUBST_INFO) {

					indcGeracaoConta = Constantes.NAO;
				}

				// Verifica se houve anormalidade de leitura para reter conta.
				if (consumoAgua.getAnormalidadeLeituraFaturada() == ControladorConta.ANORM_HIDR_LEITURA_IMPEDIDA_CLIENTE
						|| consumoAgua.getAnormalidadeLeituraFaturada() == ControladorConta.ANORM_HIDR_PORTAO_FECHADO) {

					indcGeracaoConta = Constantes.NAO;
				}
			}
		}
	}

	public int getIndicadorParalizarFaturamentoAgua() {
		return indicadorParalizarFaturamentoAgua;
	}

	public int getIndicadorParalizarFaturamentoEsgoto() {
		return indicadorParalizarFaturamentoEsgoto;
	}

	public int getOpcaoDebitoAutomatico() {
		return opcaoDebitoAutomatico;
	}

	public void setOpcaoDebitoAutomatico(String opcaoDebitoAutomatico) {
		this.opcaoDebitoAutomatico = Util.verificarNuloInt(opcaoDebitoAutomatico);
	}

	/**
	 * Método julga se imóvel em questão pode ter os seus valores de leitura ou anormalidade alterados
	 */
	public boolean podeAlterarLeituraAnormalidade() {
		boolean habilitar = true;

		if (ControladorRota.getInstancia().getDadosGerais().getIndcBloquearReemissaoConta() == Constantes.SIM) {

			if (this.isImovelCondominio()) {

				if (this.getIndcCondominio() == Constantes.SIM) {

					if (this.getIndcImovelImpresso() == Constantes.SIM) {
						habilitar = false;
					}

				} else {
					Imovel imovelMacro = ControladorRota.getInstancia().getDataManipulator().selectImovel("matricula = " + this.getMatriculaCondominio(), false);

					if (imovelMacro.getIndcImovelImpresso() == Constantes.SIM) {
						habilitar = false;
					}
				}

			} else if (this.getIndcImovelImpresso() == Constantes.SIM) {
				habilitar = false;
			}
		}

		return habilitar;
	}

	/**
	 * Verifica se o valor da conta informada no imóvel é superio ao valor
	 * máximo permitido para impressão da mesma de acordo com o seu perfil
	 */
	public boolean isValorContaMaiorPermitido() {
		// Caso o valor da conta seja maior que o valor permitido para ser impresso, só enviar a conta no final do processo (Finalizar Roteiro)
		boolean contaValorMaiorPermitido = false;

		double valorConta = this.getValorConta();
		double valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_CONTA;

		switch (Integer.parseInt(this.getCodigoPerfil())) {
			case PERFIL_GRANDE:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_GRANDE;
				break;

			case PERFIL_GRANDE_MES:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_GRANDE_MES;
				break;

			case PERFIL_ESPECIAL:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_ESPECIAL;
				break;

			case PERFIL_BONUS_SOCIAL:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_BONUS_SOCIAL;
				break;

			case PERFIL_NORMAL:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_NORMAL;
				break;

			case PERFIL_CORPORATIVO:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_CORPORATIVO;
				break;

			case PERFIL_GOVERNO_METROPOLITANO:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_GOVERNO_METROPOLITANO;
				break;

			case PERFIL_GOVERNO_INTERIOR:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_GOVERNO_INTERIOR;
				break;

			case PERFIL_CONDOMINIAL:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_CONDOMINIAL;
				break;

			case PERFIL_COLABORADOR:
				valorMaximoEmissaoConta = ControladorConta.VALOR_LIMITE_PERFIL_COLABORADOR;
				break;
		}

		if (valorConta > valorMaximoEmissaoConta) {
			contaValorMaiorPermitido = true;
		}

		return contaValorMaiorPermitido;
	}

	/**
	 * Verifica se o valor da conta informada no imóvel é inferior ao valor
	 * minimo permitido para impressão da mesma
	 */
	public boolean isValorContaAcimaDoMinimo() {
		// Caso o valor da conta seja menor que o valor permitido para ser impresso, só enviar a conta no final do processo (Finalizar Roteiro)
		boolean enviarContaValorMenorPermitido = true;

		double valorConta = this.getValorConta();
		double valorMinimoEmissaoConta = ControladorRota.getInstancia().getDadosGerais().getValorMinimEmissaoConta();

		if (valorConta < valorMinimoEmissaoConta) {

			if (this.getValorCreditos() == 0d) {
				enviarContaValorMenorPermitido = false;
			}
		}

		return enviarContaValorMenorPermitido;
	}

	/**
	 * Verifica se o crédito do imóvel é de Nitrato, Caso seja, então seta 50 %
	 * do valor de água nesse crédito que será atualizado no GSAN
	 */
	public void setValorCreditosNitrato(double valorCreditoNitrato) {
		if (this.creditos != null) {

			for (int i = 0; i < this.creditos.size(); i++) {

				Credito registroDescricaoValor = ((Credito) (this.creditos.get(i)));

				String descricaoCredito = registroDescricaoValor.getDescricao();

				if (descricaoCredito != null && !descricaoCredito.equals("")) {

					if (descricaoCredito.substring(0, 16).equals(Credito.DESCRICAO_CERDITO_NITRATO)) {

						registroDescricaoValor.setValor("" + valorCreditoNitrato);
					}
				}
			}
		}
	}

	/**
	 * Para matriculas iguais consideramos o mesmo imóvel
	 */
	public boolean equals(Object obj) {
		return (obj instanceof Imovel) && ((Imovel) obj).getMatricula() == this.getMatricula();
	}

	public void setDataEmissaoDocumento(String dataEmissaoDocumento) {
		this.dataEmissaoDocumento = Util.getData(Util.verificarNuloString(dataEmissaoDocumento));
	}

	public Date getDataEmissaoDocumento() {
		return dataEmissaoDocumento;
	}

	public void setPercentualAlternativoEsgoto(String percentColetaEsgoto) {
		this.percentualAlternativoEsgoto = Util.verificarNuloDouble(percentColetaEsgoto);
	}

	public void setConsumoPercentualAlternativoEsgoto(String percentColetaEsgoto) {
		this.consumoPercentualAlternativoEsgoto = Util.verificarNuloInt(percentColetaEsgoto);
	}

	public double getPercentualAlternativoEsgoto() {
		return percentualAlternativoEsgoto;
	}

	public int getConsumoPercentualAlternativoEsgoto() {
		return consumoPercentualAlternativoEsgoto;
	}

	/**
	 * Verifica se o imóvel tem um percentual de esgoto alternativo
	 */
	public void verificarPercentualEsgotoAlternativo(int consumoFaturadoEsgoto) {

		double percentualEsgoto = 0.00;

		// CASO O IMÓVEL SEJA PARA FATURAR ESGOTO Essa verificação se faz necessária para o pré-faturamento.
		if ((this.getIndcFaturamentoEsgoto() == Constantes.SIM) || (this.getIndcFaturamentoEsgoto() == Constantes.NAO && this.isImovelMicroCondominio())) {

			// Recupera o percentual de esgoto do imóvel.
			percentualEsgoto = this.getPercentCobrancaEsgoto();

			// CASO O IMÓVEL SEJA PARA FATURAR ÁGUA
			if (this.getIndcFaturamentoAgua() == Constantes.SIM && consumoFaturadoEsgoto != Constantes.NULO_INT) {

				// Caso o percentual alternativo de esgoto seja diferente de nulo
				if (this.getPercentualAlternativoEsgoto() != Constantes.NULO_DOUBLE) {

					double qtdeEconomia = this.getQuantidadeEconomiasTotal();
					double consumoFaturadoEsgotoDouble = consumoFaturadoEsgoto;

					int consumoPorEconomia = Util.arredondar(consumoFaturadoEsgotoDouble / qtdeEconomia);

					// verificar se o consumo por economia é menor ou igual ao consumo do percentual alternativo
					if (consumoPorEconomia <= this.getConsumoPercentualAlternativoEsgoto()) {

						// enviar como percentual de esgoto o menor valor entre percentual e percentualAlternativo
						if (this.getPercentualAlternativoEsgoto() < percentualEsgoto) {
							percentualEsgoto = this.getPercentualAlternativoEsgoto();
						}
					}
				}
			}
		}

		this.setPercentCobrancaEsgoto("" + percentualEsgoto);
	}

	/**
	 *
	 * Verifica se existe um débito com o código informado Caso positivo,
	 * retornar senão nulo
	 */
	public Debito getDebito(String codigo) {
		Debito temp = new Debito();
		temp.setCodigo(codigo);

		if (this.debitos != null && this.debitos.contains(temp)) {
			return (Debito) this.debitos.get(this.debitos.indexOf(temp));

		} else {
			return null;
		}
	}

	/**
	 * Verifica se existe um credito com o código informado Caso positivo, retornar senão nulo
	 */
	public Credito getCredito(String codigo) {

		Credito regTemp = new Credito();
		regTemp.setCodigo(codigo);

		if (this.creditos != null && this.creditos.contains(regTemp)) {
			return (Credito) this.creditos.get(this.creditos.indexOf(regTemp));

		} else {
			return null;
		}
	}

	public List<TarifacaoComplementar> selecionarTarifasComplementaresParaCalculo(boolean tipoTarifaPorCategoria, String codigoCategoria,
																				  String codigoSubCategoria, int codigoTarifa, Date dataInicioVigencia) {

		logType = "selecionarTarifasComplementaresParaCalculo";

		List<TarifacaoComplementar> retorno = new ArrayList<TarifacaoComplementar>();

		LogUtil.salvarLog(logType, "Qtd : " + tarifacoesComplementares.size());

		for (int i = 0; i < tarifacoesComplementares.size(); i++) {

			TarifacaoComplementar tarifacaoComplementar = (TarifacaoComplementar) tarifacoesComplementares.get(i);

			LogUtil.salvarLog(logType, "(1) Data Inicio Vigencia: " + dataInicioVigencia + " <--> " + "(2) Data Inicio Vigencia: " + tarifacaoComplementar.getDataInicioVigencia());
			LogUtil.salvarLog(logType, "(1) Tarifa: " + codigoTarifa + " <--> " + "(2) Tarifa: " + tarifacaoComplementar.getCodigo());
			LogUtil.salvarLog(logType, "(1) Categoria: " + codigoCategoria + " <--> " + "(2) Categoria: " + tarifacaoComplementar.getCodigoCategoria());
			LogUtil.salvarLog(logType, "(1) Subcategoria: " + codigoSubCategoria + " <--> "+ "(2) Subcategoria: " + tarifacaoComplementar.getCodigoSubcategoria());

			if (tipoTarifaPorCategoria) {

				if (Util.compararData(dataInicioVigencia, tarifacaoComplementar.getDataInicioVigencia()) == 0 && codigoTarifa == tarifacaoComplementar.getCodigo()
						&& Integer.parseInt(codigoCategoria) == tarifacaoComplementar.getCodigoCategoria()
						&& (tarifacaoComplementar.getCodigoSubcategoria() == Constantes.NULO_INT || tarifacaoComplementar.getCodigoSubcategoria() == 0)) {

					retorno.add(tarifacaoComplementar);
				}

			} else {
				if (Util.compararData(dataInicioVigencia, tarifacaoComplementar.getDataInicioVigencia()) == 0 && codigoTarifa == tarifacaoComplementar.getCodigo()
						&& Integer.parseInt(codigoCategoria) == tarifacaoComplementar.getCodigoCategoria()
						&& Integer.parseInt(codigoSubCategoria) == tarifacaoComplementar.getCodigoSubcategoria()) {

					retorno.add(tarifacaoComplementar);
				}
			}
		}

		return retorno;
	}

	/**
	 *
	 * Verifica se o imóvel é do tipo informativo.
	 *
	 * @author Daniel Zaccarias
	 * @date 03/07/2011
	 *
	 * @param
	 * @return
	 */
	public boolean isImovelInformativo() {

		if (imovelStatus == Constantes.IMOVEL_STATUS_INFORMATIVO) {
			return true;

		} else {
			return false;
		}
	}

	public void setSequencialRotaMarcacao(int sequencialRotaMarcacao) {
		this.sequencialRotaMarcacao = sequencialRotaMarcacao;
	}

	public int getSequencialRotaMarcacao() {
		return sequencialRotaMarcacao;
	}

	public List<TarifacaoMinima> getTarifacoesMinimas() {
		return tarifacoesMinimas;
	}

	public ArrayList<List<TarifacaoMinima>> getTarifacoesMinimasPorCategoria() {

		ArrayList<List<TarifacaoMinima>> tarifas = new ArrayList<List<TarifacaoMinima>>();
		List<TarifacaoMinima> tarifasResidencial = new ArrayList<TarifacaoMinima>();
		List<TarifacaoMinima> tarifasComercial = new ArrayList<TarifacaoMinima>();
		List<TarifacaoMinima> tarifasIndustrial = new ArrayList<TarifacaoMinima>();
		List<TarifacaoMinima> tarifasPublico = new ArrayList<TarifacaoMinima>();

		for (TarifacaoMinima tarifa : tarifacoesMinimas) {

			if (tarifa.getCodigoCategoria() == 1) {
				tarifasResidencial.add(tarifa);
			} else if (tarifa.getCodigoCategoria() == 2) {
				tarifasComercial.add(tarifa);
			} else if (tarifa.getCodigoCategoria() == 3) {
				tarifasIndustrial.add(tarifa);
			} else if (tarifa.getCodigoCategoria() == 4) {
				tarifasPublico.add(tarifa);
			}
		}

		tarifas.add(tarifasResidencial);
		tarifas.add(tarifasComercial);
		tarifas.add(tarifasIndustrial);
		tarifas.add(tarifasPublico);

		return tarifas;
	}

	public void setTarifacoesMinimas(List<TarifacaoMinima> tarifacoesMinimas) {
		this.tarifacoesMinimas = tarifacoesMinimas;
	}

	public List<TarifacaoComplementar> getTarifacoesComplementares() {
		return tarifacoesComplementares;
	}

	public void setTarifacoesComplementares(List<TarifacaoComplementar> tarifacoesComplementares) {
		this.tarifacoesComplementares = tarifacoesComplementares;
	}

	public double getValorImpostos() {
		double soma = 0d;
		if (impostos != null) {
			for (int i = 0; i < this.impostos.size(); i++) {
				double percentualAlicota = ((this.impostos.get(i))).getPercentualAlicota();
				double valorImposto = this.getValorContaSemImposto() * Util.arredondar((percentualAlicota / 100), 7);
				soma += valorImposto;
			}
		}

		return Util.arredondar(soma, 2);
	}

	public boolean isTarifacaoComplementarNulaOuZerada() {
		return tarifacaoComplementarNulaOuZerada;
	}

	public void setTarifacaoComplementarNulaOuZerada(boolean tarifacaoComplementarNulaOuZerada) {
		this.tarifacaoComplementarNulaOuZerada = tarifacaoComplementarNulaOuZerada;
	}
}