@echo off

REM Check if the container already exists
docker inspect googol-container > nul 2>&1
if %errorlevel% equ 0 (
    REM If the container exists, just start it
    docker start googol-container
) else (
    REM If the container doesn't exist, build it and then run it
    docker build -t googol .
    docker run -d --name googol-container -p 5433:5432 googol
)
