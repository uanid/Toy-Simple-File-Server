# Simple File Server

A simple (upload/download)-able file server.

No authentication feature. Please use this application on reliable network.

If you want setup to anonymous users can download and reliable users can upload/download, See below architecture diagrams.

![SampleImage](https://github.com/Uanid/Toy-Simple-File-Server/blob/master/persistence/sample1.PNG?raw=true)

# How to use

Default server port: 2000

In Docker
```docker

Man
1. docker run uanid/simple-file-server <root-context-path>
2. docker run -e ROOT_PATH=<root-context-path> unaid/simple-file-server

Example
docker run -d -p 80:2000 -v /mnt/p:/mnt/persistence uanid/simple-file-server /mnt/persitence
```

In DockerCompose
```
version: "3.7"
services:
  up-file-server:
    image: uanid/simple-file-server
    ports:
      - 8080:2000
    environment:
      ROOT_PATH: /mnt/persistence
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /mnt/persistence:/mnt/persistence
    container_name: up-file-server
    hostname: up-file-server

```

# Recommended Setup
![diagram-sample](https://github.com/Uanid/Toy-Simple-File-Server/blob/master/persistence/sample2.PNG?raw=true)

In my case, unreliable zone(public internet) users can access only-download server and reliable zone users can access upload/download server.

Example Docker-Compose setup
```
version: "3.7"
services:
  up-file-server:
    image: uanid/simple-file-server
    ports:
      - 8080:2000
    environment:
      ROOT_PATH: /mnt/persistence
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /mnt/persistence:/mnt/persistence
    container_name: up-file-server
    hostname: up-file-server
  down-file-server:
    image: halverneus/static-file-server:latest
    ports:
      - 80:8080
    environment:
      CORS: "true"
      FOLDER: "/var/ftp"
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /mnt/persistence:/var/ftp
    container_name: down-file-server
    hostname: down-file-server

```

Port 80 is only-downloadable port, 8080 is upload/download-able port
If you setup firewall, then upload server will access only reliable users
