/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.axis.ime.event;

import org.apache.axis.MessageContext;
import org.apache.axis.ime.MessageExchangeCorrelator;
import org.apache.axis.ime.internal.MessageExchangeSendContext;

/**
 * The MessageSendEvent is used to indicate that a message has been dispatched.
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class MessageSendEvent
        extends MessageCorrelatedEvent {

    protected MessageExchangeSendContext sendContext;
    
    public MessageSendEvent(
            MessageExchangeCorrelator correlator,
            MessageExchangeSendContext sendContext,
            MessageContext context) {
        super(correlator, context);
        this.sendContext = sendContext;
    }
    
    public MessageExchangeSendContext getMessageExchangeSendContext()
    {
        return sendContext;
    }
}