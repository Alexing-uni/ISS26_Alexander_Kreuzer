/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/Sistemas.kt, robotsmart26usage/Robotsnartusage.kt, firefly3/Firefly3.kt,
    qakdemo26/Receiver.kt per whenDispatch);
   da rigenerare/validare col plugin QAK nell'IDE a partire da src/cargoservice26.qak.
   SPRINT 3 = Sprint2 + marker come ATTORE (domark/markingDone(BARCODE)) + stato
   reale della stiva sul display (hold.stateDescription()). */
package it.unibo.cargoservice

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

class Cargoservice ( name: String, scope: CoroutineScope, isconfined: Boolean=false, isdynamic: Boolean=false ) :
          ActorBasicFsm( name, scope, confined=isconfined, dynamically=isdynamic ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		val hold    = domain.Hold()
		val display = devices.DisplayMqtt()
		@Suppress("UNUSED_VARIABLE")
		val webio   = devices.WebIoPort()     //SPRINT3: il costruttore SERVE e APRE la web-gui interattiva dell'IOPort (effetto collaterale voluto)
		val led     = devices.LedSim()
		 var Slot     = ""
		 var Retry    = 0     //ritenti del trasporto (robustezza)
		 var Oos      = false
		 var Barcode  = ""    //SPRINT3: barcode assegnato dal marker, memorizzato nello slot
		 val StepTime = 345
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("$name | starts")
						subscribe(  "cargoservice26in_out" ) //SPRINT3: percepisce loadrequest dalla web-gui (via MQTT)
						display.show("serviceworking")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
				state("available") { //this:State                          //Service working
					action { //it:State
						CommUtils.outyellow("$name | available (attende loadrequest)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t00",targetState="evalRequest",cond=whenEvent("loadrequest"))
					transition(edgeName="t00q",targetState="tellHoldState",cond=whenRequest("getholdstate"))
					transition(edgeName="t00b",targetState="checkOos",cond=whenDispatch("outofservice"))
					transition(edgeName="t00c",targetState="dropStale",cond=whenDispatch("containerInPlace"))
				}
				//SPRINT3: risponde con lo stato della stiva come termine (per tester/IOPort)
				state("tellHoldState") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getholdstate(ARG)"), Term.createTerm("getholdstate(ARG)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val HS = hold.stateTerm()
								answer("getholdstate", "holdinfo", "holdinfo($HS)"   )
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
				//SPRINT3: notifica rimasta in coda durante un trasporto: scartata se nessuna richiesta attiva
				state("dropStale") { //this:State
					action { //it:State
						CommUtils.outyellow("$name | containerInPlace senza richiesta attiva: scartato")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
				state("evalRequest") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loadrequest(CALLER)"), Term.createTerm("loadrequest(CALLER)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Slot = ""
								if(  hold.ioportOccupied()  ){
									 display.show("retrylater")     //SPRINT3: la risposta appare sul DISPLAY (web-gui), non un reply
								}
								if(  !hold.ioportOccupied() && hold.isFull()  ){
									 display.show("reject")
								}
								if(  !hold.ioportOccupied() && !hold.isFull()  ){
									 Slot = hold.reserveFreeSlot(); Retry = 0
									 display.show("reserved " + Slot); led.blink(true)     //engaged
								}
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="waitContainer", cond=doswitchGuarded({ Slot != ""
					}) )
					transition( edgeName="goto",targetState="available", cond=doswitchGuarded({! ( Slot != ""
					) }) )
				}
				//SPRINT2: OUT OF SERVICE (requisito: D > DFREE per >= 3s -> sospetto guasto del sonar)
				state("checkOos") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("outofservice(V)"), Term.createTerm("outofservice(V)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Oos = (payloadArg(0) == "on")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="outOfService", cond=doswitchGuarded({ Oos
					}) )
					transition( edgeName="goto",targetState="available", cond=doswitchGuarded({! ( Oos
					) }) )
				}
				state("outOfService") { //this:State
					action { //it:State
						 display.show("Out of service")
						CommUtils.outred("$name | OUT OF SERVICE (sospetto guasto del sonar)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t0o1",targetState="oosRetry",cond=whenEvent("loadrequest"))
					transition(edgeName="t0o2",targetState="checkOosExit",cond=whenDispatch("outofservice"))
				}
				state("oosRetry") { //this:State                           //requisito: retrylater se Out of service
					action { //it:State
						if( checkMsgContent( Term.createTerm("loadrequest(CALLER)"), Term.createTerm("loadrequest(CALLER)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 display.show("retrylater")     //SPRINT3: risposta sul DISPLAY (Out of service -> retrylater)
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="outOfService", cond=doswitch() )
				}
				state("checkOosExit") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("outofservice(V)"), Term.createTerm("outofservice(V)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Oos = (payloadArg(0) == "on")
								 if (!Oos) display.show("serviceworking")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="outOfService", cond=doswitchGuarded({ Oos
					}) )
					transition( edgeName="goto",targetState="available", cond=doswitchGuarded({! ( Oos
					) }) )
				}
				//SPRINT1: engaged -> attesa del container nell'area del sensor (max 30s)
				state("waitContainer") { //this:State
					action { //it:State
						CommUtils.outmagenta("$name | engaged: attendo il container nell'area del sensor (timeout 30s)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_waitContainer",
				 	 					  scope, context!!, "local_tout_"+name+"_waitContainer", 30000.toLong() )  //OCT2023
					}
					 transition(edgeName="t01",targetState="disengage",cond=whenTimeout("local_tout_"+name+"_waitContainer"))
					transition(edgeName="t02",targetState="goToIOPort",cond=whenDispatch("containerInPlace"))
				}
				//SPRINT1: timeout scaduto -> disengage (libera lo slot riservato, LED off)
				state("disengage") { //this:State
					action { //it:State
						 hold.freeSlot(Slot); led.blink(false); display.show("disengaged"); Slot = ""
						CommUtils.outred("$name | TIMEOUT: container non arrivato -> disengage, slot liberato")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
				state("goToIOPort") { //this:State                         //HOME -> IOPort (presa astratta)
					action { //it:State
						 val IX = hold.ioportX(); val IY = hold.ioportY()
						request("moverobot", "moverobot($IX,$IY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t03",targetState="carryToSlot5",cond=whenReply("moverobotdone"))
					transition(edgeName="t03f",targetState="retryIOPort",cond=whenReply("moverobotfailed"))
				}
				state("carryToSlot5") { //this:State                       //IOPort -> slot5 (marcatura)
					action { //it:State
						 val SX = hold.slotX("slot5"); val SY = hold.slotY("slot5")
						display.show("verso slot5 (marcatura)")   //SPRINT3: rende VISIBILE sul display il passaggio per l'area di marcatura
						request("moverobot", "moverobot($SX,$SY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t04",targetState="marking",cond=whenReply("moverobotdone"))
					transition(edgeName="t04f",targetState="retrySlot5",cond=whenReply("moverobotfailed"))
				}
				state("marking") { //this:State                            //SPRINT3: marcatura delegata al marker
					action { //it:State
						CommUtils.outcyan("$name | chiedo la marcatura in slot5 ...")
						forward("domark", "domark(slot5)" ,"marker" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t04m",targetState="markingCompleted",cond=whenDispatch("markingDone"))
				}
				state("markingCompleted") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("markingDone(BARCODE)"), Term.createTerm("markingDone(BARCODE)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Barcode = payloadArg(0)    //memorizza il barcode per lo stoccaggio
								display.show("slot5: barcode " + payloadArg(0))   //SPRINT3: barcode assegnato NELL'area di marcatura slot5
								CommUtils.outcyan("$name | marcatura completata, barcode=${payloadArg(0)}")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="carryToReserved", cond=doswitch() )
				}
				state("carryToReserved") { //this:State                    //slot5 -> slot riservato
					action { //it:State
						 val SX = hold.slotX(Slot); val SY = hold.slotY(Slot)
						request("moverobot", "moverobot($SX,$SY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t05",targetState="goHome",cond=whenReply("moverobotdone"))
					transition(edgeName="t05f",targetState="retryReserved",cond=whenReply("moverobotfailed"))
				}
				state("goHome") { //this:State                             //ritorno a HOME
					action { //it:State
						 hold.confirmStored(Slot, Barcode)
						 val HX = hold.homeX(); val HY = hold.homeY()
						request("moverobot", "moverobot($HX,$HY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t06",targetState="doneRequest",cond=whenReply("moverobotdone"))
					transition(edgeName="t06f",targetState="doneRequest",cond=whenReply("moverobotfailed"))  //container GIA' stoccato: HOME best-effort
				}
				//ROBUSTEZZA: un passo del DDR puo' fallire (es. frame della scena in ritardo):
				//si RIPROVA lo stesso target (robotsmart ripianifica dalla posizione corrente)
				state("retryIOPort") { //this:State
					action { //it:State
						 Retry = Retry + 1
						CommUtils.outyellow("$name | moverobotfailed: riprovo verso IOPort (tentativo $Retry)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="goToIOPort", cond=doswitchGuarded({ Retry <= 2
					}) )
					transition( edgeName="goto",targetState="robotFailure", cond=doswitchGuarded({! ( Retry <= 2
					) }) )
				}
				state("retrySlot5") { //this:State
					action { //it:State
						 Retry = Retry + 1
						CommUtils.outyellow("$name | moverobotfailed: riprovo verso slot5 (tentativo $Retry)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="carryToSlot5", cond=doswitchGuarded({ Retry <= 2
					}) )
					transition( edgeName="goto",targetState="robotFailure", cond=doswitchGuarded({! ( Retry <= 2
					) }) )
				}
				state("retryReserved") { //this:State
					action { //it:State
						 Retry = Retry + 1
						CommUtils.outyellow("$name | moverobotfailed: riprovo verso $Slot (tentativo $Retry)")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="carryToReserved", cond=doswitchGuarded({ Retry <= 2
					}) )
					transition( edgeName="goto",targetState="robotFailure", cond=doswitchGuarded({! ( Retry <= 2
					) }) )
				}
				//il cargorobot fallisce PRIMA dello stoccaggio -> slot liberato
				state("robotFailure") { //this:State
					action { //it:State
						 hold.freeSlot(Slot); led.blink(false); display.show("robotfailure"); Slot = ""
						CommUtils.outred("$name | moverobotfailed: slot liberato, servizio disponibile")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
				state("doneRequest") { //this:State
					action { //it:State
						 led.blink(false); display.show(hold.stateDescription())    //SPRINT3: stato REALE della stiva
						CommUtils.outgreen("$name | richiesta completata: container in $Slot")
						emit("loaddone", "loaddone($Slot)" )                        //SPRINT3: per display e tester
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition( edgeName="goto",targetState="available", cond=doswitch() )
				}
			}
		}
}
