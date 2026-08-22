package br.com.conectaeventos.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model que representa um item do portfólio de um prestador de serviços.
 */
public class PortfolioItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id_portfolio;
	private String cpf_cnpj_prestador;
	private String titulo;
	private String descricao;
	private String imagem_url;
	private Date data_publicacao;

	public PortfolioItem() {
	}

	public PortfolioItem(String cpf_cnpj_prestador, String titulo, String descricao, String imagem_url,
			Date data_publicacao) {
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.titulo = titulo;
		this.descricao = descricao;
		this.imagem_url = imagem_url;
		this.data_publicacao = data_publicacao;
	}

	public PortfolioItem(int id_portfolio, String cpf_cnpj_prestador, String titulo, String descricao,
			String imagem_url, Date data_publicacao) {
		this.id_portfolio = id_portfolio;
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.titulo = titulo;
		this.descricao = descricao;
		this.imagem_url = imagem_url;
		this.data_publicacao = data_publicacao;
	}

	public int getId_portfolio() {
		return id_portfolio;
	}

	public void setId_portfolio(int id_portfolio) {
		this.id_portfolio = id_portfolio;
	}

	public String getCpf_cnpj_prestador() {
		return cpf_cnpj_prestador;
	}

	public void setCpf_cnpj_prestador(String cpf_cnpj_prestador) {
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getImagem_url() {
		return imagem_url;
	}

	public void setImagem_url(String imagem_url) {
		this.imagem_url = imagem_url;
	}

	public Date getData_publicacao() {
		return data_publicacao;
	}

	public void setData_publicacao(Date data_publicacao) {
		this.data_publicacao = data_publicacao;
	}

	@Override
	public String toString() {
		return "PortfolioItem [id_portfolio=" + id_portfolio + ", cpf_cnpj_prestador=" + cpf_cnpj_prestador
				+ ", titulo=" + titulo + ", descricao=" + descricao + ", imagem_url=" + imagem_url
				+ ", data_publicacao=" + data_publicacao + "]";
	}
}
