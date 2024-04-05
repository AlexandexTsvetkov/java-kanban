package ru.yandex.javacource.tsvetkov.javacanban;

import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
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

        Epic loseWeight = new Epic("Похудеть", "Нужно похудеть на 10 кг");

        epicId = taskManager.addNewEpic(loseWeight);

        Subtask eatLess = new Subtask("Есть меньше", "Можно есть овощи", epicId);

        taskManager.addNewTask(runFiveRings);
        taskManager.addNewTask(eatAnApple);
        taskManager.addNewSubtask(coockFirstDish);
        taskManager.addNewSubtask(coocSecondDish);
        taskManager.addNewSubtask(eatLess);

        taskManager.getTask(3);
        taskManager.getEpic(1);
        taskManager.getSubTask(7);

        printAllTasks(taskManager);

//        System.out.println(taskManager.getTasks());
//        System.out.println(taskManager.getEpics());
//        System.out.println(taskManager.getSubTasks());
//
//        runFiveRings = taskManager.getTask(runFiveRings.getId());
//        eatAnApple = taskManager.getTask(eatAnApple.getId());
//        coockLunch = taskManager.getEpic(coockLunch.getId());
//        coockFirstDish = taskManager.getSubTask(coockFirstDish.getId());
//        coocSecondDish = taskManager.getSubTask(coocSecondDish.getId());
//        loseWeight = taskManager.getEpic(loseWeight.getId());
//        eatLess = taskManager.getSubTask(eatLess.getId());
//
//        runFiveRings = new Task(runFiveRings.getName(), runFiveRings.getDescription(), runFiveRings.getId());
//        eatAnApple = new Task(eatAnApple.getName(), eatAnApple.getDescription(), eatAnApple.getId());
//        coockLunch = new Epic(coockLunch.getName(), coockLunch.getDescription(), coockLunch.getId(), coockLunch.getSubtasksId());
//        coockFirstDish = new Subtask(coockFirstDish.getName(), coockFirstDish.getDescription(), coockFirstDish.getId(), coockFirstDish.getEpicId());
//        coocSecondDish = new Subtask(coocSecondDish.getName(), coocSecondDish.getDescription(), coocSecondDish.getId(), coocSecondDish.getEpicId());
//        loseWeight = new Epic(loseWeight.getName(), loseWeight.getDescription(), loseWeight.getId(), loseWeight.getSubtasksId());
//        eatLess = new Subtask(eatLess.getName(), eatLess.getDescription(), eatLess.getId(), eatLess.getEpicId());
//
//        runFiveRings.setStatus(Status.DONE);
//        eatAnApple.setStatus(Status.IN_PROGRESS);
//
//        coockFirstDish.setStatus(Status.DONE);
//        coocSecondDish.setStatus(Status.IN_PROGRESS);
//
//        eatLess.setStatus(Status.DONE);
//
//        loseWeight.setName("Новое имя");
//
//        taskManager.updateTask(runFiveRings);
//        taskManager.updateTask(eatAnApple);
//        taskManager.updateEpic(coockLunch);
//        taskManager.updateSubtask(coockFirstDish);
//        taskManager.updateSubtask(coocSecondDish);
//        taskManager.updateEpic(loseWeight);
//        taskManager.updateSubtask(eatLess);
//
//        System.out.println();
//        System.out.println(taskManager.getTask(runFiveRings.getId()));
//        System.out.println(taskManager.getTask(eatAnApple.getId()));
//        System.out.println(taskManager.getEpic(coockLunch.getId()));
//        System.out.println(taskManager.getSubTask(coockFirstDish.getId()));
//        System.out.println(taskManager.getSubTask(coocSecondDish.getId()));
//        System.out.println(taskManager.getEpic(loseWeight.getId()));
//        System.out.println(taskManager.getSubTask(eatLess.getId()));
//
//        taskManager.removeTask(runFiveRings.getId());
//        taskManager.removeSubtask(coockFirstDish.getId());
//        taskManager.removeEpic(loseWeight.getId());
//
//        System.out.println();
//        System.out.println(taskManager.getTasks());
//        System.out.println(taskManager.getEpics());
//        System.out.println(taskManager.getSubTasks());

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
