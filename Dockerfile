# ==========================================
# Estágio 1: Compilação do código Java
# ==========================================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copia o código fonte e recursos estáticos/bibliotecas
COPY src/ src/
COPY WebContent/ WebContent/

# Cria o diretório de classes
RUN mkdir -p build/WEB-INF/classes

# Compila todos os arquivos Java utilizando os JARs de WebContent/WEB-INF/lib
RUN find src/main/java -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -cp "WebContent/WEB-INF/lib/*" -d build/WEB-INF/classes @sources.txt

# Monta a estrutura da aplicação Web e remove servlet-api (já provido pelo Tomcat)
RUN cp -r WebContent/* build/ && \
    rm -f build/WEB-INF/lib/servlet-api.jar

# ==========================================
# Estágio 2: Runtime com Apache Tomcat 9
# ==========================================
FROM tomcat:9.0-jdk17-temurin

# Limpa aplicações padrão do Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Implanta a aplicação no contexto /ConectaEventos e também como ROOT (/)
COPY --from=builder /app/build /usr/local/tomcat/webapps/ConectaEventos
COPY --from=builder /app/build /usr/local/tomcat/webapps/ROOT

EXPOSE 8080

CMD ["catalina.sh", "run"]
