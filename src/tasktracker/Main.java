package tasktracker;

public class Main {

    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Epic epic = new Epic("Заняться учебой", "Подготовить домашнюю работу");
        inMemoryTaskManager.createEpic(epic);
        Task task1 = new Task("Убраться в прихожей", "Помыть пол");
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Постирать вещи", "Загрузить в стиралку");
        inMemoryTaskManager.createTask(task2);
        Subtask subtask1 = new Subtask("Английский", "1.1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Математика", "2.1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask2);

        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubtasks());

        task1.setProgress(Progress.DONE);
        task2.setProgress(Progress.DONE);
        subtask1.setProgress(Progress.DONE);
        subtask2.setProgress(Progress.DONE);
        inMemoryTaskManager.updateTask(task1);
        inMemoryTaskManager.updateTask(task2);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        printAllTasks(inMemoryTaskManager, inMemoryHistoryManager);
    }

    private static void printAllTasks(TaskManager manager, HistoryManager historyManager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtasksForEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

}
