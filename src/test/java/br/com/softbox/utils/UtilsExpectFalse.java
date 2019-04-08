package br.com.softbox.utils;


@FunctionalInterface
public interface UtilsExpectFalse {
    void go(boolean condition, String publisher, String msgOnError);
}
