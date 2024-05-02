### Deployment
```bash
mvn package
docker image build -t alxinsh/docker-java-hw30-payment:1.0.5 .
docker push alxinsh/docker-java-hw30-payment:1.0.5
```