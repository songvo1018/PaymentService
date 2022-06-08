# Конфигурация

В `src/main/resources/application.config` можно изменить данные для подключения к MongoDB, а так же значения тестовых 
данных.

# Приложение
Запуск: запустите метод `main` средствами IDE `src/main/java/org/nosov/Main.java`
![Screenshot](screenshot.png)

# Тесты
Запуск: запустите класс `src/test/java/org/nosov/TestPaymentsServlet.java`
![Screenshot1](screenshot1.png)

# Тест условия задания

Я не придумал ничего лучше, как симулировать ситуацию обращения из нескольких потоков, чтобы проверить условие 
"одновременного получения запросов с одинаковым новым id от разных клиентов". Не уверен, что это корректный вариант. 
У меня нет опыта написания тестов, поэтому если у вас будет предложение или материал (ссылки, возможно просто совет),
буду признателен за помощь.

Запуск: запустите класс `src/test/java/org/nosov/TestPaymentsServlet.java`
![Screenshot2](screenshot2.png)

Я был бы очень рад предоставить вам проект в контейнерах, но увы, я потерпел неудачу.