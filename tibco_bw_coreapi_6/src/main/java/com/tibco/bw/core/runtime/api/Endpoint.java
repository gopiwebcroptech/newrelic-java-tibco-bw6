package com.tibco.bw.core.runtime.api;

import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class Endpoint {

	@Trace
	public <N> void invoke(MessageContext msgCtx, N[] paramArrayOfN) {
		
		Logger logger = NewRelic.getAgent().getLogger();
		RequestContext requestCtx = msgCtx.getRequestContext();
		String endpointName = "Unknown";
		try {
			Method method = getClass().getDeclaredMethod("getEndpointName", new Class[] {});
			endpointName = (String)method.invoke(this, new Object[] {});
		} catch (Exception e) {
			logger.log(Level.FINER,e, "Exception thrown trying to get endpoint name", new Object[] {});
		} 
		QName opName;
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		if (requestCtx != null) {
			opName = requestCtx.getOperationName();
			String contextID = requestCtx.getContextID();
			if (contextID != null) {
				NewRelic.addCustomParameter("Context ID",contextID);
			}
			traced.setMetricName(new String[] {"Custom/Endpoint",opName.getLocalPart()});
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Endpoint", new String[] {"Endpoint",endpointName,opName.getLocalPart()});
		}
		
		Weaver.callOriginal();
				
	}

	
}
