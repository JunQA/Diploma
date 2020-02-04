# Дипломный проект
### Документация
* [План автоматизации](https://github.com/JunQA/Diploma/blob/master/docs/Plan.md)

* [Отчёт по итогам тестирования](https://github.com/JunQA/Diploma/blob/master/docs/Report.md)

* [Отчёт по итогам автоматизации](https://github.com/JunQA/Diploma/blob/master/docs/Summary.md)

* [Issues](https://github.com/JunQA/Diploma/issues)


### Как запустить
Для запуска контейнеров используйте команду:
```` 
docker-compose up -d
````
Для запуска SUT под MySQL используйте команду:
````
java -Dspring.datasource.url=jdbc:mysql://localhost:3306/app -jar artifacts/aqa-shop.jar
````
Для запуска SUT под PostgreSQL используйте команду:
````
java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -jar artifacts/aqa-shop.jar
````
##### Тестирование (Gradlew + Allure)
Для запуска под MySQL используйте команду:
````
gradlew -Ddb.url=jdbc:mysql://localhost:3306/app clean test allureReport
````
Для запуска под PostgreSQL используйте команду:
````
gradlew -Ddb.url=jdbc:postgresql://localhost:5432/app clean test allureReport
````
##### Формирования отчётов (Allure)
Для получения отчета используйте команду:
````
gradlew allureServe
````
##### Завершение
````
docker-compose down
````
