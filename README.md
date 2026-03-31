# issLab2026
Repository del corso Ingegneria dei Sistemi Software a.a. 2025/2026 - DISI - University of Bologna

  * [contenuti HTML](https://anatali.github.io/issLab2026/index.html)
  * [Testo di riferimento](https://anatali.github.io/issLab2026/_static/docs/Protobook.pdf)
  * [Gradle Tutorial](https://anatali.github.io/issLab2026/_static/docs/GradleTutorialForCompleteBeginners.pdf)
    
# Lectures

  * [Lectures HTML](https://anatali.github.io/issLab2026/LectureBologna2026.html)

# Chronicle [index](https://anatali.github.io/issLab2026/chronicle/index.html)

# Parte A: Dai programmi ai Sistemi a Microservizi

  [ConwayLife Project](https://anatali.github.io/issLab2026/Project%20conway26Java.html)  
  * [ConwayLife Sprint 1 code](ConwayLife/Sprint1/conway26Java) | 📄 [conway26Java_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/ConwayLife/Sprint1/conway26Java/userDocs/conway26Java_v0.html)
  * [ConwayLife Sprint 2 code](ConwayLife/Sprint2/conway26JavaSwing) | 📄 [conway26JavaSwing_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/ConwayLife/Sprint2/conway26JavaSwing/userDocs/conway26JavaSwing_v0.html)
  * [ConwayLife Sprint 3 code](ConwayLife/Sprint3/conway26GuiHtml) | 📄 [Sprint3_ConwayWeb.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/ConwayLife/Sprint3/conway26GuiHtml/userDocs/Sprint3_ConwayWeb.html)

  **Sprint 3: Dai Monoliti ai Microservizi (Protoattori)** | [Codice Sorgente](AService/Sprint1/SistemaSJavalin) | 📄 [Sprint3_Protoattori.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/AService/Sprint1/SistemaSJavalin/userDocs/Sprint3_Protoattori.html)
  * **Fase 1 (Monolite & Docker):** Realizzazione di un servizio monolitico in Java per il calcolo di un'espressione matematica (`SistemaSJavalinBetterApplMsgs`) con deployment del servizio in Docker usando il framework Javalin.
  * **Fase 2 (Refactoring ad Attori):** Transizione dal bottom-up al top-down. Refactoring del sistema (`SistemaSProtoactor`) usando l'infrastruttura a Protoattori (`MathActor`) per delegare il lavoro pesante in modo asincrono, evitando il blocco del server web.
