How to run the project locally

1. docker compose up -d
2. docker build -t carreservation .
3. docker run --network=carreservation_default -p 8080:8080 carreservation