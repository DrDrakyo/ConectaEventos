package br.com.conectaeventos.model;

import java.sql.Date;

public class Contratante {
	private int id_contratante;
	private String nome_contratante;
	private String email_contratante;
	private String senha_contratante;
	private String telefone;
	private String cpf_cnpj;
	private String endereco;
	private String cidade;
	private Date data_cadastro;
	private String situacao;

	public Contratante() {

	}

	public Contratante(String cpf_cnpj, String nome_contratante, String email_contratante, String senha_contratante,
			String telefone, String endereco, String cidade, Date data_cadastro, String situacao) {
		this.cpf_cnpj = cpf_cnpj;
		this.nome_contratante = nome_contratante;
		this.email_contratante = email_contratante;
		this.senha_contratante = senha_contratante;
		this.telefone = telefone;
		this.endereco = endereco;
		this.cidade = cidade;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public Contratante(int id_contratante, String nome_contratante, String email_contratante, String senha_contratante,
			String telefone, String cpf_cnpj, String endereco, String cidade, Date data_cadastro, String situacao) {

		this.id_contratante = id_contratante;
		this.nome_contratante = nome_contratante;
		this.email_contratante = email_contratante;
		this.senha_contratante = senha_contratante;
		this.telefone = telefone;
		this.cpf_cnpj = cpf_cnpj;
		this.endereco = endereco;
		this.cidade = cidade;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public int getId_contratante() {
		return id_contratante;
	}

	public void setId_contratante(int id_contratante) {
		this.id_contratante = id_contratante;
	}

	public String getNome_contratante() {
		return nome_contratante;
	}

	public void setNome_contratante(String nome_contratante) {
		this.nome_contratante = nome_contratante;
	}

	public String getEmail_contratante() {
		return email_contratante;
	}

	public void setEmail_contratante(String email_contratante) {
		this.email_contratante = email_contratante;
	}

	public String getSenha_contratante() {
		return senha_contratante;
	}

	public void setSenha_contratante(String senha_contratante) {
		this.senha_contratante = senha_contratante;
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
