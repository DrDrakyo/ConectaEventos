package br.com.conectaeventos.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utilitário para validação de nomes, extensões e caminhos de arquivos de upload.
 */
public class UploadUtils {

	private static final List<String> EXTENSOES_IMAGEM_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

	/**
	 * Extrai a extensão de um arquivo em letras minúsculas.
	 */
	public static String extrairExtensao(String nomeArquivo) {
		if (nomeArquivo == null || !nomeArquivo.contains(".")) {
			return "";
		}
		return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase().trim();
	}

	/**
	 * Valida se a extensão do arquivo corresponde a uma imagem válida (jpg, jpeg, png, gif, webp).
	 */
	public static boolean isImagemValida(String nomeArquivo) {
		String extensao = extrairExtensao(nomeArquivo);
		return EXTENSOES_IMAGEM_PERMITIDAS.contains(extensao);
	}

	/**
	 * Gera um nome único para o arquivo usando UUID, preservando a extensão original.
	 */
	public static String gerarNomeUnico(String nomeOriginal) {
		String extensao = extrairExtensao(nomeOriginal);
		String uuid = UUID.randomUUID().toString();
		if (extensao.isEmpty()) {
			return uuid;
		}
		return uuid + "." + extensao;
	}

	/**
	 * Remove caracteres especiais e espaços de um nome de arquivo para segurança.
	 */
	public static String sanitizarNomeArquivo(String nomeArquivo) {
		if (nomeArquivo == null) return "";
		return nomeArquivo.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
	}
}
