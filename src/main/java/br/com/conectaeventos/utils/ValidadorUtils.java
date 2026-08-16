package br.com.conectaeventos.utils;

public class ValidadorUtils {

	/**
	 * Verifica se uma String é nula ou vazia.
	 */
	public static boolean isVazio(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Valida o formato básico de um e-mail.
	 */
	public static boolean isEmailValido(String email) {
		if (isVazio(email)) {
			return false;
		}
		String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
		return email.matches(regex);
	}

	/**
	 * Verifica se a string limpa possui a quantidade de dígitos de um CPF (11) ou CNPJ (14).
	 */
	public static boolean isCpfOuCnpjValido(String documento) {
		if (isVazio(documento)) {
			return false;
		}
		String apenasNumeros = apenasNumeros(documento);
		return apenasNumeros.length() == 11 || apenasNumeros.length() == 14;
	}

	/**
	 * Remove todos os caracteres não numéricos de uma string.
	 */
	public static String apenasNumeros(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("[^0-9]", "");
	}
}

