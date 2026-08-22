package br.com.conectaeventos.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitário para geração e verificação de hashes criptográficos (SHA-256 e MD5).
 */
public class CriptografiaUtils {

	/**
	 * Gera o hash SHA-256 de uma string.
	 * 
	 * @param texto Texto a ser criptografado.
	 * @return String hexadecimal contendo o hash gerado, ou o texto original em caso de falha.
	 */
	public static String gerarHashSHA256(String texto) {
		if (texto == null) return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Algoritmo de criptografia SHA-256 não encontrado: " + e.getMessage());
			return texto;
		}
	}

	/**
	 * Gera o hash MD5 de uma string.
	 * 
	 * @param texto Texto a ser criptografado.
	 * @return String hexadecimal contendo o hash gerado.
	 */
	public static String gerarHashMD5(String texto) {
		if (texto == null) return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Algoritmo de criptografia MD5 não encontrado: " + e.getMessage());
			return texto;
		}
	}

	/**
	 * Verifica se o hash de um texto coincide com o hash esperado (comparação segura e insensível a maiúsculas).
	 * 
	 * @param texto Texto puro a ser testado.
	 * @param hashEsperado Hash SHA-256 armazenado.
	 * @return boolean true se os hashes forem idênticos.
	 */
	public static boolean verificarHashSHA256(String texto, String hashEsperado) {
		if (texto == null || hashEsperado == null) return false;
		String hashGerado = gerarHashSHA256(texto);
		return hashGerado.equalsIgnoreCase(hashEsperado);
	}
}
