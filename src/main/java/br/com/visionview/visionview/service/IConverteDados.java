package br.com.visionview.visionview.service;

public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
