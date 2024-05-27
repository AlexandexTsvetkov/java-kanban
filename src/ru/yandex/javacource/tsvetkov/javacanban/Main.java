package ru.yandex.javacource.tsvetkov.javacanban;

import ru.yandex.javacource.tsvetkov.javacanban.manager.FileBackedTaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        File file = new File("resources/ManagerMain.txt");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        Task runFiveRings = new Task("Пробежать 5 кругов", "Очень быстро надо пробежать", LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        Task eatAnApple = new Task("Съесть яблоко", "Съесть нужно целиком", LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));

        Epic coockLunch = new Epic("Приготовить обед", "Должно быть вкусно");

        int epicId = taskManager.addNewEpic(coockLunch);

        Subtask coockFirstDish = new Subtask("Приготовить первое блюдо", "Желательно суп", epicId, LocalDateTime.of(2024, 1, 3, 0, 0), Duration.ofDays(1));
        Subtask coocSecondDish = new Subtask("Приготовить второе блюдо", "Желательно макароны", epicId, LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));

        Epic loseWeight = new Epic("Похудеть", "Нужно похудеть на 10 кг");

        epicId = taskManager.addNewEpic(loseWeight);

        Subtask eatLess = new Subtask("Есть меньше", "Можно есть овощи", epicId, LocalDateTime.of(2024, 1, 5, 1, 1), Duration.ofDays(1));

        taskManager.addNewTask(runFiveRings);
        taskManager.addNewTask(eatAnApple);
        taskManager.addNewSubtask(coockFirstDish);
        taskManager.addNewSubtask(coocSecondDish);
        taskManager.addNewSubtask(eatLess);

        printAllTasks(taskManager);

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);

        printAllTasks(newTaskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}