package br.com.conectaeventos.model;

import java.sql.Date;

public class Prestador {
	private int id_prestador;
	private String nome_prestador;
	private String email_prestador;
	private String senha_prestador;
	private String telefone;
	private String cpf_cnpj;
	private String endereco;
	private String cidade;
	private String categoria;
	private String descricao;
	private Date data_cadastro;
	private String situacao;

	public Prestador() {

	}

	public Prestador(String cpf_cnpj, String nome_prestador, String email_prestador, String senha_prestador,
			String telefone, String endereco, String cidade, String categoria, String descricao, Date data_cadastro, String situacao) {
		this.cpf_cnpj = cpf_cnpj;
		this.nome_prestador = nome_prestador;
		this.email_prestador = email_prestador;
		this.senha_prestador = senha_prestador;
		this.telefone = telefone;
		this.endereco = endereco;
		this.cidade = cidade;
		this.categoria = categoria;
		this.descricao = descricao;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public Prestador(int id_prestador, String nome_prestador, String email_prestador, String senha_prestador,
			String telefone, String cpf_cnpj, String endereco, String cidade, String categoria, String descricao, Date data_cadastro, String situacao) {

		this.id_prestador = id_prestador;
		this.nome_prestador = nome_prestador;
		this.email_prestador = email_prestador;
		this.senha_prestador = senha_prestador;
		this.telefone = telefone;
		this.cpf_cnpj = cpf_cnpj;
		this.endereco = endereco;
		this.cidade = cidade;
		this.categoria = categoria;
		this.descricao = descricao;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public int getId_prestador() {
		return id_prestador;
	}

	public void setId_prestador(int id_prestador) {
		this.id_prestador = id_prestador;
	}

	public String getNome_prestador() {
		return nome_prestador;
	}

	public void setNome_prestador(String nome_prestador) {
		this.nome_prestador = nome_prestador;
	}

	public String getEmail_prestador() {
		return email_prestador;
	}

	public void setEmail_prestador(String email_prestador) {
		this.email_prestador = email_prestador;
	}

	public String getSenha_prestador() {
		return senha_prestador;
	}

	public void setSenha_prestador(String senha_prestador) {
		this.senha_prestador = senha_prestador;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCpf_cnpj() {
		return cpf_cnpj;
	}

	public void setCpf_cnpj(String cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}

