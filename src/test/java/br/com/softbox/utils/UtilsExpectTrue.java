package br.com.softbox.utils;

@FunctionalInterface
public interface UtilsExpectTrue {
    void go(boolean condition, String publisher, String msgOnError);
}
