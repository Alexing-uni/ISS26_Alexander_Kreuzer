package main.java.protoactor;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;
import main.java.protoactor26.AbstractProtoactor26;
import main.java.protoactor26.ProtoActorContext26;

public class MathActor extends AbstractProtoactor26 {

    // 1. El constructor correcto que pide el profesor
    public MathActor(String name, ProtoActorContext26 ctx) {
        super(name, ctx);
    }

    // 2. Gestionar Peticiones (Request) -> DEVUELVE el mensaje de respuesta
    @Override
    protected IApplMessage elabRequest(IApplMessage req) {
        CommUtils.outblue(name + " | elabRequest: " + req);
        
        if (req.msgId().equals("doEval")) {
            try {
                double x = Double.parseDouble(req.msgContent());
                double result = eval(x); // Hacemos el cálculo lento
                
                // Construimos la respuesta y la DEVOLVEMOS directamente
                return CommUtils.buildReply(name, req.msgId(), "" + result, req.msgSender());
            } catch (Exception e) {
                return CommUtils.buildReply(name, req.msgId(), "Error de formato", req.msgSender());
            }
        } else {
            return CommUtils.buildReply(name, req.msgId(), "requestUnknown", req.msgSender());
        }
    }

    // 3. Los demás métodos obligatorios (los dejamos vacíos pero deben estar)
    @Override
    protected void elabDispatch(IApplMessage m) {
        CommUtils.outblue(name + " | elabDispatch:" + m);
    }

    @Override
    protected void elabReply(IApplMessage r) {
        CommUtils.outblue(name + " | elabReply:" + r);
    }

    @Override
    protected void elabEvent(IApplMessage ev) {
        CommUtils.outcyan(name + " | elabEvent:" + ev);
    }

    // 4. La función de negocio (con el retardo del profesor)
    protected double eval(double x) {
        CommUtils.outblue(name + " | eval: " + x);
        if (x > 4.0) {
            CommUtils.outmagenta(name + " | Simulo ritardo per x=" + x);
            CommUtils.delay(8000);
        }
        return Math.sin(x) + Math.cos(Math.sqrt(3) * x);
    }

	@Override
	protected void proactiveJob() {
		// TODO Auto-generated method stub
		
	}
}