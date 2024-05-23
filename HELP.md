### Deployment
```bash
mvn package
docker image build -t alxinsh/docker-java-hw30-billing:1.0.5 .
docker push alxinsh/docker-java-hw30-billing:1.0.5
```