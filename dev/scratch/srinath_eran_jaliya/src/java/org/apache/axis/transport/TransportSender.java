/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.transport;

import java.io.OutputStream;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.context.MessageContext;
import org.apache.axis.handlers.AbstractHandler;
import org.apache.axis.message.OMMessage;
import org.apache.axis.registry.Parameter;

/**
 * @author Srinath Perera (hemapani@opensource.lk)
 */
public class TransportSender extends AbstractHandler implements Handler {
    private OutputStream out;
    public TransportSender(OutputStream out){
        this.out = out;
    }
    public void invoke(MessageContext msgContext) throws AxisFault {
        OMMessage message = msgContext.getOutMessage();
        message.serialize(out);
    }
}