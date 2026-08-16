package br.com.conectaeventos.model;

import java.sql.Date;

public class Contratacao {
	private int id_contratacao;
	private String cpf_cnpj_contratante;
	private String cpf_cnpj_prestador;
	private String titulo_evento;
	private String descricao_evento;
	private Date data_evento;
	private Date data_contratacao;
	private double valor_total;
	private String status;

	public Contratacao() {

	}

	public Contratacao(String cpf_cnpj_contratante, String cpf_cnpj_prestador, String titulo_evento,
			String descricao_evento, Date data_evento, Date data_contratacao, double valor_total, String status) {
		this.cpf_cnpj_contratante = cpf_cnpj_contratante;
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.titulo_evento = titulo_evento;
		this.descricao_evento = descricao_evento;
		this.data_evento = data_evento;
		this.data_contratacao = data_contratacao;
		this.valor_total = valor_total;
		this.status = status;
	}

	public Contratacao(int id_contratacao, String cpf_cnpj_contratante, String cpf_cnpj_prestador, String titulo_evento,
			String descricao_evento, Date data_evento, Date data_contratacao, double valor_total, String status) {
		this.id_contratacao = id_contratacao;
		this.cpf_cnpj_contratante = cpf_cnpj_contratante;
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.titulo_evento = titulo_evento;
		this.descricao_evento = descricao_evento;
		this.data_evento = data_evento;
		this.data_contratacao = data_contratacao;
		this.valor_total = valor_total;
		this.status = status;
	}

	public int getId_contratacao() {
		return id_contratacao;
	}

	public void setId_contratacao(int id_contratacao) {
		this.id_contratacao = id_contratacao;
	}

	public String getCpf_cnpj_contratante() {
		return cpf_cnpj_contratante;
	}

	public void setCpf_cnpj_contratante(String cpf_cnpj_contratante) {
		this.cpf_cnpj_contratante = cpf_cnpj_contratante;
	}

	public String getCpf_cnpj_prestador() {
		return cpf_cnpj_prestador;
	}

	public void setCpf_cnpj_prestador(String cpf_cnpj_prestador) {
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
	}

	public String getTitulo_evento() {
		return titulo_evento;
	}

	public void setTitulo_evento(String titulo_evento) {
		this.titulo_evento = titulo_evento;
	}

	public String getDescricao_evento() {
		return descricao_evento;
	}

	public void setDescricao_evento(String descricao_evento) {
		this.descricao_evento = descricao_evento;
	}

	public Date getData_evento() {
		return data_evento;
	}

	public void setData_evento(Date data_evento) {
		this.data_evento = data_evento;
	}

	public Date getData_contratacao() {
		return data_contratacao;
	}

	public void setData_contratacao(Date data_contratacao) {
		this.data_contratacao = data_contratacao;
	}

	public double getValor_total() {
		return valor_total;
	}

	public void setValor_total(double valor_total) {
		this.valor_total = valor_total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

