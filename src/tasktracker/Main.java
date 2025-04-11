package tasktracker;

public class Main {

    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Epic epic = new Epic("Заняться учебой", "Подготовить домашнюю работу");
        taskManager.createEpic(epic);
        Task task1 = new Task("Убраться в прихожей", "Помыть пол");
        taskManager.createTask(task1);
        Task task2 = new Task("Постирать вещи", "Загрузить в стиралку");
        taskManager.createTask(task2);
        Subtask subtask1 = new Subtask("Английский", "1.1", epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Математика", "2.1", epic.getId());
        taskManager.createSubtask(subtask2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        task1.setProgress(Progress.DONE);
        task2.setProgress(Progress.DONE);
        subtask1.setProgress(Progress.DONE);
        subtask2.setProgress(Progress.DONE);
        taskManager.updateTaskProgress(task1);
        taskManager.updateTaskProgress(task2);
        taskManager.updateSubtaskProgress(subtask1);
        taskManager.updateSubtaskProgress(subtask2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }

}
