package com.ricardosantos.localpingchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.util.Logger;

/**
 * 
 * @author Ricardo
 *
 */
public class PingAgent extends Agent
{
	private final String agentName;
	private static final String GUID 	= "com.ricardosantos.localpingchecker.PingAgent";
	private static final String version 	= "0.0.1";

	private final boolean reportMetricsToServers;
	private final String ipToPing;
	private final byte pingsPerExecution;

	private static final Logger logger = Logger.getLogger(PingAgent.class);
	
	public PingAgent(String agentName, boolean reportMetricsToServers, String ipToPing, byte pingsPerExecution)
	{
		super(GUID, version);
		this.agentName = agentName;
		this.reportMetricsToServers = reportMetricsToServers;
		this.ipToPing = ipToPing;
		this.pingsPerExecution = pingsPerExecution;
	}


	@Override
	public void pollCycle()
	{
		try
		{
			Process p = Runtime.getRuntime().exec("ping -c" + pingsPerExecution + " " + ipToPing);
			
			int goodPings = 0, unreachablePings = 0;
			double respTime = 0, avgRespTime = 0, maxRespTime = 0, minRespTime = 999999999;
			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			String line;
			while ((line = stdInput.readLine()) != null)
			{
				if(line.contains("icmp_seq"))
				{
					logger.debug("Going to parse the line: " + line);
					if (line.startsWith("64 bytes"))
					{
						goodPings++;
						respTime = Double.parseDouble(line.substring(line.indexOf("time=")+5, line.length()-3));
						avgRespTime += respTime;
						if (minRespTime>respTime) minRespTime = respTime;
						if (maxRespTime<respTime) maxRespTime = respTime;
					}
					else if (line.startsWith("Request timeout"))
					{
						unreachablePings++;
					}
					else
					{
						logger.error("Unexpected line: " + line);
					}
				}
				else
				{
					logger.debug("Discarded line: " + line);
				}
	        }
			avgRespTime /= goodPings;
			while ((line = stdError.readLine()) != null)
			{
				logger.error(line);
	        }
			logger.debug("Avg: " + avgRespTime + ", Min: " + minRespTime + ", Max: " + maxRespTime + ", Good pings: " + goodPings + ", Bad pings: " + unreachablePings);
			reportMetric("LocalPingCheck/RespTimeAverage", "double", avgRespTime);
			reportMetric("LocalPingCheck/RespTimeMinimum", "double", minRespTime);
			reportMetric("LocalPingCheck/RespTimeMaximum", "double", maxRespTime);
			reportMetric("LocalPingCheck/PingsGood", "count", goodPings);
			reportMetric("LocalPingCheck/PingsUnreachable", "count", unreachablePings);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String getAgentName()
	{
		return agentName;
	}


	@Override
	public void reportMetric(String metricName, String units, Number value)
	{
		if (reportMetricsToServers)
			super.reportMetric(metricName, units, value);
	}


	@Override
	public void reportMetric(String metricName, String units, int count, Number value, Number minValue, Number maxValue,
			Number sumOfSquares)
	{
		if (reportMetricsToServers)
			super.reportMetric(metricName, units, count, value, minValue, maxValue, sumOfSquares);
	}

}
