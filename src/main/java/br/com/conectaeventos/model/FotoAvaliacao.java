package br.com.conectaeventos.model;

import java.io.Serializable;

/**
 * Model que representa uma foto anexada a uma avaliação.
 */
public class FotoAvaliacao implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id_foto;
	private int id_avaliacao;
	private String url_foto;
	private String descricao_foto;

	public FotoAvaliacao() {
	}

	public FotoAvaliacao(int id_avaliacao, String url_foto, String descricao_foto) {
		this.id_avaliacao = id_avaliacao;
		this.url_foto = url_foto;
		this.descricao_foto = descricao_foto;
	}

	public FotoAvaliacao(int id_foto, int id_avaliacao, String url_foto, String descricao_foto) {
		this.id_foto = id_foto;
		this.id_avaliacao = id_avaliacao;
		this.url_foto = url_foto;
		this.descricao_foto = descricao_foto;
	}

	public int getId_foto() {
		return id_foto;
	}

	public void setId_foto(int id_foto) {
		this.id_foto = id_foto;
	}

	public int getId_avaliacao() {
		return id_avaliacao;
	}

	public void setId_avaliacao(int id_avaliacao) {
		this.id_avaliacao = id_avaliacao;
	}

	public String getUrl_foto() {
		return url_foto;
	}

	public void setUrl_foto(String url_foto) {
		this.url_foto = url_foto;
	}

	public String getDescricao_foto() {
		return descricao_foto;
	}

	public void setDescricao_foto(String descricao_foto) {
		this.descricao_foto = descricao_foto;
	}

	@Override
	public String toString() {
		return "FotoAvaliacao [id_foto=" + id_foto + ", id_avaliacao=" + id_avaliacao + ", url_foto=" + url_foto
				+ ", descricao_foto=" + descricao_foto + "]";
	}
}
