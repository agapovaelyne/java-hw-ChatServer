# Курсовой проект "Сетевой чат" - серверная часть

## Особенности реализации:
- **Параметры запуска и работы сервера устанавливаются файлом конфигурации:** 
  
    см. [settings.conf](src/main/resources/settings.conf);
- **Сервер работает в многопоточном режиме.**
  
    В исходном варианте для разработки и тестирования конфигурацией установлено всего 10 потоков, в эксплуатации число потоков долно быть увеличено);
- **На сервере реализовано логирование:**
  
    логируются все операции (уровень DEBUG) и ошибки (уровень ERROR) сервера посредством log4j, 
  
    запись лога осуществляется в [server.log](log/server.log),
  
    в исходном варианте запись логов DEBUG и ERROR отключена [настройками](src/main/resources/log4j.properties);
- **Ведется запись всех отправленных через сервер сообщений:** 
  
    в записях фиксируется имя пользователя и время отправки, 
    
    запись ведется посредством механизма логирования (уровень INFO);
- **При реализации использовался сборщик пакетов maven:**
  
    см. [pom.xml](pom.xml);
- **Код покрыт unit-тестами:**
  
    классы тестов реализованы в [src/test/java/server](src/test/java/server).
  

_Код и описание клиентской части проекта можно найти [здесь](https://github.com/agapovaelyne/java-hw-ChatClient)_
