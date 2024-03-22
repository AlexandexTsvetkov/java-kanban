
public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task runFiveRings = new Task("Пробежать 5 кругов", "Очень быстро надо пробежать");

        Task eatAnApple = new Task("Съесть яблоко", "Съесть нужно целиком");

        Epic coockLunch = new Epic("Приготовить обед", "Должно быть вкусно");
        Subtask coockFirstDish = new Subtask("Приготовить первое блюдо", "Желательно суп", coockLunch.getId());
        Subtask coocSecondDish = new Subtask("Приготовить второе блюдо", "Желательно макароны", coockLunch.getId());

        Epic loseWeight = new Epic("Похудеть", "Нужно похудеть на 10 кг");
        Subtask eatLess = new Subtask("Есть меньше", "Можно есть овощи", loseWeight.getId());

        taskManager.createTask(runFiveRings);
        taskManager.createTask(eatAnApple);
        taskManager.createTask(coockLunch);
        taskManager.createTask(coockFirstDish);
        taskManager.createTask(coocSecondDish);
        taskManager.createTask(loseWeight);
        taskManager.createTask(eatLess);

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList());

        runFiveRings = taskManager.getTaskById(runFiveRings.getId());
        eatAnApple = taskManager.getTaskById(eatAnApple.getId());
        coockLunch = taskManager.getEpicById(coockLunch.getId());
        coockFirstDish = taskManager.getSubtaskById(coockFirstDish.getId());
        coocSecondDish = taskManager.getSubtaskById(coocSecondDish.getId());
        loseWeight = taskManager.getEpicById(loseWeight.getId());
        eatLess = taskManager.getSubtaskById(eatLess.getId());


        runFiveRings.setStatus(Status.DONE);
        eatAnApple.setStatus(Status.IN_PROGRESS);

        coockFirstDish.setStatus(Status.DONE);
        coocSecondDish.setStatus(Status.IN_PROGRESS);

        eatLess.setStatus(Status.DONE);

        taskManager.updateTask(runFiveRings);
        taskManager.updateTask(eatAnApple);
        taskManager.updateTask(coockLunch);
        taskManager.updateTask(coockFirstDish);
        taskManager.updateTask(coocSecondDish);
        taskManager.updateTask(loseWeight);
        taskManager.updateTask(eatLess);

        System.out.println();
        System.out.println(taskManager.getTaskById(runFiveRings.getId()));
        System.out.println(taskManager.getTaskById(eatAnApple.getId()));
        System.out.println(taskManager.getEpicById(coockLunch.getId()));
        System.out.println(taskManager.getSubtaskById(coockFirstDish.getId()));
        System.out.println(taskManager.getSubtaskById(coocSecondDish.getId()));
        System.out.println(taskManager.getEpicById(loseWeight.getId()));
        System.out.println(taskManager.getSubtaskById(eatLess.getId()));

        taskManager.removeTaskById(runFiveRings.getId());
        taskManager.removeTaskById(coockFirstDish.getId());
        taskManager.removeTaskById(loseWeight.getId());

        System.out.println();
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList());

    }

}
