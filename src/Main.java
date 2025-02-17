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

        Task firstCommonTask = new Task("Первая" );
        Task     secondCommonTask = new Task("Вторая", "Описание второй");
        EpicTask firstEpicTask  = new EpicTask("Первый эпик");
        EpicTask secondEpicTask = new EpicTask("Второй Эпик", "Описание второго эпика");

        SubTask firstSubTask = new SubTask("Первая подзадача");
        SubTask    secondSubTask = new SubTask("Вторая подзадача", "Принадлежит первому Эпику");
        SubTask thirdSubTask  = new SubTask("Третья подзадача", "Принадлежит второму Эпику");

        // 'Создание' задач

        tm.createTask(firstCommonTask);
        tm.createTask(secondCommonTask);
        tm.createEpic(firstEpicTask);
        tm.createEpic(secondEpicTask);
        tm.createSubTask(firstSubTask);
        tm.createSubTask(secondSubTask);
        tm.createSubTask(thirdSubTask);

        System.out.println("Первый Тест Проверим что все задачи включая подзадачи добавились в TaskManager:");
        tm.getAllTask().forEach(System.out::println);
        tm.getAllEpic().forEach(System.out::println);
        tm.getAllSubtask().forEach(System.out::println);

        //
        System.out.println("---------------------------------------");
        System.out.println("Второй Тест Проверим что создание задач отдельно не приведет ни к чему в случае если задачи уже добавлены:");
        tm.createTask(firstSubTask );
        tm.createTask(secondSubTask );
        tm.createTask(thirdSubTask );

        tm.getAllTask().forEach(System.out::println);
        tm.getAllEpic().forEach(System.out::println);
        tm.getAllSubtask().forEach(System.out::println);


        System.out.println("---------------------------------------");


        System.out.println("Третий тест обновим подзадачи и эпики чтобы связать их:");

        firstEpicTask.addNewSubtask(5);
        firstEpicTask.addNewSubtask(6);
        secondEpicTask.addNewSubtask(7);

        firstSubTask.setParentId(3);
        secondSubTask.setParentId(3);
        thirdSubTask.setParentId(4);

        tm.updateEpic(firstEpicTask);
        tm.updateEpic(firstEpicTask);

        tm.updateSubTask(firstSubTask);
        tm.updateSubTask(secondSubTask);
        tm.updateSubTask(thirdSubTask);

        tm.getAllTask().forEach(System.out::println);
        tm.getAllEpic().forEach(System.out::println);
        tm.getAllSubtask().forEach(System.out::println);

        System.out.println("---------------------------------------");


        System.out.println("Четвертый Тест поменяем у второй простой задачи статус на выполнено:");
        secondCommonTask.setStatus(Statuses.DONE);
        secondCommonTask = tm.updateTask(secondCommonTask);

        tm.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------");
        System.out.println("Пятый Тест поменяем у первой и третьей подзадачи статус на выполнено:");
        firstSubTask.setStatus(Statuses.DONE);
        thirdSubTask.setStatus(Statuses.DONE);
        firstSubTask = tm.updateSubTask(firstSubTask);
        secondSubTask = tm.updateSubTask(thirdSubTask);

        tm.getAllEpic().forEach(System.out::println);
        tm.getAllSubtask().forEach(System.out::println);


        System.out.println("---------------------------------------");

        System.out.println("Шестой Тест поменяем у первого Эпика статус на выполнено (Должен остаться IN_PROGRESS):");
        firstEpicTask.setStatus(Statuses.DONE);
        tm.updateEpic(firstEpicTask);

        tm.getAllEpic().forEach(System.out::println);

        System.out.println("---------------------------------------");
        System.out.println("Седьмой Тест удалим вторую задачу и второй эпик");

        tm.deleteTaskById(2);
        tm.deleteEpicById(4);

        tm.getAllTask().forEach(System.out::println);
        tm.getAllEpic().forEach(System.out::println);
        tm.getAllSubtask().forEach(System.out::println);



    }
}
