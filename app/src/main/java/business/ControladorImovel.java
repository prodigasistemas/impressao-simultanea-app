package business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import model.Consumo;
import model.Credito;
import model.DadosCategoria;
import model.DadosFaturamento;
import model.DadosFaturamentoFaixa;
import model.DadosTarifa;
import model.Imovel;
import model.Tarifa;
import model.TarifacaoComplementar;
import model.TarifacaoMinima;
import util.Constantes;
import util.LogUtil;
import util.Util;
import views.MedidorAguaTab;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import static model.Credito.CRED_BOLSA_AGUA;

public class ControladorImovel {

    public static ControladorImovel instancia;

    private static String logType = "";
    private static Imovel imovelSelecionado = new Imovel();
    private static long idImovelSelecionado = 0;
    private static int imovelListPosition = -1;

	public static final int SIM = 1;
	public static final int NAO = 2;
    
    public static ControladorImovel getInstancia() {

    	if (ControladorImovel.instancia == null) {
			ControladorImovel.instancia = new ControladorImovel();

		}
		return ControladorImovel.instancia;
    }

    public Imovel getImovelSelecionado(){
    	return ControladorImovel.imovelSelecionado;
    }
    
    public void setImovelSelecionado(Imovel imovelSelecionado){
    	ControladorImovel.imovelSelecionado = imovelSelecionado;
    	LogUtil.salvarLog("IMOVEL SELECIONADO", imovelSelecionado.getMatricula() + "");
    }
     
    public void setImovelSelecionadoByListPosition(int listPosition){
    	setImovelListPosition(listPosition);
    	idImovelSelecionado = getIdImovelSelecionado(listPosition, null);
    	instancia.setImovelSelecionado(ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + idImovelSelecionado, true));
    }
    
    public void setImovelSelecionadoByListPositionInConsulta(int listPositionInConsulta, String condition){
    	idImovelSelecionado = getIdImovelSelecionado(listPositionInConsulta, condition);
    	setImovelListPosition(getImovelListPositionById(idImovelSelecionado));

    	instancia.setImovelSelecionado(ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + idImovelSelecionado, true));
    }
    
    public void setImovelSelecionado(long id){
    	idImovelSelecionado = id;

    	instancia.setImovelSelecionado(ControladorRota.getInstancia().getDataManipulator().selectImovel("id = " + idImovelSelecionado, true));
    }
    
    public int getIdImovelSelecionado(int listPosition, String condition){
    	// se for cadastro novo
    	if (listPosition == -1){
    		return 0;
 
    	}else{
        	return Integer.parseInt(ControladorRota.getInstancia().getDataManipulator().selectIdImoveis(condition).get(listPosition));
     	}
    }
    
    public int getImovelListPositionById(long id){
    	int position = 0;
    	ArrayList<String> listIds = (ArrayList<String>) ControladorRota.getInstancia().getDataManipulator().selectIdImoveis(null);
    	
    	for(int i = 0; i < listIds.size(); i++){
    		if (id == Long.parseLong(listIds.get(i))){
    			position = i;
    			break;
    		}
      	}
    	return position;
    }
    
    public long getIdImovelSelecionado(){
    	return idImovelSelecionado;
    }
    
    // Retorna a posição do imovel selecionado na lista de imoveis ordenada por inscrição
    public int getImovelListPosition(){
    	return imovelListPosition;
    }
    
    // Guarda a posição do imovel selecionado na lista de imoveis ordenada por inscrição
    public void setImovelListPosition(int position){
    	imovelListPosition = position;
    }
    
    public boolean tipoTarifaPorCategoria(Imovel imovel) {

		for (int i = 0; i < imovel.getTarifacoesMinimas().size(); i++) {
		    TarifacaoMinima tarifacaoMinima = (TarifacaoMinima) imovel.getTarifacoesMinimas().get(i);
	
		    if (tarifacaoMinima.getCodigoSubcategoria() == Constantes.NULO_INT) {
		    	return true;
		    }
		}
	
		return false;
    }

    /**
     * Calcula o consumo minimo do imovel. Inicialmente tentamos pesquisar por subcategoria, e caso nao consigamos, pesquisamos por categorias.
     */
	public int calcularConsumoMinimoImovel(Imovel imovel, Date dataInicioVigencia) {
		logType = "calcularConsumoMinimoImovel";

		int consumoMinimoImovel = 0;

		for (int i = 0; i < imovel.getDadosCategoria().size(); i++) {
			DadosCategoria dadosCategoria = (DadosCategoria) imovel.getDadosCategoria().get(i);

			int codigoSubcategoria = 0;

			if (dadosCategoria.getCodigoSubcategoria() != null && !dadosCategoria.getCodigoSubcategoria().equals("")) {
				codigoSubcategoria = Integer.parseInt(dadosCategoria.getCodigoSubcategoria());
			}

			boolean calculoPorSubcategoria = false;

			// Por Subcategoria
			for (int j = 0; j < imovel.getTarifacoesMinimas().size(); j++) {

				TarifacaoMinima tarifacaoMinima = (TarifacaoMinima) imovel.getTarifacoesMinimas().get(j);

				if (Util.compararData(dataInicioVigencia, tarifacaoMinima.getDataVigencia()) == 0
						&& imovel.getCodigoTarifa() == tarifacaoMinima.getCodigo()
						&& dadosCategoria.getCodigoCategoria() == tarifacaoMinima.getCodigoCategoria() 
						&& codigoSubcategoria == tarifacaoMinima.getCodigoSubcategoria()) {

					consumoMinimoImovel += tarifacaoMinima.getConsumoMinimoSubcategoria() * dadosCategoria.getQtdEconomiasSubcategoria();
					calculoPorSubcategoria = true;

					LogUtil.salvarLog(logType, "[Tarifa Minima] - Data Vigencia: " + tarifacaoMinima.getDataVigencia());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo: " + tarifacaoMinima.getCodigo());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo Categoria: " + tarifacaoMinima.getCodigoCategoria());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo Subcategoria: " + tarifacaoMinima.getCodigoSubcategoria());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Consumo Minimo Subcategoria: " + tarifacaoMinima.getConsumoMinimoSubcategoria());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Qtd Economias Subcategoria: " + dadosCategoria.getQtdEconomiasSubcategoria());
					LogUtil.salvarLog(logType, "[Tarifa Minima] - Consumo Minimo Imovel: " + consumoMinimoImovel);

					break;
				}
			}
			
			if (!calculoPorSubcategoria) {
				// Por Categoria
				for (int j = 0; j < imovel.getTarifacoesMinimas().size(); j++) {

					TarifacaoMinima tarifacaoMinima = (TarifacaoMinima) imovel.getTarifacoesMinimas().get(j);

					if (Util.compararData(dataInicioVigencia, tarifacaoMinima.getDataVigencia()) == 0 
							&& imovel.getCodigoTarifa() == tarifacaoMinima.getCodigo()
							&& dadosCategoria.getCodigoCategoria() == tarifacaoMinima.getCodigoCategoria()
							&& (tarifacaoMinima.getCodigoSubcategoria() == Constantes.NULO_INT || tarifacaoMinima.getCodigoSubcategoria() == 0)) {

						consumoMinimoImovel += tarifacaoMinima.getConsumoMinimoSubcategoria() * dadosCategoria.getQtdEconomiasSubcategoria();

						LogUtil.salvarLog(logType, "[Tarifa Minima] - Data Vigencia: " + tarifacaoMinima.getDataVigencia());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo: " + tarifacaoMinima.getCodigo());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo Categoria: " + tarifacaoMinima.getCodigoCategoria());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Codigo Subcategoria: " + tarifacaoMinima.getCodigoSubcategoria());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Consumo Minimo Subcategoria: " + tarifacaoMinima.getConsumoMinimoSubcategoria());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Qtd Economias Subcategoria: " + dadosCategoria.getQtdEconomiasSubcategoria());
						LogUtil.salvarLog(logType, "[Tarifa Minima] - Consumo Minimo Imovel: " + consumoMinimoImovel);

						break;
					}
				}
			}
		}

		return consumoMinimoImovel;
	}


    /**
     * [UC0743] Calcular Valores de Água/Esgoto
     */
	public void calcularValores(Imovel imovel, Consumo consumo, int tipoMedicao) {

		Object[] dadosCalculo = this.deveAplicarCalculoSimples(imovel);
		boolean calculoSimples = ((Boolean) dadosCalculo[0]).booleanValue();
		Date[] dataInicioVigencia = (Date[]) dadosCalculo[1];
		
		if (calculoSimples) {
			this.calculoSimples(imovel, consumo, tipoMedicao, dataInicioVigencia[0], true);
		} else {
			this.calculoProporcionalMaisUmaTarifa(imovel, consumo, tipoMedicao);
		}

		LogUtil.salvarLog("calcularValores", "Valor Agua: " + getImovelSelecionado().getValorAgua()
					+ " | Valor Esgoto: " + getImovelSelecionado().getValorEsgoto()
					+ " | Valor Debitos: " + getImovelSelecionado().getValorDebitos()
					+ " | Valor Rateio Agua: " + getImovelSelecionado().getValorRateioAgua()
					+ " | Valor Rateio Esgoto: " + getImovelSelecionado().getValorRateioEsgoto()
					+ " | Valores Impostos: " + getImovelSelecionado().getValores());
		LogUtil.salvarLog("calcularValores", "Valor da Conta: " + getImovelSelecionado().getValorConta());
	}
    
    public Object[] deveAplicarCalculoSimples(Imovel imovel) {

		Object[] retorno = new Object[2];
		Boolean calculoSimples = Boolean.TRUE;
		int tamanho = 0;
		if (imovel.getTarifacoesMinimas() != null){
			tamanho = imovel.getTarifacoesMinimas().size();
	    }
		
		Date[] dataInicioVigencia = new Date[tamanho];
		
		int indiceData = 0;
		// TarifacaoMinima registro = ( TarifacaoMinima ) this.registrosTipo9.elementAt( i
		// );
		Date data0 = ((TarifacaoMinima) imovel.getTarifacoesMinimas().get(0))
			.getDataVigencia();
	
		dataInicioVigencia[indiceData] = data0;
	
		Date data1 = null;
		for (int i = 1; i < tamanho; i++) {
		    data1 = ((TarifacaoMinima) imovel.getTarifacoesMinimas().get(i))
			    .getDataVigencia();
	
		    if (Util.compararData(data0, data1) != 0) {
			calculoSimples = Boolean.FALSE;
			indiceData++;
			data0 = data1;
			dataInicioVigencia[indiceData] = data0;
		    }
		}
		retorno[0] = calculoSimples;
		retorno[1] = dataInicioVigencia;
		return retorno;
    }
    
    /**
     * [SB0001] - Cálculo Simples para uma Única Tarifa.
	 * @return
	 */
    @SuppressWarnings("unchecked")
	private void calculoSimples(Imovel imovel, Consumo consumo, int tipoMedicao, Date dataInicioVigencia, boolean isCalculoSimples) {
    	logType = "calculoSimples";
    	
		LogUtil.salvarLog(logType, "Tipo Calculo Tarifa: " + imovel.getTipoCalculoTarifa());
		
		// Verificamos se o tipo de calculo é por categoria ou por subcategoria
		boolean tipoTarifaPorCategoria = ControladorImovel.getInstancia().tipoTarifaPorCategoria(imovel);

		// Consumo minimo do imovel
		int consumoMinimoImovel = 0;
		int excessoImovel = 0;

		if (imovel.getTipoCalculoTarifa() == Tarifa.TIPO_CALCULO_TARIFA_3) {
			// Calculamos o consumo minimo do imovel com a data de vigencia do primeiro dado tarifa que encontramos
			if (imovel.getIndcCondominio() == Constantes.SIM && imovel.getMatriculaCondominio() == Constantes.NULO_INT) {
				consumoMinimoImovel = 0;
			} else {
				consumoMinimoImovel = ControladorImovel.getInstancia().calcularConsumoMinimoImovel(imovel, dataInicioVigencia);
			}

			// Calculamos o consumo em excesso, onde retiramos o consumo a ser cobrado do mes o consumo minimo do imovel
			Double consumoCobradoMes;

			// Calculo de Condominio
			if (imovel.getIndcCondominio() == Constantes.SIM && imovel.getMatriculaCondominio() == Constantes.NULO_INT) {
				consumoCobradoMes = new Double(imovel.getEfetuarRateioConsumoHelper().getConsumoParaRateioAgua());
			} else {
				consumoCobradoMes = new Double(consumo.getConsumoCobradoMes());
			}
			
			excessoImovel = consumoCobradoMes.intValue() - consumoMinimoImovel;
			LogUtil.salvarLog(logType, "Consumo em Excesso: " + excessoImovel);
		}

		// Calculamos o consumo por economia
		int consumoPorEconomia;

		// Calculo de Condominio
		if (imovel.getIndcCondominio() == Constantes.SIM && imovel.getMatriculaCondominio() == Constantes.NULO_INT) {
			consumoPorEconomia = imovel.getEfetuarRateioConsumoHelper().getConsumoParaRateioAgua() / imovel.getQuantidadeEconomiasTotal();
		} else {
			consumoPorEconomia = consumo.getConsumoCobradoMes() / imovel.getQuantidadeEconomiasTotal();
		}

		LogUtil.salvarLog(logType, "Consumo Por Economia: " + consumoPorEconomia);

		// Para cada registro de Dados de Categoria
		for (int i = 0; i < imovel.getDadosCategoria().size(); i++) {

			// Pegamos o regitro tipo 2 atual
			DadosCategoria dadosEconomiasSubcategorias = (DadosCategoria) imovel.getDadosCategoria().get(i);

			// Quantidade de economias para cada categoria ou subcategoria
			int quantidadeEconomiasCategoriaSubCategoria;

			if (!dadosEconomiasSubcategorias.getFatorEconomiaCategoria().trim().equals(Constantes.NULO_STRING)) {
				quantidadeEconomiasCategoriaSubCategoria = Integer.parseInt(dadosEconomiasSubcategorias.getFatorEconomiaCategoria().trim());
			} else {
				if (tipoTarifaPorCategoria) {
					quantidadeEconomiasCategoriaSubCategoria = imovel.getQuantidadeEconomias(dadosEconomiasSubcategorias.getCodigoCategoria(),
							dadosEconomiasSubcategorias.getCodigoSubcategoria());
				} else {
					quantidadeEconomiasCategoriaSubCategoria = dadosEconomiasSubcategorias.getQtdEconomiasSubcategoria();
				}

			}

	    	LogUtil.salvarLog(logType, "Categoria: " + dadosEconomiasSubcategorias.getCodigoCategoria()
	    			+ " | Subcategoria: " + dadosEconomiasSubcategorias.getCodigoSubcategoria()
	    			+ " | Qtd de Economias: " + quantidadeEconomiasCategoriaSubCategoria);
			
			// Seleciona as tarifas de consumo para cada categoria ou subcategoria do imovel
			TarifacaoMinima tarifacaoMinima = null;

			if (tipoTarifaPorCategoria) {
				tarifacaoMinima = imovel.pesquisarDadosTarifaMinimaImovel(tipoTarifaPorCategoria, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
						dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);
			} else {
				tarifacaoMinima = imovel.pesquisarDadosTarifaMinimaImovel(tipoTarifaPorCategoria, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
						dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);

				if (tarifacaoMinima == null) {
					tarifacaoMinima = imovel.pesquisarDadosTarifaMinimaImovel(true, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
							dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);
				}
			}

			dadosEconomiasSubcategorias.setTarifa(new DadosTarifa(tarifacaoMinima));

			double valorTarifaMinima = 0;
			int consumoMinimo = 0;
			double valorPorEconomia = 0;
			int consumoEconomiaCategoriaOuSubcategoria = 0;

			if (imovel.getIndcCondominio() == Constantes.SIM && imovel.getMatriculaCondominio() == Constantes.NULO_INT && consumoPorEconomia < 10) {
				if (consumoPorEconomia > 0) {

					double faixaInicial = ((TarifacaoComplementar) imovel.getTarifacoesComplementares().get(0)).getValorM3Faixa();
					valorTarifaMinima = faixaInicial / consumoPorEconomia;
					consumoMinimo = consumoPorEconomia;
				}
			} else {
				// Calcula os seguintes valores, da categoria ou subcategoria pesquisada
				valorTarifaMinima = tarifacaoMinima.getTarifaMinimaCategoria() * quantidadeEconomiasCategoriaSubCategoria;

				consumoMinimo = tarifacaoMinima.getConsumoMinimoSubcategoria() * quantidadeEconomiasCategoriaSubCategoria;

				valorPorEconomia = tarifacaoMinima.getTarifaMinimaCategoria();

				if (consumoPorEconomia > tarifacaoMinima.getConsumoMinimoSubcategoria()) {
					consumoEconomiaCategoriaOuSubcategoria = tarifacaoMinima.getConsumoMinimoSubcategoria();
				} else {
					consumoEconomiaCategoriaOuSubcategoria = consumoPorEconomia;
				}
			}
			
			int consumoExcedente = 0;

			if (imovel.getTipoCalculoTarifa() == Tarifa.TIPO_CALCULO_TARIFA_3) {
				if (excessoImovel > 0) {
					consumoExcedente = (int) (excessoImovel * consumoMinimo) / consumoMinimoImovel;
					consumoExcedente = consumoExcedente / quantidadeEconomiasCategoriaSubCategoria;
				}
			} else {
				consumoExcedente = consumoPorEconomia - tarifacaoMinima.getConsumoMinimoSubcategoria();
			}
			
			List<DadosFaturamentoFaixa> faixasParaInclusao = new ArrayList<DadosFaturamentoFaixa>();
			
			LogUtil.salvarLog(logType, "Consumo Excedente: " + consumoExcedente);
			
			if (consumoExcedente > 0) {
				List<TarifacaoComplementar> tarifacoesComplementares = null;
				
				LogUtil.salvarLog(logType, "Tipo Tarifa Por Categoria: " + tipoTarifaPorCategoria);

				if (tipoTarifaPorCategoria) {
					tarifacoesComplementares = imovel.selecionarTarifasComplementaresParaCalculo(tipoTarifaPorCategoria, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
							dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);
				} else {
					tarifacoesComplementares = imovel.selecionarTarifasComplementaresParaCalculo(tipoTarifaPorCategoria, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
							dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);

					if (tarifacoesComplementares == null || tarifacoesComplementares.isEmpty()) {
						tarifacoesComplementares = imovel.selecionarTarifasComplementaresParaCalculo(true, dadosEconomiasSubcategorias.getCodigoCategoria() + "",
								dadosEconomiasSubcategorias.getCodigoSubcategoria(), imovel.getCodigoTarifa(), dataInicioVigencia);
					}
				}

				LogUtil.salvarLog(logType, "Qtd Tarifas Complementares Selecionadas: " + tarifacoesComplementares.size());
				
				int consumoFaturadoFaixa = 0;
				double valorFaturadoFaixa = 0d;
				int limiteInicialConsumoFaixa = 0;
				int limiteFinalConsumoFaixa = 0;
				double valorTarifaFaixa = 0d;
				int limiteFaixaFimAnterior = 0;
				
				if (tarifacaoMinima.getConsumoMinimoSubcategoria() != Constantes.NULO_INT) {
					limiteFaixaFimAnterior = tarifacaoMinima.getConsumoMinimoSubcategoria();
				}

				
				DadosFaturamentoFaixa faixaParaInclusao = null;
				
				if (tarifacoesComplementares != null && !tarifacoesComplementares.isEmpty()) {
					if (imovel.getTipoCalculoTarifa() == Tarifa.TIPO_CALCULO_TARIFA_2) {
						for (int j = 0; j < tarifacoesComplementares.size(); j++) {
							TarifacaoComplementar faixa = (TarifacaoComplementar) tarifacoesComplementares.get(j);
							
							if (consumoExcedente <= faixa.getLimiteFinalFaixa() && consumoExcedente >= faixa.getLimiteInicialFaixa()) {
								
								valorFaturadoFaixa = this.calcularValorFaturadoFaixa(consumoPorEconomia, valorTarifaMinima, faixa.getValorM3Faixa());
								
								if (consumoExcedente > 0 && valorFaturadoFaixa == 0d) {
									getImovelSelecionado().setTarifacaoComplementarNulaOuZerada(true);
									LogUtil.salvarLog(logType, "Lista de Tarifas Complementares ZERADA");
								}
								
								limiteInicialConsumoFaixa = faixa.getLimiteInicialFaixa();
								limiteFinalConsumoFaixa = faixa.getLimiteFinalFaixa();
								
								consumoFaturadoFaixa = faixa.getLimiteFinalFaixa() - limiteFaixaFimAnterior;
								
								faixaParaInclusao = new DadosFaturamentoFaixa(consumoFaturadoFaixa, valorFaturadoFaixa, limiteInicialConsumoFaixa, limiteFinalConsumoFaixa, faixa.getValorM3Faixa());
								faixaParaInclusao.setTipoFaturamentoFaixa(tipoMedicao);

								faixasParaInclusao.add(faixaParaInclusao);
								valorPorEconomia = valorFaturadoFaixa;
								consumoEconomiaCategoriaOuSubcategoria = consumoFaturadoFaixa;
								
								LogUtil.salvarLog(logType, "Valor Tarifa Faixa: " + faixa.getValorM3Faixa()
										+ " | Limite Inicial Consumo Faixa: " + faixa.getLimiteInicialFaixa()
										+ " | Limite Final Consumo Faixa: " + faixa.getLimiteFinalFaixa());
								
								break;
							}
						}
					} else {
						for (int j = 0; j < tarifacoesComplementares.size() && consumoExcedente > 0; j++) {
							TarifacaoComplementar faixa = (TarifacaoComplementar) tarifacoesComplementares.get(j);
							
							consumoFaturadoFaixa = (faixa.getLimiteFinalFaixa() - limiteFaixaFimAnterior);
							
							if (consumoExcedente < consumoFaturadoFaixa) {
								consumoFaturadoFaixa = consumoExcedente;
							}
							
							valorFaturadoFaixa = consumoFaturadoFaixa * faixa.getValorM3Faixa();
							
							if (consumoExcedente > 0 && valorFaturadoFaixa == 0d) {
								getImovelSelecionado().setTarifacaoComplementarNulaOuZerada(true);
								LogUtil.salvarLog(logType, "Lista de Tarifas Complementares ZERADA");
							}
							
							valorPorEconomia += valorFaturadoFaixa;
							
							consumoEconomiaCategoriaOuSubcategoria += consumoFaturadoFaixa;
							
							limiteInicialConsumoFaixa = faixa.getLimiteInicialFaixa();
							limiteFinalConsumoFaixa = faixa.getLimiteFinalFaixa();
							valorTarifaFaixa = faixa.getValorM3Faixa();
							
							consumoExcedente -= consumoFaturadoFaixa;
							
							faixaParaInclusao = new DadosFaturamentoFaixa(consumoFaturadoFaixa, valorFaturadoFaixa, limiteInicialConsumoFaixa, limiteFinalConsumoFaixa,valorTarifaFaixa);
							faixaParaInclusao.setTipoFaturamentoFaixa(tipoMedicao);
							faixasParaInclusao.add(faixaParaInclusao);
							
							limiteFaixaFimAnterior = faixa.getLimiteFinalFaixa();
							
							LogUtil.salvarLog(logType, "Valor Tarifa Faixa: " + faixa.getValorM3Faixa()
									+ " | Limite Inicial Consumo Faixa: " + faixa.getLimiteInicialFaixa()
									+ " | Limite Final Consumo Faixa: " + faixa.getLimiteFinalFaixa());
						}
					}
				} else {
					getImovelSelecionado().setTarifacaoComplementarNulaOuZerada(true);
					LogUtil.salvarLog(logType, "Lista de Tarifas Complementares NULA");
				}

				LogUtil.salvarLog(logType, "Valor Faturado Faixa: " + valorFaturadoFaixa
						+ " | Valor por Economia: " + valorPorEconomia
						+ " | Consumo ECONOMIA Categoria Ou Subcategoria: " + consumoEconomiaCategoriaOuSubcategoria);
			}

			double valorFaturado = 0d;
			if (imovel.getTipoCalculoTarifa() == Tarifa.TIPO_CALCULO_TARIFA_2) {
				valorFaturado = valorPorEconomia;

			} else {
				valorFaturado = valorPorEconomia * quantidadeEconomiasCategoriaSubCategoria;
			}

			int consumoFaturadoCategoriaOuSubcategoria = 0;
			if (imovel.getTipoCalculoTarifa() == Tarifa.TIPO_CALCULO_TARIFA_2) {
				consumoFaturadoCategoriaOuSubcategoria = consumoEconomiaCategoriaOuSubcategoria;
			} else {
				consumoFaturadoCategoriaOuSubcategoria = consumoEconomiaCategoriaOuSubcategoria * quantidadeEconomiasCategoriaSubCategoria;
			}
			

			if (tipoMedicao == Constantes.LIGACAO_POCO) {
				valorFaturado = Util.arredondar(valorFaturado * (imovel.getPercentCobrancaEsgoto() / 100), 2);
				valorTarifaMinima = Util.arredondar(valorTarifaMinima * (imovel.getPercentCobrancaEsgoto() / 100), 2);
			}

			//chamada do metodo que faz o calculo do uso cheio do credito Bolsa Água

			DadosFaturamento faturamento = null;

			if (isCalculoSimples) {
				faturamento = calculoValorFaturadoBolsaAgua(
						imovel,
						tipoMedicao,
						valorFaturado,
						consumoFaturadoCategoriaOuSubcategoria,
						quantidadeEconomiasCategoriaSubCategoria,
						valorTarifaMinima,
						consumoMinimo,
						faixasParaInclusao,
						dadosEconomiasSubcategorias);
			} else {
				faturamento = new DadosFaturamento(
						valorFaturado,
						consumoFaturadoCategoriaOuSubcategoria,
						valorTarifaMinima,
						consumoMinimo,
						faixasParaInclusao);
			}

			LogUtil.salvarLog(logType, "Valor Faturado: " + valorFaturado
					+ " | Consumo FATURADO Categoria Ou Subcategoria: " + consumoFaturadoCategoriaOuSubcategoria
					+ " | Valor Tarifa Minima: " + valorTarifaMinima
					+ " | Consumo Minimo: " + consumoMinimo);

			atribuirDadosFaturamento(imovel, tipoMedicao, dadosEconomiasSubcategorias, faturamento);
		}
	}

	private void atribuirDadosFaturamento(Imovel imovel, int tipoMedicao, DadosCategoria dadosEconomiasSubcategorias, DadosFaturamento faturamento) {
		faturamento.setIdDadosCategoria(dadosEconomiasSubcategorias.getId());
		faturamento.setTipoFaturamento(tipoMedicao);
		int posicao = imovel.getDadosCategoria().indexOf(dadosEconomiasSubcategorias);
		if (tipoMedicao == Constantes.TIPO_FATURAMENTO_AGUA) {
			imovel.getDadosCategoria().get(posicao).setFaturamentoAgua(faturamento);
		} else if (tipoMedicao == Constantes.TIPO_FATURAMENTO_ESGOTO) {
			imovel.getDadosCategoria().get(posicao).setFaturamentoEsgoto(faturamento);
		}

		if (tipoMedicao == ControladorConta.LIGACAO_AGUA) {
			dadosEconomiasSubcategorias.setFaturamentoAgua(faturamento);
		} else {
			dadosEconomiasSubcategorias.setFaturamentoEsgoto(faturamento);
		}
	}

	private DadosFaturamento calculoValorFaturadoBolsaAgua(
			Imovel imovel,
			int tipoMedicao,
			double valorFaturado,
			int consumoFaturadoCategoriaOuSubcategoria,
			int quantidadeEconomiasCategoriaSubCategoria,
			double valorTarifaMinima,
			int consumoMinimo,
			List<DadosFaturamentoFaixa> faixasParaInclusao,
			DadosCategoria dadosEconomiasSubcategorias) {
		//variavel utilizada para a atualização do valor do credito do água pará
		Double valorAguaEsgotoFinal;
		//lista de creditos
		List<Credito> creditos = this.getImovelSelecionado().getCreditos();

		//Verifica se o imovel já foi calculado
		if (getImovelSelecionado().getIndcImovelCalculado() != Constantes.SIM) {
			//Verificação se o imovel tem economias residenciais e comercial
			if (dadosEconomiasSubcategorias.getCodigoCategoria() == DadosCategoria.RESIDENCIAL) {
				//Verificação se o imovel tem um ou mais pontos de consumo
				//Imovel com somente UM ponto de consumo
				if (quantidadeEconomiasCategoriaSubCategoria == 1) {
					//Verifica se o imovel tem faturamento Água é Esgoto
					if (imovel.getIndcFaturamentoAgua() == SIM && imovel.getIndcFaturamentoEsgoto() == SIM) {
						//verifica se o tipo medição atual é de água
						if (tipoMedicao == Constantes.LIGACAO_AGUA) {
							//calculo do valor que equivale a cota do credito destinado a água
							double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua() * (62.5 / 100), 2);
							//verifica se o valor faturado é menor que o valor da cota do bolsa água
							if (valorFaturado <= valorBolsaAgua) {
								valorFaturado = valorBolsaAgua;
								if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
									consumoFaturadoCategoriaOuSubcategoria = 20;
								}
							}
						}
						if (tipoMedicao == Constantes.LIGACAO_POCO) {
							//calculo do valor que equivale a cota do credito destinado a água
							double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua() * (37.5 / 100), 2);
							//verifica se o valor faturado é menor que o valor da cota do bolsa água
							if (valorFaturado <= valorBolsaAgua) {
								valorFaturado = valorBolsaAgua;
							}
						}
					}
					//Verifica se o imovel tem faturamento Água sem Esgoto
					if (imovel.getIndcFaturamentoAgua() == SIM && imovel.getIndcFaturamentoEsgoto() == NAO) {
						double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua(), 2);
						//verifica se o valor faturado é menor que o valor da cota do bolsa água
						if (valorFaturado < valorBolsaAgua) {
							valorFaturado = valorBolsaAgua;
							if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
								consumoFaturadoCategoriaOuSubcategoria = 20;
							}
						}
					}
					//Verifica se o imovel tem faturamento Esgoto sem Água
					if (imovel.getIndcFaturamentoEsgoto() == SIM && imovel.getIndcFaturamentoAgua() == NAO) {
						double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua(), 2);
						//verifica se o valor faturado é menor que o valor da cota do bolsa água
						if (valorFaturado < valorBolsaAgua) {
							valorFaturado = valorBolsaAgua;
							if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
								consumoFaturadoCategoriaOuSubcategoria = 20;
							}
						}
					}
					//Imovel com MAIS DE UM ponto de consumo
				} else {
					//Verifica se o imovel tem faturamento Água é Esgoto
					if (imovel.getIndcFaturamentoAgua() == SIM && imovel.getIndcFaturamentoEsgoto() == SIM) {
						//verifica se o tipo medição atual é de água
						if (tipoMedicao == Constantes.LIGACAO_AGUA) {
							//calculo do valor que equivale a cota do credito destinado a água
							double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua() * (51.47 / 100), 2);
							//verifica se o consumo é menor que 20

							//verifica se o valor faturado é menor ou igual que o valor da cota do bolsa água
							if (valorFaturado <= valorBolsaAgua) {
								valorFaturado = valorBolsaAgua;
								//verifica se o consumo é menor igual que 20
								if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
									consumoFaturadoCategoriaOuSubcategoria = 20;
								}
								for (int i = 0; i < creditos.size(); i++) {
									Credito credito = (Credito) creditos.get(i);
									if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
										credito.setValor(String.valueOf(valorBolsaAgua + Util.arredondar(valorBolsaAgua * 0.6, 2)));
										ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
									}
								}
							}  else if (valorFaturado > valorBolsaAgua) {
								for (int i = 0; i < creditos.size(); i++) {
									Credito credito = (Credito) creditos.get(i);
									if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
										credito.setValor(String.valueOf(valorBolsaAgua + Util.arredondar(valorBolsaAgua * 0.6, 2)));
										ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
									}
								}
							}
						}
					}
					//Verifica se o imovel tem faturamento Água sem Esgoto
					if (imovel.getIndcFaturamentoAgua() == SIM && imovel.getIndcFaturamentoEsgoto() == NAO) {
						double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua() * (82.35 / 100), 2);
						//verifica se o valor faturado é menor que o valor da cota do bolsa água
						if (valorFaturado <= valorBolsaAgua) {
							valorFaturado = valorBolsaAgua;
							//verifica se o consumo é menor que 20
							if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
								consumoFaturadoCategoriaOuSubcategoria = 20;
							}
							for (int i = 0; i < creditos.size(); i++) {
								Credito credito = (Credito) creditos.get(i);
								if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
									credito.setValor(String.valueOf(valorFaturado));
									ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
								}
							}
						} else if (valorFaturado > valorBolsaAgua) {
							for (int i = 0; i < creditos.size(); i++) {
								Credito credito = (Credito) creditos.get(i);
								if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
									credito.setValor(String.valueOf(valorBolsaAgua));
									ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
								}
							}
						}
					}
					//Verifica se o imovel tem faturamento Esgoto sem Água
					if (imovel.getIndcFaturamentoEsgoto() == SIM && imovel.getIndcFaturamentoAgua() == NAO) {
						double valorBolsaAgua = Util.arredondar(imovel.getValorCreditosBolsaAgua() * (82.35 / 100), 2);
						//verifica se o valor faturado é menor que o valor da cota do bolsa água
						if (valorFaturado <= valorBolsaAgua) {
							valorFaturado = valorBolsaAgua;
							for (int i = 0; i < creditos.size(); i++) {
								Credito credito = (Credito) creditos.get(i);
								if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
									credito.setValor(String.valueOf(valorFaturado));
									ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
								}
							}
							//verifica se o consumo é menor que 20
							if (consumoFaturadoCategoriaOuSubcategoria <= 20) {
								consumoFaturadoCategoriaOuSubcategoria = 20;
							}
						} else if (valorFaturado > valorBolsaAgua) {
							for (int i = 0; i < creditos.size(); i++) {
								Credito credito = (Credito) creditos.get(i);
								if (credito.getCodigo().equals(CRED_BOLSA_AGUA)) {
									credito.setValor(String.valueOf(valorBolsaAgua));
									ControladorRota.getInstancia().getDataManipulator().updateCredito(imovel.getMatricula(), credito);
								}
							}
						}
					}
				}
			}
		}
		return new DadosFaturamento(
				valorFaturado,
				consumoFaturadoCategoriaOuSubcategoria,
				valorTarifaMinima,
				consumoMinimo,
				faixasParaInclusao);
	}

	private double calcularPercentual(double valorFaturado, double valorCreditosBolsaAgua) {
		double percentual = (valorFaturado / valorCreditosBolsaAgua) * 100;

		return new BigDecimal(percentual)
				.setScale(2, BigDecimal.ROUND_HALF_DOWN)
				.doubleValue();
	}
    
    /**
     * Calculamos o valor a ser faturado na faixa
     * 
     * @param consumoFaturado
     *            consumo que foi faturafo
     * @param valorTarifaMinimaCategoria
     *            Tarifa minima da categoria
     * @param valorTarifaFaixa
     *            Tarifa na faixa
     * @return
     */
    private double calcularValorFaturadoFaixa(int consumoFaturado, double valorTarifaMinimaCategoria, double valorTarifaFaixa) {

		// Legenda: x = consumoFaturado; NI = valorTarifaMinima
	
		double retorno = 0;
	
		int CONSUMO_SUPERIOR = 201;
	
		// Consumidores Taxados
		if (consumoFaturado < CONSUMO_SUPERIOR) {
	
		    double divisor = 10000;
		    int multiplicadorExp = 7;
	
		    // NI
		    if (consumoFaturado <= 10) {
		    	retorno = valorTarifaMinimaCategoria;
		    }
		    // NI(7x² + valorTarifaFaixa * x) / 10000
		    else {
	
				double parcial = consumoFaturado * consumoFaturado;
				parcial = parcial * multiplicadorExp;
		
				double parcial2 = valorTarifaFaixa * consumoFaturado;
		
				double parcialFinal = parcial + parcial2;
		
				parcialFinal = valorTarifaMinimaCategoria * parcialFinal;
		
				retorno = parcialFinal / divisor;
		    }
		}
		// Consumo Superior = NI(valorTarifaFaixa * x - 11,2)
		else {
	
		    double valor1 = 11.2;
	
		    double parcial = valorTarifaFaixa * consumoFaturado;
	
		    parcial = parcial - valor1;
	
		    retorno = valorTarifaMinimaCategoria * parcial;
		}
	
		return retorno;
    }

    /**
     * [UC0743] Calcular Valores de Água/Esgoto no Dispositivo Móvel [SB0002 –
     * Cálculo Proporcional Para Mais de Uma Tarifa]
     * 
     * @author Bruno Barros
     * @date 16/10/2009
     */
	public void calculoProporcionalMaisUmaTarifa(Imovel imovel, Consumo consumo, int tipoMedicao) {
		/*
		 * 1.O sistema seleciona as tarifas vigentes para o imóvel no período de
		 * leitura da seguinte forma: 1.1.Seleciona todas as ocorrências de
		 * registro 9 com código da tarifa igual ao código da tarifa do imóvel e
		 * com data de início da vigência entre as datas de leitura anterior e a
		 * data corrente, inclusive; 1.2.Caso não seja selecionada nenhuma
		 * ocorrência no item 1.1 ou caso nenhuma ocorrência selecionada tenha
		 * data de início da vigência=data de leitura anterior, selecionar
		 * também a ocorrência de registro 9 com código da tarifa igual ao
		 * código da tarifa do imóvel e com a maior data de início da vigência
		 * que seja menor que a data de leitura anterior. 1.3.As tarifas
		 * vigentes para o período de leitura serão as ocorrências selecionadas
		 * nos passos 1.1 e 1.2 e devem estar classificadas pela data de início
		 * da vigência. TODOS OS REGISTROS TIPO 9 PRESENTES NO IMOVEL JA
		 * RESPEITAM AS CONDIÇÕES SUPRECITADAS, LOGO APENAS SELECIONAMOS
		 */
		logType = "calculoProporcionalMaisUmaTarifa";

		boolean calculado = false;

		ArrayList<List<TarifacaoMinima>> tarifacoesMinimasPorCategoria = imovel.getTarifacoesMinimasPorCategoria();

		TarifacaoMinima codigoCategoria = null;
		double valorFaturadoBolsaAgua = 0;
		double valorFaturadoBolsaAguaMinimo = 0;
		Vector faixasProporcional = new Vector();

		if (getImovelSelecionado().getIndcImovelCalculado() == Constantes.NAO) {

			// Selecionamos a data de leitura anterior dando prioridade ao ligação de água
			Date dataLeituraAnterior = null;
			if (imovel.getMedidor(Constantes.LIGACAO_AGUA) != null && !imovel.getMedidor(Constantes.LIGACAO_AGUA).equals("")) {
				dataLeituraAnterior = imovel.getMedidor(Constantes.LIGACAO_AGUA).getDataLeituraAnteriorFaturada();
			} else {
				if (imovel.getMedidor(Constantes.LIGACAO_POCO) != null && !imovel.getMedidor(Constantes.LIGACAO_POCO).equals("")) {
					dataLeituraAnterior = imovel.getMedidor(Constantes.LIGACAO_POCO).getDataLeituraAnteriorFaturada();
				}
			}

			if (dataLeituraAnterior == null) {
				dataLeituraAnterior = imovel.getDataLeituraAnteriorNaoMedido();
			}

			Date dataLeituraAtual = Util.getDataSemHora(MedidorAguaTab.getCurrentDateByGPS());

			// 2.Calcula a quantidade de dias entre as leituras = data corrente - data de leitura anterior;
			long qtdDiasEntreLeituras = Util.obterModuloDiferencasDatasDias(dataLeituraAtual, dataLeituraAnterior);
			qtdDiasEntreLeituras += 1;

			DadosFaturamento dadosFaturamento = null;
			// cria o objteto de faturamento proporcional para somar os valores por categoria de cada data de vigencia
			DadosFaturamento dadosFaturamentoProporcional = null;

			for (List<TarifacaoMinima> tarifacoesMinimas : tarifacoesMinimasPorCategoria) {

				// 3.Data da vigência inicial = data da leitura anterior
				Date dataVigenciaInicial = dataLeituraAnterior;
				int contador = 0;
				// 4.Para cada tarifa vigente para o período de leitura, obtida no passo 1.3, ordenando por data de início da
				// vigência, o sistema efetua os seguintes procedimentos
				for (int i = 0; i < tarifacoesMinimas.size(); i++) {
					// 4.1.[SB0001 – Cálculo Simples Para Uma Única Tarifa];
					TarifacaoMinima reg9 = (TarifacaoMinima) tarifacoesMinimas.get(i);
					this.calculoSimples(imovel, consumo, tipoMedicao, reg9.getDataVigencia(), false);
					codigoCategoria = tarifacoesMinimas.get(i);
					// 4.2.Caso exista próxima tarifa vigente então data da vigência final = data de início da vigência da próxima
					// tarifa vigente menos um dia, caso contrário, data da vigência final = data corrente;
					Date dataVigenciaFinal = null;

					if (i < tarifacoesMinimas.size() - 1) {
						TarifacaoMinima proxReg9 = (TarifacaoMinima) tarifacoesMinimas.get(i + 1);

						if (proxReg9.getDataVigencia().before(dataLeituraAtual) || proxReg9.getDataVigencia().equals(dataLeituraAtual)) {
							dataVigenciaFinal = Util.adicionarNumeroDiasDeUmaData(proxReg9.getDataVigencia(), -1);
						} else {
							dataVigenciaFinal = dataLeituraAtual;
						}
					} else {
						if (reg9.getDataVigencia().before(dataLeituraAtual) || reg9.getDataVigencia().equals(dataLeituraAtual)) {
							dataVigenciaFinal = dataLeituraAtual;
						} else {
							dataVigenciaFinal = reg9.getDataVigencia();
						}
					}

					// 4.3.Caso seja a primeira tarifa, a quantidade de dias de vigência da tarifa dentro do período
					// de leitura = data da vigência final – data da vigência inicial
					long qtdDiasVigenciaTarifaDentroPeriodoLeitura = Util.obterModuloDiferencasDatasDias(dataVigenciaFinal, dataVigenciaInicial);

					if (i == 0) {
						if (dataVigenciaFinal.after(dataVigenciaInicial) || dataVigenciaFinal.equals(dataVigenciaInicial)) {
							qtdDiasVigenciaTarifaDentroPeriodoLeitura += 1;
						} else {
							qtdDiasVigenciaTarifaDentroPeriodoLeitura = 0;
						}

						// 4.4.Caso contrário a quantidade de dias de vigência da tarifa dentro do período de
						// leitura = data da vigência final – data da vigência inicial + 1 dia;
					} else {
						if (dataVigenciaFinal.before(dataLeituraAtual) || dataVigenciaFinal.equals(dataLeituraAtual)) {
							qtdDiasVigenciaTarifaDentroPeriodoLeitura += 1;
						} else {
							qtdDiasVigenciaTarifaDentroPeriodoLeitura = 0;
						}
					}

					// 4.5.Calcula o fator de vigência da tarifa = quantidade de dias de vigência da tarifa no período de
					// leitura / quantidade de dias entre as leituras;
					double fatorVigenciaTarifa = Util.arredondar((double) qtdDiasVigenciaTarifaDentroPeriodoLeitura / (double) qtdDiasEntreLeituras, 4);

					/*
					 * 1.1.Para cada Categoria, aplica o fator de vigência da tarifa
					 * sobre os seguintes atributos obtidos nos cálculos efetuados no
					 * passo 4.1, arredondando para duas casas decimais: 1.1.1.Valor
					 * faturado; 1.1.2.Valor da tarifa mínima; 1.1.3.Para cada faixa da
					 * tarifa de consumo: 1.1.3.1.Valor faturado na faixa; 1.1.3.2.Valor
					 * da tarifa na faixa.
					 */
					DadosFaturamentoFaixa faixaProporcional = null;
					DadosFaturamentoFaixa faixa = null;
					for (int j = 0; j < imovel.getDadosCategoria().size(); j++) {

						List<DadosCategoria> teste = imovel.getDadosCategoria();
						DadosCategoria reg2 = (DadosCategoria) imovel.getDadosCategoria().get(j);

						if (tipoMedicao == Constantes.LIGACAO_AGUA) {
							dadosFaturamento = reg2.getFaturamentoAgua();
							dadosFaturamentoProporcional = reg2.getFaturamentoAguaProporcional();
						} else {
							dadosFaturamento = reg2.getFaturamentoEsgoto();
							dadosFaturamentoProporcional = reg2.getFaturamentoEsgotoProporcional();
						}

						if (dadosFaturamentoProporcional == null
								|| dadosFaturamentoProporcional.equals("") || i == 0) {
							dadosFaturamentoProporcional = new DadosFaturamento();
							dadosFaturamentoProporcional.setValorFaturado(0d);
							dadosFaturamentoProporcional.setValorTarifaMinima(0d);
						}

						double valorFaturadoPorFator = dadosFaturamentoProporcional.getValorFaturado();
						valorFaturadoPorFator = valorFaturadoPorFator + Util.arredondar(dadosFaturamento.getValorFaturado() * fatorVigenciaTarifa, 2);

						double valorTarifaMinimaPorFator = dadosFaturamentoProporcional.getValorTarifaMinima();
						valorTarifaMinimaPorFator = valorTarifaMinimaPorFator + Util.arredondar(dadosFaturamento.getValorTarifaMinima() * fatorVigenciaTarifa, 2);

						// seta os valores adicionados para os dados de faturamento proporcional
						dadosFaturamentoProporcional.setValorFaturado(valorFaturadoPorFator);
						dadosFaturamentoProporcional.setValorTarifaMinima(valorTarifaMinimaPorFator);

						// seta os dados do faturamento proporcional no dado de faturamento
						dadosFaturamento.setValorFaturado(valorFaturadoPorFator);
						dadosFaturamento.setValorTarifaMinima(valorTarifaMinimaPorFator);

						if(calculado == true){
							if(reg2.getCodigoCategoria() == DadosCategoria.RESIDENCIAL){
								// seta os valores adicionados para os dados de faturamento proporcional
								dadosFaturamentoProporcional.setValorFaturado(valorFaturadoBolsaAgua);
								dadosFaturamentoProporcional.setValorTarifaMinima(valorTarifaMinimaPorFator);

								// seta os dados do faturamento proporcional no dado de faturamento
								dadosFaturamento.setValorFaturado(valorFaturadoBolsaAgua);
								dadosFaturamento.setValorTarifaMinima(valorTarifaMinimaPorFator);
							}

						}

						for (int k = 0; k < dadosFaturamento.getFaixas().size(); k++) {
							faixa = (DadosFaturamentoFaixa) dadosFaturamento.getFaixas().get(k);


							if (dadosFaturamentoProporcional.getFaixas() == null || dadosFaturamentoProporcional.getFaixas().equals("") || i == 0) {
								faixaProporcional = new DadosFaturamentoFaixa();
								faixaProporcional.setValorFaturado(0d);
								faixaProporcional.setValorTarifa(0d);
							} else {
								faixaProporcional = (DadosFaturamentoFaixa) dadosFaturamentoProporcional.getFaixas().get(k);
							}

							double valorFaturadoPorFatorNaFaixa = faixaProporcional.getValorFaturado();
							valorFaturadoPorFatorNaFaixa = valorFaturadoPorFatorNaFaixa + Util.arredondar(faixa.getValorFaturado() * fatorVigenciaTarifa, 2);

							double valorTarifaPorFatorNaFaixa = faixaProporcional.getValorTarifa();
							valorTarifaPorFatorNaFaixa = valorTarifaPorFatorNaFaixa + Util.arredondar(faixa.getValorTarifa() * fatorVigenciaTarifa, 2);

							// seta os valores adicionados para os dados de faturamento proporcional
							faixaProporcional.setValorFaturado(valorFaturadoPorFatorNaFaixa);
							faixaProporcional.setValorTarifa(valorTarifaPorFatorNaFaixa);
							faixasProporcional.addElement(faixaProporcional);

							// seta os dados do faturamento proporcional no dado de faturamento
							faixa.setValorFaturado(valorFaturadoPorFatorNaFaixa);
							faixa.setValorTarifa(valorTarifaPorFatorNaFaixa);

							}
						if(calculado == false){

							if (reg2.getCodigoCategoria() == DadosCategoria.RESIDENCIAL) {
								valorFaturadoBolsaAgua = valorFaturadoPorFator;
								contador++;
							}
							if (reg2.getCodigoCategoria() == DadosCategoria.RESIDENCIAL) {
								valorFaturadoBolsaAguaMinimo = valorTarifaMinimaPorFator;
							}/* else {

								faixaProporcional.setValorFaturado(valorFaturadoPorFatorNaFaixa);
								faixaProporcional.setValorTarifa(valorTarifaPorFatorNaFaixa);
								faixasProporcional.addElement(faixaProporcional);

								// seta os dados do faturamento proporcional no dado de faturamento
								faixa.setValorFaturado(valorFaturadoPorFatorNaFaixa);
								faixa.setValorTarifa(valorTarifaPorFatorNaFaixa);

							}*/

						}


						// seta as faixas proporcionais no dado de faturamento proporcional
						dadosFaturamentoProporcional.setFaixas(faixasProporcional);
						if (tipoMedicao == Constantes.LIGACAO_AGUA) {
							reg2.setFaturamentoAguaProporcional(dadosFaturamentoProporcional);
						} else {
							reg2.setFaturamentoEsgotoProporcional(dadosFaturamentoProporcional);
						}

					}
					if (codigoCategoria.getCodigoCategoria() == DadosCategoria.RESIDENCIAL && contador == 2 && imovel.getValorCreditosBolsaAgua() > 0.0) {
						// TODO - calcular novo valor faturado para o bolsa agua
						DadosCategoria dadosCategoria = null;
						for(DadosCategoria dados: imovel.getDadosCategoria()){
                               if(dados.getCodigoCategoria() == DadosCategoria.RESIDENCIAL){
								   dadosCategoria = dados;
							   }
						}
						DadosFaturamento faturamento = calculoValorFaturadoBolsaAgua(
								imovel,
								tipoMedicao,
								valorFaturadoBolsaAgua,
								dadosFaturamento.getConsumoFaturado(),
								dadosCategoria.getQtdEconomiasSubcategoria(),
								valorFaturadoBolsaAguaMinimo,
								dadosFaturamento.getConsumoMinimo(),
								Collections.list(faixasProporcional.elements()),
								dadosCategoria);

						dadosFaturamento.setValorFaturado(faturamento.getValorFaturado());
						dadosFaturamento.setValorTarifaMinima(faturamento.getValorFaturado());
						dadosCategoria.getFaturamentoAguaProporcional().setValorFaturado(faturamento.getValorFaturado());

						valorFaturadoBolsaAgua = faturamento.getValorFaturado();

						calculado = true;

						DadosCategoria dadosEconomiasSubcategorias = (DadosCategoria) dadosCategoria;

						LogUtil.salvarLog(logType, "Valor Faturado: " + faturamento.getValorFaturado()
								+ " | Consumo FATURADO Categoria Ou Subcategoria: " + dadosFaturamento.getConsumoFaturado()
								+ " | Valor Tarifa Minima: " + Util.arredondar(valorFaturadoBolsaAguaMinimo, 2)
								+ " | Consumo Minimo: " + dadosFaturamento.getConsumoMinimo());

						atribuirDadosFaturamento(imovel, tipoMedicao, dadosEconomiasSubcategorias, faturamento);
					}

					// 4.8.Calcula data da vigência inicial = data da vigência final + 1 dia.
					dataVigenciaFinal = Util.adicionarNumeroDiasDeUmaData(dataVigenciaFinal, +1);

					dataVigenciaInicial = dataVigenciaFinal;

					if (dataVigenciaFinal.before(dataLeituraAnterior)) {
						dataVigenciaInicial = dataLeituraAnterior;
					} else {
						dataVigenciaInicial = dataVigenciaFinal;
					}
				}
			}
		}
		getImovelSelecionado().setIndcImovelCalculado(Constantes.SIM);
	}

	public boolean isPrintingAllowed() {
		boolean habilitaOpcaoImpressao = true;

		// Se não for imovel condominial com leitura individualizada ou informativo
		if (getImovelSelecionado().isImovelCondominio() || getImovelSelecionado().isImovelInformativo()) {
			habilitaOpcaoImpressao = false;
		}

		return habilitaOpcaoImpressao;
	}
    
    public boolean isImovelInformativo(int indcParalizarFaturamentoAgua,  int indcParalizarFaturamentoEsgoto, int numeroConta, String situacaoLigacaoAgua, String situacaoLigacaoEsgoto) {
		boolean informativo = false;
		boolean paralizarFaturamento = false;

		if (indcParalizarFaturamentoAgua == Constantes.SIM || indcParalizarFaturamentoEsgoto == Constantes.SIM) {
			paralizarFaturamento = true;
		}

		if (numeroConta == Constantes.NULO_INT && paralizarFaturamento == true || !Constantes.LIGADO.equals(situacaoLigacaoAgua) && !Constantes.LIGADO.equals(situacaoLigacaoEsgoto)) {
			informativo = true;
		}








		return informativo;
		//Os imoveis setados como informativo contendo debitos passados, que precisam ter suas contas geradas e impressas, tem seu status alterado
		//na hora da insercao dos debitos do mesmo imovel. (DataManipulator.java - 2140) (Almeida Paulo, 19-10-2021)

	}
    
	public void setupDataAfterPrinting(int impressaoTipo) {
		if (impressaoTipo == Constantes.IMPRESSAO_FATURA || impressaoTipo == Constantes.IMPRESSAO_FATURA_E_NOTIFICACAO) {
			getImovelSelecionado().setIndcImovelImpresso(Constantes.SIM);

			getImovelSelecionado().setQuantidadeContasImpressas(1 + (getImovelSelecionado().getQuantidadeContasImpressas()));

			System.out.println("Quantidade de vezes impressas: " + getImovelSelecionado().getQuantidadeContasImpressas());

			// Guarda a data da impressao da conta de imovel nao-medido. Já que não possui data de leitura.
			if (getImovelSelecionado().getMedidor(Constantes.LIGACAO_AGUA) == null && getImovelSelecionado().getMedidor(Constantes.LIGACAO_POCO) == null) {
				getImovelSelecionado().setDataImpressaoNaoMedido(Util.dateToAnoMesDiaString(MedidorAguaTab.getCurrentDateByGPS()));
			}
			
			Log.i(" Imovel Selecionado", String.valueOf(getImovelSelecionado().getMatricula()));
		}

		getImovelSelecionado().setImovelStatus(Constantes.IMOVEL_STATUS_CONCLUIDO);
		ControladorRota.getInstancia().getDataManipulator().salvarImovel(getImovelSelecionado());
	}

	public int getImpressaoTipo(Context context) {
		int impressaoTipo = Constantes.IMPRESSAO_NAO_PERMITIDA;

		if (BusinessConta.getInstancia(context).isImpressaoPermitida()) {
			impressaoTipo = Constantes.IMPRESSAO_FATURA;
		} else {
			impressaoTipo = Constantes.IMPRESSAO_NAO_PERMITIDA;
			Toast.makeText(context, BusinessConta.getInstancia().getMensagemPermiteImpressao(), Toast.LENGTH_LONG).show();
		}

		return impressaoTipo;
	}
}
