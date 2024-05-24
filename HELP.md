### Deployment
```bash
mvn package
docker image build -t alxinsh/docker-java-hw35-billing:1.0.0 .
docker push alxinsh/docker-java-hw35-billing:1.0.0
```

#### Add an Account
```bash
curl -H 'Content-Type: application/json' \
     -d '{ "name":"Василий Пупкин"}' \
     -X POST \
     http://localhost:8082/account | json_pp
```

```bash
curl http://localhost:8082/account | json_pp
```

#### Topup
```bash
curl -H 'Content-Type: application/json' \
     -d '{ "amount":"20.2"}' \
     -X POST \
     http://localhost:8082/account/top-up/1 | json_pp
```