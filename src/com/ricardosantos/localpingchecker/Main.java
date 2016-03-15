/**
 * 
 */
package com.ricardosantos.localpingchecker;

import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

/**
 * @author Ricardo
 *
 */
public class Main
{

	/**
	 * Starts up the agent.
	 * @param args Command line arguments (no used)
	 */
    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            runner.add(new PingAgentFactory());
            runner.setupAndRun(); // Never returns
        } catch (ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
