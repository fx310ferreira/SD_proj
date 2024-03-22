docker build -t googol .
docker run -d --name googol-container -p 5432:5432 googol
