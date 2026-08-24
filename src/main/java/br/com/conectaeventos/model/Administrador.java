package br.com.conectaeventos.model;

import java.sql.Date;

public class Administrador {
	private int id_administrador;
	private String nome_administrador;
	private String email_administrador;
	private String senha_administrador;
	private Date data_cadastro;
	private String situacao;

	public Administrador() {

	}

	public Administrador(String nome_administrador, String email_administrador, String senha_administrador,
			Date data_cadastro, String situacao) {
		this.nome_administrador = nome_administrador;
		this.email_administrador = email_administrador;
		this.senha_administrador = senha_administrador;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public Administrador(int id_administrador, String nome_administrador, String email_administrador,
			String senha_administrador, Date data_cadastro, String situacao) {
		this.id_administrador = id_administrador;
		this.nome_administrador = nome_administrador;
		this.email_administrador = email_administrador;
		this.senha_administrador = senha_administrador;
		this.data_cadastro = data_cadastro;
		this.situacao = situacao;
	}

	public int getId_administrador() {
		return id_administrador;
	}

	public void setId_administrador(int id_administrador) {
		this.id_administrador = id_administrador;
	}

	public String getNome_administrador() {
		return nome_administrador;
	}

	public void setNome_administrador(String nome_administrador) {
		this.nome_administrador = nome_administrador;
	}

	public String getEmail_administrador() {
		return email_administrador;
	}

	public void setEmail_administrador(String email_administrador) {
		this.email_administrador = email_administrador;
	}

	public String getSenha_administrador() {
		return senha_administrador;
	}

	public void setSenha_administrador(String senha_administrador) {
		this.senha_administrador = senha_administrador;
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
