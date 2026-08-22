package br.com.conectaeventos.model;

import java.io.Serializable;

/**
 * Model que representa uma Categoria de serviço no ConectaEventos.
 */
public class Categoria implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id_categoria;
	private String nome_categoria;
	private String descricao;

	public Categoria() {
	}

	public Categoria(String nome_categoria, String descricao) {
		this.nome_categoria = nome_categoria;
		this.descricao = descricao;
	}

	public Categoria(int id_categoria, String nome_categoria, String descricao) {
		this.id_categoria = id_categoria;
		this.nome_categoria = nome_categoria;
		this.descricao = descricao;
	}

	public int getId_categoria() {
		return id_categoria;
	}

	public void setId_categoria(int id_categoria) {
		this.id_categoria = id_categoria;
	}

	public String getNome_categoria() {
		return nome_categoria;
	}

	public void setNome_categoria(String nome_categoria) {
		this.nome_categoria = nome_categoria;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "Categoria [id_categoria=" + id_categoria + ", nome_categoria=" + nome_categoria + ", descricao="
				+ descricao + "]";
	}
}
