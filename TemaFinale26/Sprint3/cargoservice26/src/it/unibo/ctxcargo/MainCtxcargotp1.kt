/* Main per la prova TP1 AUTOMATIZZATA (carico nominale, demo finale 32.2).
   Carica cargoservice26tp1.pl: stesso sistema della demo interattiva ma con il
   tester che genera da solo pushbutton+sensor e verifica TP1 PASS/FAIL.
   Si lancia con:  gradlew runTp1   (vedi build.gradle). */
package it.unibo.ctxcargo
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "localhost", this, "cargoservice26tp1.pl", "sysRules.pl", "ctxcargo"
	)
}
