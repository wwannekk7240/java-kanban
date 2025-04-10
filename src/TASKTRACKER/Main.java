package TASKTRACKER;

public class Main {

    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task1 = taskManager.createTask("Помыть пол", "Взять швабру");
        Task task2 = taskManager.createTask("Постирать вещи", "Загрузить в стиралку");
        Epic epic = taskManager.createEpic("Сделать уроки", "По школе");
        Subtask subtask1 = taskManager.createSubtask("Английский", "1.1", epic.getId());
        Subtask subtask2 = taskManager.createSubtask("Математика", "2.1", epic.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.updateTaskProgress(task1.getId(), TaskProgress.Progress.DONE);
        taskManager.updateTaskProgress(task2.getId(), TaskProgress.Progress.DONE);
        taskManager.updateSubtaskProgress(subtask1.getId(), TaskProgress.Progress.DONE);
        taskManager.updateSubtaskProgress(subtask2.getId(), TaskProgress.Progress.DONE);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }

}
