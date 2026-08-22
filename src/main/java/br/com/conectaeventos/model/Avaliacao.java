package br.com.conectaeventos.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model que representa uma Avaliação feita por um contratante a um prestador após a conclusão de uma contratação.
 */
public class Avaliacao implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id_avaliacao;
	private int id_contratacao;
	private String cpf_cnpj_contratante;
	private String cpf_cnpj_prestador;
	private int nota;
	private String comentario;
	private Date data_avaliacao;

	public Avaliacao() {
	}

	public Avaliacao(int id_contratacao, String cpf_cnpj_contratante, String cpf_cnpj_prestador, int nota,
			String comentario, Date data_avaliacao) {
		this.id_contratacao = id_contratacao;
		this.cpf_cnpj_contratante = cpf_cnpj_contratante;
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.nota = nota;
		this.comentario = comentario;
		this.data_avaliacao = data_avaliacao;
	}

	public Avaliacao(int id_avaliacao, int id_contratacao, String cpf_cnpj_contratante, String cpf_cnpj_prestador,
			int nota, String comentario, Date data_avaliacao) {
		this.id_avaliacao = id_avaliacao;
		this.id_contratacao = id_contratacao;
		this.cpf_cnpj_contratante = cpf_cnpj_contratante;
		this.cpf_cnpj_prestador = cpf_cnpj_prestador;
		this.nota = nota;
		this.comentario = comentario;
		this.data_avaliacao = data_avaliacao;
	}

	public int getId_avaliacao() {
		return id_avaliacao;
	}

	public void setId_avaliacao(int id_avaliacao) {
		this.id_avaliacao = id_avaliacao;
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

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Date getData_avaliacao() {
		return data_avaliacao;
	}

	public void setData_avaliacao(Date data_avaliacao) {
		this.data_avaliacao = data_avaliacao;
	}

	@Override
	public String toString() {
		return "Avaliacao [id_avaliacao=" + id_avaliacao + ", id_contratacao=" + id_contratacao
				+ ", cpf_cnpj_contratante=" + cpf_cnpj_contratante + ", cpf_cnpj_prestador=" + cpf_cnpj_prestador
				+ ", nota=" + nota + ", comentario=" + comentario + ", data_avaliacao=" + data_avaliacao + "]";
	}
}
