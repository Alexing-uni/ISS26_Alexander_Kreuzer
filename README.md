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
  * [ConwayLife Sprint 1 (Console)](conway26Java) | 📄 [conway26Java_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26Java/userDocs/conway26Java_v0.html)
  * [ConwayLife Sprint 2 (Swing GUI)](conway26JavaSwing) | 📄 [conway26JavaSwing_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26JavaSwing/userDocs/conway26JavaSwing_v0.html)
  * [ConwayLife Sprint 3 (Web GUI)](conway26GuiHtml) | 📄 [Sprint3_ConwayWeb.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26GuiHtml/userDocs/Sprint3_ConwayWeb.html)
  * [ConwayLife Sprint 3 (Protoattori)](conway26Protoactors) | 📄 [ConwayProtoactors.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26Protoactors/userDocs/ConwayProtoactors.html)

  **SistemaS: Dai Monoliti ai Microservizi** | [Codice Sorgente](AService/Sprint1/SistemaSJavalin) | 📄 [Sprint3_Protoattori.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/AService/Sprint1/SistemaSJavalin/userDocs/Sprint3_Protoattori.html)
  **SistemaS: Dai Monoliti ai Microservizi** | [Codice Sorgente](AService/Sprint1/SistemaSJavalin) | 📄 [Sprint3_Protoattori.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/AService/Sprint1/SistemaSJavalin/userDocs/Sprint3_Protoattori.html)
  * **Fase 1 (Monolite & Docker):** Realizzazione di un servizio monolitico in Java per il calcolo di un'espressione matematica (`SistemaSJavalinBetterApplMsgs`) con deployment del servizio in Docker usando il framework Javalin.
  * **Fase 2 (Refactoring ad Attori):** Transizione dal bottom-up al top-down. Refactoring del sistema (`SistemaSProtoactor`) usando l'infrastruttura a Protoattori (`MathActor`) per delegare il lavoro pesante in modo asincrono, evitando il blocco del server web.

# Parte B: Il linguaggio QAK

  **Progetto qakdemo26** | [Sorgenti QAK](qakdemo26/src)
  * [demoSendReceiveEmit.qak](qakdemo26/src/demoSendReceiveEmit.qak) — demo messaggi Dispatch ed Event
  * [conway26qak0.qak](qakdemo26/src/conway26qak0.qak) — modello QAK di ConwayLife con MQTT

  **Progetto sistemasqak** | [Sorgenti](sistemasqak/src)
  * [sistemas.qak](sistemasqak/src/sistemas.qak) — modello QAK del SistemaS (Dispatch + Request, versione del docente)
  * [demoApril21.qak](sistemasqak/src/demoApril21.qak) — variante con `sin(x)+cos(√3·x)` allineata a `SistemaSJavalinBetterApplMsgs`

  **Progetto firefly** | [Sorgenti](firefly/src)
  * [fireflyModel0.qaktt](firefly/src/fireflyModel0.qaktt) — prima formalizzazione analitica (versione del docente, con `griddisplaymock`)
  * [firefly1.qak](firefly/src/firefly1.qak) — sistema con 1 lucciola (timer interno)
  * [firefly3.qak](firefly/src/firefly3.qak) — sistema con 3 lucciole sincronizzate
  * [firefly100.qak](firefly/src/firefly100.qak) — sistema con 100 lucciole create dinamicamente
  * Altri modelli del docente: [firefly1external](firefly/src/firefly1external.qaktt), [firefly1mqtt](firefly/src/firefly1mqtt.qaktt), [firefly3.qaktt](firefly/src/firefly3.qaktt), [firefly5.qaktt](firefly/src/firefly5.qaktt), [fireflyMany.qaktt](firefly/src/fireflyMany.qaktt), [fireflyPicow.qaktt](firefly/src/fireflyPicow.qaktt)

  **Confronto Javalin vs QAK** | 📄 [Lab2_Comparacion_Javalin_QAK.html](userDocs/Lab2_Comparacion_Javalin_QAK.html) | 📄 [Main.html](qakdemo26/userDocs/Main.html)
