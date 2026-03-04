# issLab2026
Laboratorio di **Ingegneria dei Sistemi Software** a.a. 2025/2026 
**Studente:** Alexander Kreuzer  
**Repo:** [ISS26_Alexander_Kreuzer](https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer)

[Testo di riferimento: Protobook](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf)

---

<h2 id="ParteA">Parte A: Dai programmi ai Sistemi a Microservizi</h2>

### Sistema ConwayLife in locale
[Riferimento: conway26Java Dai requisiti al deployment](https://anatali.github.io/issLab2026/Project%20conway26Java.html#conway26java-dai-requisiti-al-deployment)

* **[ConwayLife Sprint 1](https://alexing-uni.github.io/ISS26_Alexander_Kreuzer/ConwayLife/Sprint1/conway26Java/userDocs/conway26Java_v0.html)**
    * **Descrizione:** Analisi dei requisiti e impostazione di un primo prototipo in Java con dispositivi Mock di I/O seguendo i principi della Clean Architecture.
    * **Distribuzione:** File executable JAR.
    * **Documentazione Online:** [Sprint 1 HTML](https://alexing-uni.github.io/ISS26_Alexander_Kreuzer/ConwayLife/Sprint1/conway26Java/userDocs/conway26Java_v0.html)

* **[ConwayLife Sprint 2](./ConwayLife/Sprint2/conway26JavaSwing)**
    * **Evoluzione:** Transizione fondamentale **"Da funzione a servizio"**.
    * **Caratteristiche:** Integrazione del server web **Javalin** e di un dispositivo di output realizzato in **Java Swing**.
    * **Comunicazione:** Lo stato della griglia è esposto tramite endpoint **HTTP/JSON** e il controllo remoto è gestito via **WebSockets**.
    * **Distribuzione:** Creazione di un **Fat JAR** eseguibile che include tutte le dipendenze (Javalin, Jackson, Jetty).



### Sistemi come servizi
* **Separation of Concerns:** Reutilizzo della logica di business dello Sprint 1 (file JAR) come libreria esterna per il servizio del Sprint 2.
* **Serializzazione:** Implementazione di Jackson per il mapping automatico della matrice di gioco in formato JSON.
* **Gestione Connessioni:** Utilizzo di WebSockets per la gestione asincrona dei comandi `next` e `clear`.

### Sistema ConwayLife con pagine HTML (Next Steps)
* **[ConwayLife Sprint 3](./ConwayLife/Sprint3/conway26Java):** Evoluzione del sistema usando una pagina HTML/JS come dispositivo di I/O (Client-Server completo).
* **Distribuzione:** Containerizzazione tramite **Docker yaml**.

---
*Ultimo aggiornamento: Marzo 2026*
