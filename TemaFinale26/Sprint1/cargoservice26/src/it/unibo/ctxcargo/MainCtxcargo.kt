/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/src/it/unibo/ctxsistemas/MainCtxsistemas.kt);
   da rigenerare/validare col plugin QAK nell'IDE. */
package it.unibo.ctxcargo
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	//System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
	QakContext.createContexts(
	        "localhost", this, "cargoservice26.pl", "sysRules.pl", "ctxcargo"
	)
	//JAN Facade
	//JAN24 Display
}
