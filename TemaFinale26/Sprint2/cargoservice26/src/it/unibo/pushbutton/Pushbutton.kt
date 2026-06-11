/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/Callermock.kt per request + whenReply);
   da rigenerare/validare col plugin QAK nell'IDE.
   PUSHBUTTON (Sprint 2): SIMULA il pushbutton dell'IOPort con 3 richieste:
     t=1s  -> reserved(slot1)  (ciclo 1)
     t=75s -> retrylater       (sistema Out of service)
     t=91s -> reserved(slot2)  (rientrato in servizio; ciclo 2) */
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
						CommUtils.outgreen("$name | premuto (1) -> loadrequest")
						request("loadrequest", "loadrequest(pushbutton)" ,"cargoservice" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t07",targetState="showAnswer1",cond=whenReply("answer"))
				}
				state("showAnswer1") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(RESP)"), Term.createTerm("answer(RESP)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outgreen("$name | risposta (1): " + payloadArg(0))
						}
						delay(73000)
						CommUtils.outgreen("$name | premuto (2) -> loadrequest (sistema Out of service?)")
						request("loadrequest", "loadrequest(pushbutton)" ,"cargoservice" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t08",targetState="showAnswer2",cond=whenReply("answer"))
				}
				state("showAnswer2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(RESP)"), Term.createTerm("answer(RESP)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outgreen("$name | risposta (2): " + payloadArg(0))
						}
						delay(16000)
						CommUtils.outgreen("$name | premuto (3) -> loadrequest (rientrato in servizio?)")
						request("loadrequest", "loadrequest(pushbutton)" ,"cargoservice" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t09",targetState="showAnswer3",cond=whenReply("answer"))
				}
				state("showAnswer3") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(RESP)"), Term.createTerm("answer(RESP)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outgreen("$name | risposta (3): " + payloadArg(0))
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
