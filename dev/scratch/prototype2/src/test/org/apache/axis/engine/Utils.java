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
package org.apache.axis.engine;

import javax.xml.namespace.QName;


import org.apache.axis.impl.engine.GlobalImpl;
import org.apache.axis.impl.engine.ModuleImpl;
import org.apache.axis.impl.engine.OperationImpl;
import org.apache.axis.impl.engine.ServiceImpl;
import org.apache.axis.impl.engine.TransportImpl;
import org.apache.axis.impl.providers.SimpleJavaProvider;
import org.apache.axis.impl.registry.ParameterImpl;
import org.apache.axis.impl.registry.EngineRegistryImpl;
import org.apache.axis.registry.EchoService;
import org.apache.axis.registry.EngineRegistry;
import org.apache.axis.registry.MockFlow;
import org.apache.axis.registry.Module;
import org.apache.axis.registry.Parameter;

/**
 * @author Srinath Perera (hemapani@opensource.lk)
 */
public class Utils {
    public static EngineRegistry createMockRegistry(QName serviceName,QName operationName,QName transportName) throws AxisFault{
        EngineRegistry engineRegistry = null;
        Global global = new GlobalImpl();
        global.setInFlow(new MockFlow("globel inflow",4));
        global.setOutFlow(new MockFlow("globel outflow",2));
        global.setFaultFlow(new MockFlow("globel faultflow",1));
        engineRegistry = new EngineRegistryImpl(global);
        
        Transport transport = new TransportImpl(transportName);
        transport.setInFlow(new MockFlow("transport inflow",4));
        transport.setOutFlow(new MockFlow("transport outflow",2));
        transport.setFaultFlow(new MockFlow("transport faultflow",1));
        engineRegistry.addTransport(transport);
        
        Service service = new ServiceImpl(serviceName);
        service.setInFlow(new MockFlow("service inflow",4));
        service.setOutFlow(new MockFlow("service outflow",5));
        service.setFaultFlow(new MockFlow("service faultflow",1));
        service.setClassLoader(Thread.currentThread().getContextClassLoader());
        
        Parameter classParam = new ParameterImpl("className",EchoService.class.getName());
        service.addParameter(classParam);
         
        service.setProvider(new SimpleJavaProvider());
        
        Module m1 = new ModuleImpl(new QName("","A Mdoule 1"));
        m1.setInFlow(new MockFlow("service module inflow",4));
        m1.setFaultFlow(new MockFlow("service module faultflow",1));
        service.addModule(m1);
        
        Operation operation = new OperationImpl(operationName,service);
        operation.setInFlow(new MockFlow("inflow",4));
        
        service.addOperation(operation);
        engineRegistry.addService(service);
        //create Execution Chains
        ExecutionChain inchain = new ExecutionChain();
        inchain.addPhase(new Phase(Constants.PHASE_TRANSPORT));
        inchain.addPhase(new Phase(Constants.PHASE_GLOBAL));
        inchain.addPhase(new Phase(Constants.PHASE_SERVICE));
        EngineUtils.addHandlers(transport.getInFlow(),inchain,Constants.PHASE_TRANSPORT);
        EngineUtils.addHandlers(global.getInFlow(),inchain,Constants.PHASE_GLOBAL);
        EngineUtils.addHandlers(service.getInFlow(),inchain,Constants.PHASE_SERVICE);
        service.setInputExecutionChain(inchain);
        
        ExecutionChain outchain = new ExecutionChain();
        outchain.addPhase(new Phase(Constants.PHASE_SERVICE));
        outchain.addPhase(new Phase(Constants.PHASE_GLOBAL));
        outchain.addPhase(new Phase(Constants.PHASE_TRANSPORT));
        EngineUtils.addHandlers(service.getOutFlow(),outchain,Constants.PHASE_SERVICE);
        EngineUtils.addHandlers(global.getOutFlow(),outchain,Constants.PHASE_GLOBAL);
        EngineUtils.addHandlers(transport.getOutFlow(),outchain,Constants.PHASE_TRANSPORT);
        service.setOutExecutionChain(outchain);
        
        ExecutionChain faultchain = new ExecutionChain();
        
        faultchain.addPhase(new Phase(Constants.PHASE_SERVICE));
        faultchain.addPhase(new Phase(Constants.PHASE_GLOBAL));
        faultchain.addPhase(new Phase(Constants.PHASE_TRANSPORT));
        
        EngineUtils.addHandlers(service.getFaultFlow(),faultchain,Constants.PHASE_SERVICE);
        EngineUtils.addHandlers(global.getFaultFlow(),faultchain,Constants.PHASE_GLOBAL);
        EngineUtils.addHandlers(transport.getFaultFlow(),faultchain,Constants.PHASE_TRANSPORT);
        service.setFaultExecutionChain(outchain);
        return engineRegistry;
    }
}
