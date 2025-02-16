/*     Проверка кода называется тестированием. Подробно вы изучите эту тему дальше в курсе. Тем не менее, сам процесс тестирования можно начать уже сейчас. Создайте в классе Main метод static void main(String[] args) и внутри него:

        Создайте две задачи, а также эпик с двумя подзадачами и  эпик с одной подзадачей.
        Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
        Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        И, наконец, попробуйте удалить одну из задач и один из эпиков.
        Воспользуйтесь дебаггером среды разработки, чтобы понять логику работы программы и отладить её.
    Не оставляйте в коде мусор — превращённые в комментарии или ненужные куски кода. Это сквозной проект, на его основе вы будете делать несколько следующих домашних заданий.
    Давайте коммитам осмысленные комментарии: порядок в репозитории и коде — ключ к успеху написания хороших программ.*/


import ru.yandex.practicum.cva.tasktracker.*;



public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager tm = new TaskManager();


        // Создание объектов

        Task firstCommonTask = new CommonTask("Первая" );
        Task secondCommonTask = new CommonTask("Вторая", "Описание второй");
        Task firstEpicTask = new EpicTask("Первый эпик");
        Task secondEpicTask = new EpicTask("Второй Эпик", "Описание второго эпика");

        Task firstSubTask = ((EpicTask) firstEpicTask).createSubtask("Первая подзадача");
        Task secondSubTask = ((EpicTask) firstEpicTask).createSubtask("Вторая подзадача", "Принадлежит первому Эпику");
        Task thirdSubtask = ((EpicTask) secondEpicTask).createSubtask("Третья подзадача", "Принадлежит второму Эпику");

        // 'Создание' задач

        tm.createTask(firstCommonTask);
        tm.createTask(secondCommonTask);
        tm.createTask(firstEpicTask);
        tm.createTask(secondEpicTask);

        // Проверим что все задачи включая подзадачи добавились в TaskManager
        System.out.println("Первый Тест:");
        tm.getTasks(TaskTypes.EPIC).forEach(System.out::println);
        tm.getTasks(TaskTypes.DEFAULT).forEach(System.out::println);
        tm.getTasks(TaskTypes.SUBTASK).forEach(System.out::println);

        // Проверим что создание задач отдельно не приведет ни к чему в случае если задачи уже добавлены
        System.out.println("---------------------------------------");
        System.out.println("Второй Тест:");
        tm.createTask(firstSubTask );
        tm.createTask(secondSubTask );
        tm.createTask(thirdSubtask );

        tm.getTasks(TaskTypes.EPIC).forEach(System.out::println);
        tm.getTasks(TaskTypes.DEFAULT).forEach(System.out::println);
        tm.getTasks(TaskTypes.SUBTASK).forEach(System.out::println);

        System.out.println("---------------------------------------");
        System.out.println("Третий Тест поменяем у второй простой задачи статус на выполнено:");
        secondCommonTask.setStatus(Statuses.DONE);
        secondCommonTask = tm.updateTask(secondCommonTask);

        tm.getTasks(TaskTypes.DEFAULT).forEach(System.out::println);

        System.out.println("---------------------------------------");
        System.out.println("Четвертый Тест поменяем у первой и третьей подзадачи статус на выполнено:");
        firstSubTask.setStatus(Statuses.DONE);
        thirdSubtask.setStatus(Statuses.DONE);
        firstSubTask = tm.updateTask(firstSubTask);
        secondSubTask = tm.updateTask(thirdSubtask);

        tm.getTasks(TaskTypes.EPIC).forEach(System.out::println);
        tm.getTasks(TaskTypes.SUBTASK).forEach(System.out::println);


        System.out.println("---------------------------------------");

        System.out.println("Пятый Тест поменяем у первого Эпика статус на выполнено (Должен остаться IN_PROGRESS):");
        firstEpicTask.setStatus(Statuses.DONE);
        tm.getTasks(TaskTypes.EPIC).forEach(System.out::println);

        System.out.println("---------------------------------------");
        System.out.println("Шестой Тест удалим вторую задачу и второй эпик");

        tm.removeTask(1);
        tm.removeTask(5);

        tm.getTasks(TaskTypes.EPIC).forEach(System.out::println);
        tm.getTasks(TaskTypes.DEFAULT).forEach(System.out::println);
        tm.getTasks(TaskTypes.SUBTASK).forEach(System.out::println);



    }
}
