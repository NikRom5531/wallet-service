# wallet-service

## Установка и запуск

1. Клонировать репозиторий 
    ```bash
    git clone https://github.com/NikRom5531/wallet-service.git
    ```
2. Создать в корневой папке файл `.env` и прописать данные для БД (`DB_USER`, `DB_PASSWORD` и `DB_NAME`)

3. Запустить `docker-compose` через терминал
    ```bash
    docker-compose up --build
    ```