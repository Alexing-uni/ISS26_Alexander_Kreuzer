/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi demoSendReceiveEmit.qak per emit, sistemasqak/Callermock.kt per request+whenReply);
   da rigenerare/validare col plugin QAK nell'IDE.
   TESTER (Sprint 3): TestPlan TP1 AUTOMATIZZATO (richiesto per la demo finale, 32.2).
   Esercita il sistema SENZA interazione umana, al posto della web-gui:
     - EMETTE il pushbutton  (EVENT loadrequest)
     - EMETTE il sensor      (EVENT sonaralarm:distance(20) per ~3s = container presente)
     - poi INTERROGA lo stato della stiva (getholdstate, pattern getrobotstate del docente)
       ogni 10s finche' uno slot risulta pieno: stampa TP1 PASS / TP1 FAIL (timeout ~2,5 min). */
package it.unibo.tester

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

class Tester ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		val io = devices.IoPortSim()    //simulatore headless dell'IOPort (canali separati, come la web-gui)
		 var NPoll = 0
		 var Done  = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("$name | TP1 (carico nominale automatizzato) START")
						delay(2000)
						CommUtils.outgreen("$name | premo il pushbutton -> loadrequest")
						io.loadrequest("tester")
						delay(2000)
						CommUtils.outgreen("$name | muovo il sensore: container presente (distance 20)")
						 for (i in 1..4) { io.distance(20); delay(1000) }
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="pollHold", cond=doswitch() )
				}
				state("pollHold") { //this:State                  //attende il trasporto interrogando lo stato
					action { //it:State
						 NPoll = NPoll + 1
						delay(10000)
						request("getholdstate", "getholdstate(tester)" ,"cargoservice" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t1t",targetState="tp1Check",cond=whenReply("holdinfo"))
				}
				state("tp1Check") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("holdinfo(STATE)"), Term.createTerm("holdinfo(STATE)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Done = payloadArg(0).contains("(pieno)")
								if(  Done  ){
									CommUtils.outgreen("$name | *** TP1 PASS *** stiva: ${payloadArg(0)}")
								}
								if(  !Done  ){
									CommUtils.outblue("$name | TP1 attendo ($NPoll): ${payloadArg(0)}")
								}
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="tp1End", cond=doswitchGuarded({ Done
					}) )
					transition( edgeName="goto",targetState="pollAgain", cond=doswitchGuarded({! ( Done
					) }) )
				}
				state("pollAgain") { //this:State
					action { //it:State
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="pollHold", cond=doswitchGuarded({ NPoll < 15
					}) )
					transition( edgeName="goto",targetState="tp1Fail", cond=doswitchGuarded({! ( NPoll < 15
					) }) )
				}
				state("tp1End") { //this:State
					action { //it:State
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
				}
				state("tp1Fail") { //this:State
					action { //it:State
						CommUtils.outred("$name | *** TP1 FAIL *** (timeout ~2,5 min)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
				}
			}
		}
}
