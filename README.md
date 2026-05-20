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
  * [ConwayLife Sprint 1 (Console)](ConwayLife/Sprint1/conway26Java) | 📄 [conway26Java_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/ConwayLife/Sprint1/conway26Java/userDocs/conway26Java_v0.html)
  * [ConwayLife Sprint 2 (Swing GUI)](conway26JavaSwing) | 📄 [conway26JavaSwing_v0.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26JavaSwing/userDocs/conway26JavaSwing_v0.html)
  * [ConwayLife Sprint 3 (Web GUI)](conway26GuiHtml) | 📄 [Sprint3_ConwayWeb.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26GuiHtml/userDocs/Sprint3_ConwayWeb.html)
  * [ConwayLife Sprint 3 (Protoattori)](conway26Protoactors) | 📄 [ConwayProtoactors.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/conway26Protoactors/userDocs/ConwayProtoactors.html)

  **SistemaS: Dai Monoliti ai Microservizi** | [Codice Sorgente](AService/Sprint1/SistemaSJavalin) | 📄 [Sprint3_Protoattori.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/AService/Sprint1/SistemaSJavalin/UserDocs/Sprint3_Protoattori.html)
  * **Fase 1 (Monolite & Docker):** Realizzazione di un servizio monolitico in Java per il calcolo di un'espressione matematica (`SistemaSJavalinBetterApplMsgs`) con deployment del servizio in Docker usando il framework Javalin.
  * **Fase 2 (Refactoring ad Attori):** Transizione dal bottom-up al top-down. Refactoring del sistema (`SistemaSProtoactor`) usando l'infrastruttura a Protoattori (`MathActor`) per delegare il lavoro pesante in modo asincrono, evitando il blocco del server web.

# Parte B: Il linguaggio QAK

  * **Progetto qakdemo26** — Esempi e demo del linguaggio QAK | [Codice Sorgente](qakdemo26/src) | 📄 [qakdemo26.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/qakdemo26/userDocs/qakdemo26.html)
  * **Progetto sistemasqak** — SistemaS modellato in QAK | [Codice Sorgente](sistemasqak/src) | 📄 [sistemasqak.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/sistemasqak/userDocs/sistemasqak.html)
  * **Progetto firefly** — Sincronizzazione di lucciole modellata in QAK | [Codice Sorgente](firefly/src) | 📄 [firefly.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/firefly/userDocs/firefly.html)
    * [fireflySynchOrchestr.qaktt](firefly/src/fireflySynchOrchestr.qaktt) — sincronizzazione orchestrata (lezione 29/04)
    * [fireflySynchCoreog.qaktt](firefly/src/fireflySynchCoreog.qaktt) — sincronizzazione coreografata (lezione 29/04)
  * **Progetto griddisplay** — Comportamento orchestrato e visualizzazione su griglia | [Codice Sorgente](griddisplay/src) | 📄 [griddisplay.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/griddisplay/userDocs/griddisplay.html)

  **Analisi comparativa Javalin vs QAK** | 📄 [Lab2_Comparacion_Javalin_QAK.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/userDocs/Lab2_Comparacion_Javalin_QAK.html) | 📄 [Main.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/qakdemo26/userDocs/Main.html)

# Parte C: Robotica e sistemi cyber-fisici (settimane 12-14)

  * **Progetto it.unibo.virtualRobot2026** — DDR formalizzato + ambiente WebGL | [Codice Sorgente](it.unibo.virtualRobot2026) | 📄 [virtualRobot2026.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/it.unibo.virtualRobot2026/userDocs/virtualRobot2026.html)
  * **Progetto vrUsage26** — Esempi d'uso del VirtualRobot26 (BoundaryWalk, Step, TuneSteptime) | [Codice Sorgente](vrUsage26/src) | 📄 [vrUsage26.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/vrUsage26/userDocs/vrUsage26.html)
  * **Progetto Picow** — Raspberry Pi PICOW: sonar HC-SR04, LED firefly, WiFi/MQTT | [Codice Sorgente](Picow) | 📄 [Picow.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/Picow/userDocs/Picow.html)
  * **Progetto boundaryworker** — DDRBoundary autonomo con mappa (base TemaFinale25) | [Codice Sorgente](boundaryworker/src) | 📄 [boundaryworker.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/boundaryworker/userDocs/boundaryworker.html) | 📄 [AnalisiDDRBoundary.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/boundaryworker/userDocs/AnalisiDDRBoundary.html)
  * **Progetto robotservice26** — Il robot come microservizio (move/cmd/step + eventi) | [Codice Sorgente](robotservice26/src) | 📄 [robotservice26.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/robotservice26/userDocs/robotservice26.html)

# Parte D: ConwayLife evolutivo

  * **ConwayLife Sprint 3 GUI Alone** — ConwayLife con GUI HTML standalone (WS + MQTT, multi-client, Docker) | [Codice Sorgente](ConwayLife/Sprint3/conway26GuiAlone) | 📄 [conway26GuiAlone.html](https://htmlpreview.github.io/?https://github.com/Alexing-uni/ISS26_Alexander_Kreuzer/blob/main/ConwayLife/Sprint3/conway26GuiAlone/userDocs/conway26GuiAlone.html)

# Materiali di supporto

  * [it.unibo.kotlinIntro](it.unibo.kotlinIntro) — introduzione a Kotlin
  * [SistemaSProtoactor](SistemaSProtoactor) — versione standalone del Sistema S a Protoattori
  * [mqttdemo](mqttdemo) — demo MQTT
  * [qakdemoresource](qakdemoresource) — esempi di Resource pattern in QAK
