package br.com.conectaeventos.model;

public class ItemContratacao {
	private int id_item;
	private int id_contratacao;
	private String descricao_item;
	private int quantidade;
	private double valor_unitario;
	private double valor_total;

	public ItemContratacao() {

	}

	public ItemContratacao(int id_contratacao, String descricao_item, int quantidade, double valor_unitario) {
		this.id_contratacao = id_contratacao;
		this.descricao_item = descricao_item;
		this.quantidade = quantidade;
		this.valor_unitario = valor_unitario;
		this.valor_total = quantidade * valor_unitario;
	}

	public ItemContratacao(int id_contratacao, String descricao_item, int quantidade, double valor_unitario,
			double valor_total) {
		this.id_contratacao = id_contratacao;
		this.descricao_item = descricao_item;
		this.quantidade = quantidade;
		this.valor_unitario = valor_unitario;
		this.valor_total = valor_total;
	}

	public ItemContratacao(int id_item, int id_contratacao, String descricao_item, int quantidade,
			double valor_unitario, double valor_total) {
		this.id_item = id_item;
		this.id_contratacao = id_contratacao;
		this.descricao_item = descricao_item;
		this.quantidade = quantidade;
		this.valor_unitario = valor_unitario;
		this.valor_total = valor_total;
	}

	public int getId_item() {
		return id_item;
	}

	public void setId_item(int id_item) {
		this.id_item = id_item;
	}

	public int getId_contratacao() {
		return id_contratacao;
	}

	public void setId_contratacao(int id_contratacao) {
		this.id_contratacao = id_contratacao;
	}

	public String getDescricao_item() {
		return descricao_item;
	}

	public void setDescricao_item(String descricao_item) {
		this.descricao_item = descricao_item;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public double getValor_unitario() {
		return valor_unitario;
	}

	public void setValor_unitario(double valor_unitario) {
		this.valor_unitario = valor_unitario;
	}

	public double getValor_total() {
		return valor_total;
	}

	public void setValor_total(double valor_total) {
		this.valor_total = valor_total;
	}

	public double getValor_item() {
		return valor_total;
	}

	public void setValor_item(double valor_item) {
		this.valor_total = valor_item;
	}
}
