package ru.yandex.javacource.tsvetkov.javacanban.manager;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException(final String message) {
        super(message);
    }
}
