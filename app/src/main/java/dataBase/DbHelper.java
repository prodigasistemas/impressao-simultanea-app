package dataBase;

import util.Constantes;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_DADOS_ROTA = "CREATE TABLE dados_rota (id INTEGER PRIMARY KEY, grupo TEXT, localidade TEXT, setor TEXT, rota TEXT, referencia TEXT)";

	private static final String DATABASE_IMOVEL_QUERY = "CREATE TABLE imovel (id INTEGER PRIMARY KEY, matricula INTEGER, nome_gerencia_regional TEXT, nome_escritorio TEXT, nome_usuario TEXT, data_vencimento DATE, data_validade_conta DATE, inscricao TEXT, localidade TEXT, setor TEXT, quadra TEXT, lote TEXT, sublote TEXT,"
			+ "endereco TEXT, ano_mes_conta TEXT, digito_verificador_conta TEXT, codigo_responsavel TEXT, nome_responsavel TEXT, endereco_entrega TEXT, situacao_lig_agua TEXT, situacao_lig_esgoto TEXT, descricao_banco TEXT, codigo_agencia TEXT, matricula_condominio TEXT, indc_condominio TEXT, "
			+ "codigo_perfil TEXT, consumo_medio INTEGER, indc_faturamento_agua TEXT, indc_faturamento_esgoto TEXT, indc_emissao_conta TEXT, consumo_min_agua TEXT, consumo_min_esgoto TEXT, percent_coleta_esgoto TEXT, percent_cobranca_esgoto TEXT, tipo_poco TEXT, codigo_tarifa TEXT, consumo_estouro INTEGER, "
			+ "alto_consumo INTEGER, baixo_consumo INTEGER, fator_mult_estouro TEXT, fator_mult_media_alto_consumo TEXT, percent_baixo_consumo TEXT, consumo_maximo INTEGER, grupo_faturamento TEXT, codigo_rota INTEGER, numero_conta TEXT, tipo_calculo_tarifa TEXT, endereco_atendimento TEXT, telefone_localidade_ddd TEXT,"
			+ " sequencial_rota INTEGER, mensagem_conta1 TEXT, mensagem_conta2 TEXT, mensagem_conta3 TEXT, mensagem_quitacao_anual TEXT, consumo_minimo_imovel INTEGER, consumo_minimo_imovel_nao_medido INTEGER, numero_documento_notificacao_debito TEXT, numero_codigo_barra_notificacao_debito TEXT, cpf_cnpj_cliente TEXT, data_leitura_anterior_nao_medido DATE, indicador_abastecimento_agua TEXT,"
			+ " indicador_imovel_sazonal TEXT, indicador_paralizar_faturamento_agua TEXT, indicador_paralizar_faturamento_esgoto TEXT, opcao_debito_automatico TEXT, percentual_alternativo_esgoto TEXT, consumo_percentual_alternativo_esgoto TEXT, data_emissao_documento DATE, quantidade_contas_impressas TEXT, contagem_validacao_agua TEXT,"
			+ " contagem_validacao_poco TEXT, leitura_gravada_anterior TEXT, anormalidade_gravada_anterior TEXT, data_impressao_nao_medido DATE, valor_residual_credito TEXT, indc_adicionou_dados_iniciais_helper_rateio TEXT, valor_rateio_agua TEXT, valor_rateio_esgoto TEXT, consumo_rateio_agua TEXT, "
			+ "consumo_rateio_esgoto TEXT, mensagem_estouro_consumo_1 TEXT, mensagem_estouro_consumo_2 TEXT, mensagem_estouro_consumo_3 TEXT, imovel_status TEXT, imovel_enviado TEXT, indc_imovel_impresso TEXT, indc_geracao TEXT, latitude TEXT, longitude TEXT,"
			+ " enviar_conta_fisica TEXT, codigo_convenio TEXT)";

	private static final String DATABASE_DADOS_CATEGORIA_QUERY = "CREATE TABLE dados_categoria (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, codigo_categoria TEXT, descricao_categoria TEXT, codigo_subcategoria TEXT, descricao_subcategoria TEXT, "
			+ "quantidade_econominas_subcategoria TEXT, descricao_abreviada_categoria TEXT, descricao_abreviada_subcategoria TEXT, fator_economia_categoria TEXT)";

	private static final String DATABASE_HISTORICO_CONSUMO_QUERY = "CREATE TABLE historico_consumo (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, tipo_ligacao TEXT, ano_mes_referencia TEXT, consumo TEXT, anormalidade_leitura TEXT, anormalidade_consumo TEXT)";

	private static final String DATABASE_DEBITO_QUERY = "CREATE TABLE debito (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, descricao TEXT, valor TEXT, codigo TEXT, indc_uso TEXT, indc_incluir_calculo_imposto INTEGER)";

	private static final String DATABASE_CREDITO_QUERY = "CREATE TABLE credito (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, descricao TEXT, valor TEXT, codigo TEXT, indc_uso TEXT)";

	private static final String DATABASE_IMPOSTO_QUERY = "CREATE TABLE imposto (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, tipo_imposto TEXT, descricao_imposto TEXT, percentual_aliquota TEXT)";

	private static final String DATABASE_CONTA_QUERY = "CREATE TABLE conta (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, ano_mes_referencia_conta TEXT, valor_conta TEXT, data_vencimento_conta Date, valor_acresc_impontualidade TEXT )";

	private static final String DATABASE_TARIFACAO_MINIMA_QUERY = "CREATE TABLE tarifacao_minima (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, codigo TEXT, data_vigencia DATE, codigo_categoria TEXT, codigo_subcategoria TEXT, consumo_minimo_subcategoria TEXT,"
			+ "tarifa_minima_categoria TEXT)";

	private static final String DATABASE_TARIFACAO_COMPLEMENTAR_QUERY = "CREATE TABLE tarifacao_complementar (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, codigo INTEGER, data_inicio_vigencia DATE, codigo_categoria INTEGER, codigo_subcategoria INTEGER, limite_inicial_faixa INTEGER,"
			+ "limite_final_faixa INTEGER, valor_m3_faixa TEXT)";

	private static final String DATABASE_MEDIDOR_QUERY = "CREATE TABLE medidor (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, tipo_medicao TEXT, numero_hidrometro TEXT, data_instalacao_hidrometro TEXT, num_digitos_leitura_hidrometro TEXT, leitura_anterior_faturamento TEXT, "
			+ "data_leitura_anterior_faturamento TEXT, codigo_situacao_leitura_anterior TEXT, leitura_esperada_inicial TEXT, leitura_esperada_final TEXT, consumo_medio TEXT, local_instalacao TEXT, leitura_anterior_informada TEXT, "
			+ "data_leitura_anterior_informada TEXT, data_ligacao_fornecimento TEXT, tipo_rateio TEXT, leitura_instalacao_hidrometro TEXT, "
			+ "data_leitura TEXT, leitura_anterior INTEGER, data_leitura_atual_faturamento TEXT, leitura INTEGER, anormalidade INTEGER, qtd_dias_ajustado INTEGER,"
			+ "leitura_atual_faturamento INTEGER, leitura_relatorio INTEGER, anormalidade_relatorio TNTEGER)";

	private static final String DATABASE_GERAL_QUERY = "CREATE TABLE geral (id INTEGER PRIMARY KEY autoincrement, data_referencia_arrecadacao TEXT, ano_mes_faturamento TEXT, codigo_empresa_febraban TEXT, telefone0800 TEXT, "
			+ "cnpj_empresa TEXT, inscricao_estadual_empresa TEXT, valor_minim_emissao_conta TXT, percent_tolerancia_rateio TEXT, decremento_maximo_consumo_economia TEXT, incremento_maximo_consumo_economia TEXT, indc_tarifa_catgoria TEXT, login TEXT, "
			+ "senha TEXT, data_ajuste_leitura TEXT, indicador_ajuste_consumo TEXT, indicador_transmissao_offline TEXT, versao_celular TEXT, indc_atualizar_sequencia_rota TEXT, indc_bloquear_reemissao_conta TEXT, qtd_dias_ajuste_consumo TEXT, indicador_rota_dividida TEXT,"
			+ "indicador_calculo_media TEXT, id_rota TEXT, modulo_verificador_codigo_barras TEXT, data_inicio TEXT, data_fim TEXT, data_referencia_arrecadada TEXT, descricao_aliquota_imposto TEXT, valor_aliquota_imposto REAL, descricao_aliquota_agreguladora TEXT, valor_aliquota_agreguladora REAL)";

	private static final String DATABASE_ANORMALIDADE_QUERY = "CREATE TABLE anormalidade (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT, indicador_leitura INTEGER, id_consumo_a_cobrar_sem_leitura INTEGER, "
			+ "id_consumo_a_cobrar_com_leitura INTEGER, id_leitura_faturar_sem_leitura INTEGER, id_leitura_faturar_com_leitura INTEGER, indc_uso INTEGER, numero_fator_sem_leitura INTEGER,"
			+ "numero_fator_com_leitura INTEGER)";

	private static final String DATABASE_CONSUMO_AGUA_QUERY = "CREATE TABLE consumo_agua (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, consumo_medido_mes INTEGER, consumo_cobrado_mes INTEGER, consumo_cobrado_mes_imovel_micro INTEGER, consumo_cobrado_mes_original INTEGER, leitura_atual INTEGER, tipo_consumo INTEGER, "
			+ "dias_consumo INTEGER, anormalidade_consumo INTEGER, anormalidade_leitura_faturada INTEGER)";

	private static final String DATABASE_CONSUMO_ESGOTO_QUERY = "CREATE TABLE consumo_esgoto (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, consumo_medido_mes INTEGER, consumo_cobrado_mes INTEGER, consumo_cobrado_mes_imovel_micro INTEGER, consumo_cobrado_mes_original INTEGER, leitura_atual INTEGER, tipo_consumo INTEGER, "
			+ "dias_consumo INTEGER, anormalidade_consumo INTEGER, anormalidade_leitura_faturada INTEGER)";

	private static final String DATABASE_SITUACAO_TIPO_QUERY = "CREATE TABLE situacao_tipo (id INTEGER PRIMARY KEY autoincrement, matricula INTEGER not null, tipo_situacao_especial_feturamento TEXT, id_anormalidade_consumo_sem_leitura TEXT, id_anormalidade_consumo_com_leitura TEXT, id_anormalidade_leitura_sem_leitura TEXT, "
			+ "id_anormalidade_leitura_com_leitura TEXT, consumo_agua_medido_historico_faturamento TEXT, consumo_agua_nao_medido_historico_faturamento TEXT, volume_esgoto_medido_historico_faturamento TEXT, volume_esgoto_nao_medido_historico_faturamento TEXT, indc_valida_agua TEXT,"
			+ " indc_valida_esgoto TEXT)";

	private static final String DATABASE_RATEIO_CONSUMO_HELPER_QUERY = "CREATE TABLE rateio_condominio (id INTEGER PRIMARY KEY autoincrement, matricula_macro INTEGER not null, matricula_ultimo_micro INTEGER, quantidade_economia_agua_total INTEGER, consumo_ligacao_agua_total INTEGER, quantidade_economia_esgoto_total INTEGER, consumo_ligacao_esgoto_total INTEGER, "
			+ " consumo_minimo_total INTEGER, consumo_para_rateio_agua INTEGER, conta_para_rateio_agua TEXT, consumo_para_rateio_esgoto INTEGER, conta_para_rateio_esgoto TEXT, passos INTEGER)";

	private static final String DATABASE_CONSUMO_ANORMALIDADE_ACAO_QUERY = "CREATE TABLE consumo_anormalidade_acao (id INTEGER PRIMARY KEY autoincrement, id_consumo_anormalidade INTEGER, id_categoria INTEGER, id_perfil INTEGER, id_leitura_anormalidade_consumo_primeiro_mes INTEGER, id_leitura_anormalidade_consumo_segundo_mes INTEGER, "
			+ "id_leitura_anormalidade_consumo_terceiro_mes INTEGER, fator_consumo_primeiro_mes INTEGER, fator_consumo_segundo_mes INTEGER, fator_consumo_terceiro_mes INTEGER, indc_geracao_conta_primeiro_mes INTEGER, indc_geracao_conta_segundo_mes INTEGER, "
			+ "indc_geracao_conta_terceiro_mes, mensagem_conta_primeiro_mes TEXT, mensagem_conta_segundo_mes TEXT, mensagem_conta_terceiro_mes TEXT)";

	private static final String DATABASE_DADOS_FATURAMENTO_QUERY = "CREATE TABLE dados_faturamento (id INTEGER PRIMARY KEY autoincrement, id_dados_categoria INTEGER, tipo_faturamento INTEGER, valor_faturado TEXT, consumo_faturado INTEGER, valor_tarifa_minima TEXT, consumo_minimo INTEGER)";

	private static final String DATABASE_DADOS_FATURAMENTO_FAIXA_QUERY = "CREATE TABLE dados_faturamento_faixa (id INTEGER PRIMARY KEY autoincrement, id_dados_faturamento INTEGER, tipo_faturamento_faixa INTEGER, consumo_faturado INTEGER, valor_faturado TEXT, limite_inicial_consumo INTEGER, limite_final_consumo INTEGER, valor_tarifa TEXT)";

	private static final String DATABASE_DADOS_RELATORIO_QUERY = "CREATE TABLE dados_relatorio (id INTEGER PRIMARY KEY autoincrement, id_imovel INTEGER)";

	private static final String DATABASE_CONFIGURACAO_QUERY = "CREATE TABLE configuracao (id INTEGER PRIMARY KEY autoincrement, nome_arquivo_imoveis TEXT, bluetooth_address TEXT, id_imovel_selecionado INTEGER, indice_imovel_condominio INTEGER, sucesso_carregamento INTEGER, "
			+ "quantidade_imoveis INTEGER, nome_arquivo_retorno TEXT)";

	private static final String DATABASE_DADOS_QUALIDADE_AGUA_QUERY = "CREATE TABLE dados_qualidade_agua (id INTEGER PRIMARY KEY autoincrement, turbidez_padrao TEXT, ph_padrao TEXT, cor_padrao TEXT, cloro_padrao TEXT, fluor_padrao TEXT, ferro_padrao TEXT, coliformes_totais_padrao TEXT, coliformes_fecais_padrao TEXT, nitrato_padrao TEXT, "
			+ "coliformes_termo_tolerantes_padrao TEXT, am_referencia_qualidade_agua TEXT, numero_cloro_residual TEXT, numero_turbidez TEXT, numero_ph TEXT, numero_cor TEXT, numero_fluor TEXT, numero_ferro TEXT, numero_coliformes_totais TEXT, numero_coliformes_fecais TEXT, numero_nitrato TEXT, "
			+ "numero_coliformes_termo_tolerantes TEXT, descricao_fonte_capacitacao TEXT, quantidade_turbidez_exigidas TEXT, quantidade_cor_exigidas TEXT, quantidade_cloro_exigidas TEXT, quantidade_fluor_exigidas TEXT, quantidade_coliformes_totais_exigidas TEXT, quantidade_coliformes_fecais_exigidas TEXT, "
			+ "quantidade_coliformes_termo_tolerantes_exigidas TEXT, quantidade_turbidez_analisadas TEXT, quantidade_cor_analisadas TEXT, quantidade_cloro_analisadas TEXT, quantidade_fluor_analisadas TEXT, quantidade_coliformes_totais_analisadas TEXT, quantidade_coliformes_fecais_analisadas TEXT, "
			+ "quantidade_coliformes_termo_tolerantes_analisadas TEXT, quantidade_turbidez_conforme TEXT, quantidade_cor_conforme TEXT, quantidade_cloro_conforme TEXT, quantidade_fluor_conforme TEXT, quantidade_coliformes_totais_conforme TEXT, quantidade_coliformes_fecais_conforme TEXT, "
			+ "quantidade_coliformes_termo_tolerantes_conforme TEXT)";

	public DbHelper(Context context) {
		super(context, Constantes.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_DADOS_ROTA);
		db.execSQL(DATABASE_IMOVEL_QUERY);
		db.execSQL(DATABASE_DADOS_CATEGORIA_QUERY);
		db.execSQL(DATABASE_HISTORICO_CONSUMO_QUERY);
		db.execSQL(DATABASE_DEBITO_QUERY);
		db.execSQL(DATABASE_CREDITO_QUERY);
		db.execSQL(DATABASE_IMPOSTO_QUERY);
		db.execSQL(DATABASE_CONTA_QUERY);
		db.execSQL(DATABASE_MEDIDOR_QUERY);
		db.execSQL(DATABASE_TARIFACAO_MINIMA_QUERY);
		db.execSQL(DATABASE_TARIFACAO_COMPLEMENTAR_QUERY);
		db.execSQL(DATABASE_GERAL_QUERY);
		db.execSQL(DATABASE_ANORMALIDADE_QUERY);
		db.execSQL(DATABASE_CONSUMO_AGUA_QUERY);
		db.execSQL(DATABASE_CONSUMO_ESGOTO_QUERY);
		db.execSQL(DATABASE_RATEIO_CONSUMO_HELPER_QUERY);
		db.execSQL(DATABASE_CONSUMO_ANORMALIDADE_ACAO_QUERY);
		db.execSQL(DATABASE_DADOS_FATURAMENTO_QUERY);
		db.execSQL(DATABASE_DADOS_FATURAMENTO_FAIXA_QUERY);
		db.execSQL(DATABASE_DADOS_RELATORIO_QUERY);
		db.execSQL(DATABASE_SITUACAO_TIPO_QUERY);
		db.execSQL(DATABASE_CONFIGURACAO_QUERY);
		db.execSQL(DATABASE_DADOS_QUALIDADE_AGUA_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Constantes.TABLE_IMOVEL);
		onCreate(db);
	}
}
