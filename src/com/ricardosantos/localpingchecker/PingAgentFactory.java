/**
 * 
 */
package com.ricardosantos.localpingchecker;

import java.util.Map;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

import sun.net.util.IPAddressUtil;

/**
 * Ping agent factory.
 * @author Ricardo
 */
public class PingAgentFactory extends AgentFactory
{
	//private static final Logger logger = Logger.getLogger(PingAgentFactory.class);

	/* (non-Javadoc)
	 * @see com.newrelic.metrics.publish.AgentFactory#createConfiguredAgent(java.util.Map)
	 */
	@Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException
	{
		String agentName = (String) properties.get("agentName");
		String reportMetricsToServers = (String) properties.get("reportMetricsToServers");
		String ipToPing = (String) properties.get("ipToPing");
		String pingsPerPollExecution = (String) properties.get("pingsPerPollExecution");
		if (agentName == null)
		{
            throw new ConfigurationException("'agentName' in plugin.json file cannot be null.");
		}
		if (reportMetricsToServers == null)
		{
			throw new ConfigurationException("'reportMetricsToServers' in plugin.json file cannot be null");
		}
		if (Boolean.valueOf(reportMetricsToServers) == null)
		{
			throw new ConfigurationException("'reportMetricsToServers' must have the value 'true' or 'false', not " + reportMetricsToServers);
		}
		if (ipToPing == null)
		{
            throw new ConfigurationException("'ipToPing' in plugin.json file cannot be null.");
		}
		if (!IPAddressUtil.isIPv4LiteralAddress(ipToPing))
		{
			throw new ConfigurationException("'ipToPing' in plugin.json file must be a valid IP, not: " + ipToPing);
		}
		if (pingsPerPollExecution == null)
		{
            throw new ConfigurationException("'pingsPerPollExecution' in plugin.json file cannot be null.");
		}
		if (Byte.valueOf(pingsPerPollExecution) == null)
		{
			throw new ConfigurationException("'pingsPerPollExecution' must be a valid number, not " + pingsPerPollExecution);
		}

        return new PingAgent(agentName, Boolean.parseBoolean(reportMetricsToServers), ipToPing, Byte.parseByte(pingsPerPollExecution));
    }
}
