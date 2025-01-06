Структура Таблицы 
-- Контакты
--     id PK
--     Имя
--     Фамилия
--     Телефонный номер
--     Email

CREATE TABLE contacts (
       id SERIAL PRIMARY KEY,
       first_name VARCHAR(50) NOT NULL,
       last_name VARCHAR(50) NOT NULL,
       phone_number VARCHAR(15),
       email VARCHAR(100)
   );
