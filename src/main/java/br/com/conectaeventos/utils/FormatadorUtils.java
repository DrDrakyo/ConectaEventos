package br.com.conectaeventos.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilitário para formatação de documentos, telefones, valores monetários e datas.
 */
public class FormatadorUtils {

	private static final Locale PT_BR = new Locale("pt", "BR");

	/**
	 * Formata um CPF no padrão 000.000.000-00.
	 */
	public static String formatarCpf(String cpf) {
		if (cpf == null) return "";
		String limpo = ValidadorUtils.apenasNumeros(cpf);
		if (limpo.length() != 11) return cpf;
		return limpo.substring(0, 3) + "." + limpo.substring(3, 6) + "." + limpo.substring(6, 9) + "-" + limpo.substring(9, 11);
	}

	/**
	 * Formata um CNPJ no padrão 00.000.000/0000-00.
	 */
	public static String formatarCnpj(String cnpj) {
		if (cnpj == null) return "";
		String limpo = ValidadorUtils.apenasNumeros(cnpj);
		if (limpo.length() != 14) return cnpj;
		return limpo.substring(0, 2) + "." + limpo.substring(2, 5) + "." + limpo.substring(5, 8) + "/"
				+ limpo.substring(8, 12) + "-" + limpo.substring(12, 14);
	}

	/**
	 * Formata CPF (11 dígitos) ou CNPJ (14 dígitos) automaticamente.
	 */
	public static String formatarCpfOuCnpj(String doc) {
		if (doc == null) return "";
		String limpo = ValidadorUtils.apenasNumeros(doc);
		if (limpo.length() == 11) return formatarCpf(limpo);
		if (limpo.length() == 14) return formatarCnpj(limpo);
		return doc;
	}

	/**
	 * Formata um telefone nos padrões (00) 0000-0000 (10 dígitos) ou (00) 00000-0000 (11 dígitos).
	 */
	public static String formatarTelefone(String telefone) {
		if (telefone == null) return "";
		String limpo = ValidadorUtils.apenasNumeros(telefone);
		if (limpo.length() == 10) {
			return "(" + limpo.substring(0, 2) + ") " + limpo.substring(2, 6) + "-" + limpo.substring(6, 10);
		} else if (limpo.length() == 11) {
			return "(" + limpo.substring(0, 2) + ") " + limpo.substring(2, 7) + "-" + limpo.substring(7, 11);
		}
		return telefone;
	}

	/**
	 * Formata um valor numérico em Real (R$ 1.234,56).
	 */
	public static String formatarMoeda(double valor) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(PT_BR);
		DecimalFormat df = new DecimalFormat("R$ #,##0.00", symbols);
		return df.format(valor);
	}

	/**
	 * Formata uma data no padrão dd/MM/yyyy.
	 */
	public static String formatarData(Date data) {
		if (data == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(data);
	}

	/**
	 * Formata uma data e hora no padrão dd/MM/yyyy HH:mm.
	 */
	public static String formatarDataHora(Date data) {
		if (data == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(data);
	}
}
