package org.apache.axis.description;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.axis.context.MessageContext;
import org.apache.axis.context.OperationContext;
import org.apache.axis.context.OperationContextFactory;
import org.apache.axis.context.ServiceContext;
import org.apache.axis.engine.AxisError;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.MessageReceiver;
import org.apache.wsdl.WSDLConstants;
import org.apache.wsdl.WSDLOperation;
import org.apache.wsdl.impl.WSDLOperationImpl;

/**
 * @author chathura@opensource.lk
 *  
 */
public class OperationDescription extends WSDLOperationImpl implements
		ParameterInclude, WSDLOperation, DescriptionConstants, PhasesInclude,
		WSDLConstants {

	private MessageReceiver messageReceiver;
    private ArrayList remainingPhasesInInFlow;
    private ArrayList phasesInOutFlow;
    private ArrayList phasesInFaultInFlow;
    private ArrayList phasesInFaultOutFlow;
    
	private int mep = MEP_CONSTANT_INVALID;

	public OperationDescription() {
		this.setMessageExchangePattern(MEP_URI_IN_OUT);
		this.setComponentProperty(PARAMETER_KEY, new ParameterIncludeImpl());
		this.setComponentProperty(MODULEREF_KEY, new ArrayList());
		this.setComponentProperty(PHASES_KEY, new PhasesIncludeImpl());
	}

	public OperationDescription(QName name) {
		this();
		this.setName(name);
	}

	public void addModule(QName moduleref) {
		if (moduleref == null) {
			return;
		}
		Collection collectionModule = (Collection) this
				.getComponentProperty(MODULEREF_KEY);
		collectionModule.add(moduleref);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis.description.ServiceDescription#getModules()
	 */

	/**
	 * Method getModules
	 * 
	 * @return
	 */
	public Collection getModules() {
		return (Collection) this.getComponentProperty(MODULEREF_KEY);
	}

	/**
	 * Method addParameter
	 * 
	 * @param param
	 *            Parameter that will be added
	 */
	public void addParameter(Parameter param) {
		if (param == null) {
			return;
		}
		ParameterIncludeImpl paramInclude = (ParameterIncludeImpl) this
				.getComponentProperty(PARAMETER_KEY);
		paramInclude.addParameter(param);
	}

	/**
	 * Method getParameter
	 * 
	 * @param name
	 *            Name of the parameter
	 * @return
	 */
	public Parameter getParameter(String name) {
		ParameterIncludeImpl paramInclude = (ParameterIncludeImpl) this
				.getComponentProperty(PARAMETER_KEY);
		return (Parameter) paramInclude.getParameter(name);
	}

	/**
	 * This method is responsible for finding a MEPContext for an incomming
	 * messages. An incomming message can be of two states.
	 * 
	 * 1)This is a new incomming message of a given MEP. 2)This message is a
	 * part of an MEP which has already begun.
	 * 
	 * The method is special cased for the two MEPs
	 * 
	 * #IN_ONLY #IN_OUT
	 * 
	 * for two reasons. First reason is the wide usage and the second being that
	 * the need for the MEPContext to be saved for further incomming messages.
	 * 
	 * In the event that MEP of this operation is different from the two MEPs
	 * deafulted above the decession of creating a new or this message relates
	 * to a MEP which already in business is decided by looking at the WSA
	 * Relates TO of the incomming message.
	 * 
	 * @param msgContext
	 * @return
	 */
	public OperationContext findOperationContext(MessageContext msgContext,
			ServiceContext serviceContext, boolean serverside) throws AxisFault {
		OperationContext operationContext = null;

		if (null == msgContext.getRelatesTo()) {
			//Its a new incomming message so get the factory to create a new
			// one
			operationContext = OperationContextFactory.createMEPContext(
					getAxisSpecifMEPConstant(), serverside, this,
					serviceContext);

		} else {
			// So this message is part of an ongoing MEP
			//			operationContext =
			msgContext.getSystemContext().getOperationContext(
					msgContext.getRelatesTo().getValue());

			if (null == operationContext) {
				throw new AxisFault(
						"Cannot relate the message in the operation :"
								+ this.getName()
								+ " :Unrelated RelatesTO value "
								+ msgContext.getRelatesTo().getValue());
			}

		}

		msgContext.getSystemContext().registerOperationContext(
				msgContext.getMessageID(), operationContext);
		operationContext.addMessageContext(msgContext);
		msgContext.setOperationContext(operationContext);
		if (operationContext.isComplete()) {
			operationContext.cleanup();
		}

		return operationContext;

	}

	public MessageReceiver getMessageReciever() {
		return messageReceiver;
	}

	public void setMessageReciever(MessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	public void setPhases(ArrayList phases, int flow) throws AxisFault {
		if (phases == null) {
			return;
		}
		PhasesIncludeImpl phaseInclude = (PhasesIncludeImpl) this
				.getComponentProperty(PHASES_KEY);
		phaseInclude.setPhases(phases, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis.description.PhasesInclude#getPhases(int)
	 */

	/**
	 * Method getPhases
	 * 
	 * @param flow
	 * @return
	 * @throws AxisFault
	 */
	public ArrayList getPhases(int flow) throws AxisFault {
		PhasesIncludeImpl phaseInclude = (PhasesIncludeImpl) this
				.getComponentProperty(PHASES_KEY);
		return (ArrayList) phaseInclude.getPhases(flow);
	}

	/**
	 * This method will simply map the String URI of the Message exchange
	 * pattern to a integer. Further in the first lookup it will cash the looked
	 * up value so that the subsequent method calls will be extremely efficient.
	 * 
	 * @return
	 */
	public int getAxisSpecifMEPConstant() {
		if (this.mep != MEP_CONSTANT_INVALID) {
			return this.mep;
		}
		
		int temp = MEP_CONSTANT_INVALID;

		if (MEP_URI_IN_OUT.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_IN_OUT;
		} else if (MEP_URI_IN_ONLY.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_IN_ONLY;
		} else if (MEP_URI_IN_OPTIONAL_OUT.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_IN_OPTIONAL_OUT;
		} else if (MEP_URI_OUT_IN.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_OUT_IN;
		} else if (MEP_URI_OUT_ONLY.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_OUT_ONLY;
		} else if (MEP_URI_OUT_OPTIONAL_IN.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_OUT_OPTIONAL_IN;
		} else if (MEP_URI_ROBUST_IN_ONLY.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_ROBUST_IN_ONLY;
		} else if (MEP_URI_ROBUST_OUT_ONLY.equals(getMessageExchangePattern())) {
			temp = MEP_CONSTANT_ROBUST_OUT_ONLY;
		}
		
		if(temp == MEP_CONSTANT_INVALID){
			throw new AxisError("Could not Map the MEP URI to a axis MEP constant value");
		}
		this.mep = temp;
		return this.mep;

	}
    
    
    
    /**
     * @return
     */
    public ArrayList getPhasesInFaultInFlow() {
        return phasesInFaultInFlow;
    }

    /**
     * @return
     */
    public ArrayList getPhasesInFaultOutFlow() {
        return phasesInFaultOutFlow;
    }

    /**
     * @return
     */
    public ArrayList getPhasesInOutFlow() {
        return phasesInOutFlow;
    }

    /**
     * @return
     */
    public ArrayList getRemainingPhasesInInFlow() {
        return remainingPhasesInInFlow;
    }

    /**
     * @param list
     */
    public void setPhasesInFaultInFlow(ArrayList list) {
        phasesInFaultInFlow = list;
    }

    /**
     * @param list
     */
    public void setPhasesInFaultOutFlow(ArrayList list) {
        phasesInFaultOutFlow = list;
    }

    /**
     * @param list
     */
    public void setPhasesInOutFlow(ArrayList list) {
        phasesInOutFlow = list;
    }

    /**
     * @param list
     */
    public void setRemainingPhasesInInFlow(ArrayList list) {
        remainingPhasesInInFlow = list;
    }

}
