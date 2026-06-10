/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi qakdemo26/sender e firefly3/Firefly3.kt per emit);
   da rigenerare/validare col plugin QAK nell'IDE.
   SIMULA il sensor dell'IOPort: dopo 5s emette containerInPlace (cammino felice).
   Per provare il TIMEOUT: portare il delay a > 30000 (o non emettere). */
package it.unibo.iosensor

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.kactor.sysUtil.createActor   //Sept2023
//Sept2024
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.json.simple.parser.JSONParser
import org.json.simple.JSONObject


//User imports JAN2024

class Iosensor ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outcyan("$name | sensor IOPort attivo (simulato)")
						delay(5000)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="detect", cond=doswitch() )
				}
				state("detect") { //this:State
					action { //it:State
						CommUtils.outcyan("$name | container rilevato -> emit containerInPlace")
						emit("containerInPlace", "containerInPlace(ioport)" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
				}
			}
		}
}
