/* Scritto seguendo il pattern del codice generato dal plugin QAK
   (vedi sistemasqak/Sistemas.kt, robotsmart26usage/Robotsnartusage.kt, firefly3/Firefly3.kt);
   da rigenerare/validare col plugin QAK nell'IDE a partire da src/cargoservice26.qak.
   SPRINT 1 = core + sensor dell'IOPort (containerInPlace) + timeout 30s -> disengage. */
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
		val display = devices.DisplaySim()
		val led     = devices.LedSim()
		 var Slot     = ""
		 val StepTime = 345
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("$name | starts")
						subscribe(  "cargoservice26in_out" ) //mqtt.subscribe(this,topic)
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
					 transition(edgeName="t00",targetState="evalRequest",cond=whenRequest("loadrequest"))
				}
				state("evalRequest") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loadrequest(CALLER)"), Term.createTerm("loadrequest(CALLER)"),
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Slot = ""
								if(  hold.ioportOccupied()  ){
									answer("loadrequest", "answer", "answer(retrylater)"   )
									 display.show("retrylater")
								}
								if(  !hold.ioportOccupied() && hold.isFull()  ){
									answer("loadrequest", "answer", "answer(reject)"   )
									 display.show("reject")
								}
								if(  !hold.ioportOccupied() && !hold.isFull()  ){
									 Slot = hold.reserveFreeSlot()
									answer("loadrequest", "answer", "answer(reserved($Slot))"   )
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
					transition(edgeName="t02",targetState="goToIOPort",cond=whenEvent("containerInPlace"))
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
						CommUtils.outblue("$name | containerInPlace ricevuto -> cargorobot verso IOPort")
						 val IX = hold.ioportX(); val IY = hold.ioportY()
						request("moverobot", "moverobot($IX,$IY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t03",targetState="carryToSlot5",cond=whenReply("moverobotdone"))
					transition(edgeName="t03f",targetState="robotFailure",cond=whenReply("moverobotfailed"))
				}
				state("carryToSlot5") { //this:State                       //IOPort -> slot5 (marcatura)
					action { //it:State
						 val SX = hold.slotX("slot5"); val SY = hold.slotY("slot5")
						request("moverobot", "moverobot($SX,$SY,$StepTime)" ,"robotsmart" )
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}
					 transition(edgeName="t04",targetState="marking",cond=whenReply("moverobotdone"))
					transition(edgeName="t04f",targetState="robotFailure",cond=whenReply("moverobotfailed"))
				}
				state("marking") { //this:State                            //marker simulato nel core
					action { //it:State
						CommUtils.outcyan("$name | marcatura in slot5 ...")
						delay(1000)
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
					transition(edgeName="t05f",targetState="robotFailure",cond=whenReply("moverobotfailed"))
				}
				state("goHome") { //this:State                             //ritorno a HOME
					action { //it:State
						 hold.confirmStored(Slot)
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
				//SPRINT1: il cargorobot fallisce PRIMA dello stoccaggio -> slot liberato
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
						 led.blink(false); display.show("holdstate")
						CommUtils.outgreen("$name | richiesta completata: container in " + Slot)
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
