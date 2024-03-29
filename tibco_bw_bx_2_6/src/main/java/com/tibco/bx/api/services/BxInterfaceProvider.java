package com.tibco.bx.api.services;

import java.util.Set;

import javax.xml.namespace.QName;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class BxInterfaceProvider {

	public abstract Set<String> getOperationNames();
	public abstract long getServiceId();
	public abstract QName getPortTypeQName();
	public abstract BxOperation getOperation(String paramString);
	public abstract String getServiceName();

	public abstract String getFullServiceName();

	@Trace
	public <T> void invoke(T[] paramArrayOfT, BxReplyEndpointReference replyEndpointReference, BxInvocationInfo invocationInfo) {
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(invocationInfo.headers);
		Weaver.callOriginal();
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom/BXInterfaceProvider",getServiceName(),invocationInfo.getOperationName()});
	}
}
