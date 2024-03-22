# !/bin/bash
docker build -t googol .
docker run -d --name googol-container -p 5433:5432 googol
