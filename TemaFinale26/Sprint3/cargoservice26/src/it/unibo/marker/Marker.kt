/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/Sistemas.kt per whenDispatch + onMsg);
   da rigenerare/validare col plugin QAK nell'IDE.
   MARKER (Sprint 3): in slot5, etichetta il container con un barcode e
   SEGNALA il completamento (requisito: "signals when this marking activity
   is completed"). Simulato: tempo di marcatura 1s. */
package it.unibo.marker

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

class Marker ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		 var nMarked = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("$name | marker attivo (slot5)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}
				state("idle") { //this:State
					action { //it:State
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t0m",targetState="doMarking",cond=whenDispatch("domark"))
				}
				state("doMarking") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("domark(SLOT)"), Term.createTerm("domark(SLOT)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 nMarked = nMarked + 1
								 val Barcode = "bc" + nMarked    //senza '-': in Prolog bc-1 = -(bc,1)
								CommUtils.outcyan("$name | marcatura in corso, barcode=$Barcode")
								delay(1000)
								forward("markingDone", "markingDone($Barcode)" ,"cargoservice" )
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}
			}
		}
}
