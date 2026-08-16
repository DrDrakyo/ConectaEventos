package br.com.conectaeventos.utils;

public class ValidadorUtils {

	/**
	 * Verifica se uma String é nula ou vazia (apenas espaços).
	 * 
	 * @param str String a ser verificada
	 * @return true se nula ou vazia, false caso contrário
	 */
	public static boolean isVazio(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Valida o formato de um e-mail através de expressão regular.
	 * 
	 * @param email Endereço de e-mail a ser validado
	 * @return true se o e-mail for válido, false caso contrário
	 */
	public static boolean isEmailValido(String email) {
		if (isVazio(email)) {
			return false;
		}
		String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
		return email.matches(regex);
	}

	/**
	 * Verifica se a string limpa possui a quantidade de dígitos válida para um CPF (11) ou CNPJ (14).
	 * 
	 * @param documento String contendo o CPF ou CNPJ (com ou sem máscara)
	 * @return true se possuir 11 ou 14 dígitos numéricos, false caso contrário
	 */
	public static boolean isCpfOuCnpjValido(String documento) {
		if (isVazio(documento)) {
			return false;
		}
		String numeros = apenasNumeros(documento);
		return numeros.length() == 11 || numeros.length() == 14;
	}

	/**
	 * Remove todos os caracteres não numéricos de uma String.
	 * 
	 * @param str String original
	 * @return String contendo apenas os dígitos numéricos
	 */
	public static String apenasNumeros(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("[^0-9]", "");
	}
}

