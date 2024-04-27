package ru.yandex.javacource.tsvetkov.javacanban;

import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        Task runFiveRings = new Task("Пробежать 5 кругов", "Очень быстро надо пробежать");

        Task eatAnApple = new Task("Съесть яблоко", "Съесть нужно целиком");

        Epic coockLunch = new Epic("Приготовить обед", "Должно быть вкусно");

        int epicId = taskManager.addNewEpic(coockLunch);

        Subtask coockFirstDish = new Subtask("Приготовить первое блюдо", "Желательно суп", epicId);
        Subtask coocSecondDish = new Subtask("Приготовить второе блюдо", "Желательно макароны", epicId);
        Subtask coockTea = new Subtask("Приготовить чай", "Желательно горячий", epicId);

        Epic loseWeight = new Epic("Похудеть", "Нужно похудеть на 10 кг");

        taskManager.addNewEpic(loseWeight);

        taskManager.addNewTask(runFiveRings);
        taskManager.addNewTask(eatAnApple);
        taskManager.addNewSubtask(coockFirstDish);
        taskManager.addNewSubtask(coocSecondDish);
        taskManager.addNewSubtask(coockTea);

        taskManager.getTask(3);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.getSubTask(5);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.getEpic(1);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.getSubTask(7);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.getEpic(1);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.getTask(3);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");
        taskManager.removeTask(3);
        System.out.println(taskManager.getHistory());
        taskManager.removeEpic(1);
        System.out.println(taskManager.getHistory());
        System.out.println("/////////////////////////////");

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
