/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi firefly3/Firefly3.kt per whenEvent/onMsg, Robotsnartusage.kt per subscribe);
   da rigenerare/validare col plugin QAK nell'IDE.
   SENSORMONITOR (Sprint 2): traduce il dato grezzo distance(D) del sensor dell'IOPort
   in notifiche LOGICHE al cargoservice (dispatch point-to-point):
     D < DFREE/2 per ~3s          -> containerInPlace(ioport)
     D > DFREE   per >= 3s        -> outofservice(on)
     dopo on, D <= DFREE per ~3s  -> outofservice(off)
   DFREE = 60 cm: PARAMETRO di calibrazione (da tarare sul dispositivo reale). */
package it.unibo.sensormonitor

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

class Sensormonitor ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		 val DFREE   = 60
		 var nLow    = 0      // campioni consecutivi D < DFREE/2  (presenza)
		 var nHigh   = 0      // campioni consecutivi D > DFREE    (guasto)
		 var nOk     = 0      // campioni consecutivi D <= DFREE   (rientro)
		 var oosOn   = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("$name | monitor del sensor attivo (DFREE=60)")
						subscribe(  "cargoservice26in_out" ) //mqtt.subscribe(this,topic)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="monitoring", cond=doswitch() )
				}
				state("monitoring") { //this:State
					action { //it:State
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t10",targetState="evalDistance",cond=whenEvent("sonaralarm"))
				}
				state("evalDistance") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("distance(D)"), Term.createTerm("distance(D)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val D = payloadArg(0).toInt()
								 if (D < DFREE/2) nLow++  else nLow  = 0
								 if (D > DFREE)   nHigh++ else nHigh = 0
								 if (D <= DFREE)  nOk++   else nOk   = 0
								if(  nLow == 3  ){
									CommUtils.outcyan("$name | presenza container (D<DFREE/2 per ~3s) -> containerInPlace")
									forward("containerInPlace", "containerInPlace(ioport)" ,"cargoservice" )
								}
								if(  nHigh == 3 && !oosOn  ){
									 oosOn = true
									CommUtils.outred("$name | D>DFREE per ~3s -> outofservice(on)")
									forward("outofservice", "outofservice(on)" ,"cargoservice" )
								}
								if(  nOk == 3 && oosOn  ){
									 oosOn = false
									CommUtils.outgreen("$name | sensor rientrato -> outofservice(off)")
									forward("outofservice", "outofservice(off)" ,"cargoservice" )
								}
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="monitoring", cond=doswitch() )
				}
			}
		}
}
