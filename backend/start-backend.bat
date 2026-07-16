@echo off
title PGE-CE - Backend Starter
color 0A

echo ====================================================
echo      INICIANDO O ECOSSISTEMA BACKEND (PGE-CE)      
echo ====================================================
echo.

:: 1. Subindo a infraestrutura do Docker (Banco, Redis e RabbitMQ)
echo [1/5] Iniciando PostgreSQL, Redis e RabbitMQ no Docker...
docker compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Falha ao subir o Docker. Certifique-se de que o Docker Desktop esta aberto!
    pause
    exit /b
)
echo Docker subiu com sucesso. Aguardando 5 segundos para inicialização dos serviços...
timeout /t 15 /nobreak >nul

:: 2. Iniciando o Eureka Server
echo [2/5] Iniciando Eureka Server (Porta 8761)...
start "Eureka Server [8761]" cmd /k "cd eureka-server && mvnw spring-boot:run"
echo Aguardando 12 segundos para o Eureka estabilizar antes dos outros servicos...
timeout /t 15 /nobreak >nul

:: 3. Iniciando o API Gateway
echo [3/5] Iniciando API Gateway (Porta 8080)...
start "API Gateway [8080]" cmd /k "cd gateway && mvnw spring-boot:run"
timeout /t 15 /nobreak >nul

:: 4. Iniciando o Account Service
echo [4/5] Iniciando Account Service (Porta 8081)...
start "Account Service [8081]" cmd /k "cd account-service && mvnw spring-boot:run"
timeout /t 15 /nobreak >nul

:: 5. Iniciando o Ride Service
echo [5/5] Iniciando Ride Service (Porta 8082)...
start "Ride Service [8082]" cmd /k "cd ride-service && mvnw spring-boot:run"

echo.
echo ====================================================
echo  Tudo pronto! As janelas dos servicos estao abrindo.
echo  Mantenha este terminal aberto se quiser gerenciar.
echo ====================================================
echo.
pause