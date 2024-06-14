package ru.yandex.javacource.tsvetkov.javacanban.manager;

public class NotFoundExeption extends RuntimeException {
    public NotFoundExeption(final String message) {
        super(message);
    }
}
