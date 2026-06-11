/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi firefly3/Firefly3.kt per emit, firefly100 per kotlin-loop in [# #]);
   da rigenerare/validare col plugin QAK nell'IDE.
   IOSENSOR (Sprint 2): SIMULA il sensor (sonar) dell'IOPort in stile PicoW,
   emettendo il dato grezzo sonaralarm:distance(D) a ~1Hz secondo lo scenario:
     t=5s  presenza (D=20 x4) -> ciclo di carico 1
     t=65s guasto   (D=90 x4) -> Out of service
     t=85s rientro  (D=45 x4) -> serviceworking
     t=93s presenza (D=20 x4) -> ciclo di carico 2
   Il PicoW REALE pubblica msg(sonaralarm,event,picow,none,distance(D),0) sul
   topic del sistema: sostituisce questo attore senza modifiche al resto. */
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
						CommUtils.outcyan("$name | sensor IOPort attivo (simulato, stile PicoW)")
						delay(5000)
						 for (i in 1..4) { emit("sonaralarm", "distance(20)"); delay(1000) }     //presenza 1
						CommUtils.outcyan("$name | (attesa: trasporto ciclo 1 in corso)")
						delay(51000)
						CommUtils.outred("$name | GUASTO simulato: D=90 > DFREE")
						 for (i in 1..4) { emit("sonaralarm", "distance(90)"); delay(1000) }     //guasto
						delay(12000)
						CommUtils.outcyan("$name | rientro: D=45")
						 for (i in 1..4) { emit("sonaralarm", "distance(45)"); delay(1000) }     //rientro
						delay(4000)
						 for (i in 1..4) { emit("sonaralarm", "distance(20)"); delay(1000) }     //presenza 2
						CommUtils.outcyan("$name | scenario completato")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
				}
			}
		}
}
