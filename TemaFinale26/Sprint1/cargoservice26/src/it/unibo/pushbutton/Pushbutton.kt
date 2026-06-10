/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/Callermock.kt per request + whenReply);
   da rigenerare/validare col plugin QAK nell'IDE.
   SIMULA il pushbutton dell'IOPort: avvia la demo inviando una loadrequest. */
package it.unibo.pushbutton

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

class Pushbutton ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						delay(1000)
						CommUtils.outgreen("$name | premuto -> loadrequest")
						request("loadrequest", "loadrequest(pushbutton)" ,"cargoservice" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t07",targetState="showAnswer",cond=whenReply("answer"))
				}
				state("showAnswer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(RESP)"), Term.createTerm("answer(RESP)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outgreen("$name | risposta del cargoservice: " + payloadArg(0))
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
				}
			}
		}
}
