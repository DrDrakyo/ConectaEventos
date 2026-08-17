package br.com.conectaeventos.utils;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Responsável por salvar em disco as imagens enviadas em avaliar-prestador.html
 * (campos "Adicionar foto 1" e "Adicionar foto 2", que alimentam
 * CONTRATACAO.ava_imagem1 e ava_imagem2). O Servlet chamador deve estar
 * anotado com @MultipartConfig para que o Part seja recebido corretamente.
 */
public class UploadUtils {

    private static final String PASTA_AVALIACOES = "uploads" + File.separator + "avaliacoes";

    private UploadUtils() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Salva o arquivo enviado dentro de {caminhoBase}/uploads/avaliacoes e
     * retorna o caminho relativo (para gravar em ava_imagem1/ava_imagem2).
     * Retorna null se nenhum arquivo tiver sido enviado (foto opcional).
     *
     * @param parte      o Part recebido via request.getPart("...")
     * @param caminhoBase normalmente getServletContext().getRealPath("/")
     */
    public static String salvarImagem(Part parte, String caminhoBase) throws IOException {
        if (parte == null || parte.getSize() == 0) {
            return null;
        }

        String nomeOriginal = extrairNomeArquivo(parte);
        String extensao = nomeOriginal.contains(".")
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf('.'))
                : "";
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        File pastaDestino = new File(caminhoBase, PASTA_AVALIACOES);
        if (!pastaDestino.exists()) {
            pastaDestino.mkdirs();
        }

        File arquivoDestino = new File(pastaDestino, nomeArquivo);
        try (InputStream entrada = parte.getInputStream()) {
            Files.copy(entrada, arquivoDestino.toPath());
        }

        return PASTA_AVALIACOES.replace(File.separator, "/") + "/" + nomeArquivo;
    }

    private static String extrairNomeArquivo(Part parte) {
        String cabecalho = parte.getHeader("content-disposition");
        if (cabecalho == null) {
            return "arquivo";
        }
        for (String token : cabecalho.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "arquivo";
    }
}