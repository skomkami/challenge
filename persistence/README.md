To create elasticsearch container run command: 

```docker run --rm -d --name elastic -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.9.0```

To create keycloak container run command:

```docker run -it -d -p 8088:8080 --name keycloak kamilskomro/kc:1.0```

To create ui container run command:

```docker run -it -d -p 4200:80 --name ui kamilskomro/challenge-ui:1.0```
docker run -it -d -p 4200:80 --name ui kamilskomro/challenge-ui:1.0 