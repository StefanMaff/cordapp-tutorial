package com.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.node.services.ServiceInfo;
import net.corda.node.services.transactions.ValidatingNotaryService;
import net.corda.nodeapi.User;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import org.bouncycastle.asn1.x500.X500Name;

import static java.util.Collections.*;
import static net.corda.testing.driver.Driver.driver;

/**
 * This file is exclusively for being able to run your nodes through an IDE (as opposed to running deployNodes)
 * Do not use in a production environment.
 * <p>
 * To debug your CorDapp:
 * <p>
 * 1. Firstly, run the "Run Example CorDapp" run configuration.
 * 2. Wait for all the nodes to start.
 * 3. Note the debug ports which should be output to the console for each node. They typically start at 5006, 5007,
 * 5008. The "Debug CorDapp" configuration runs with port 5007, which should be "NodeB". In any case, double check
 * the console output to be sure.
 * 4. Set your breakpoints in your CorDapp code.
 * 5. Run the "Debug CorDapp" remote debug run configuration.
 */
public class Main {
    public static void main(String[] args) {
        // No permissions required as we are not invoking flows.
        final User user = new User("user1", "test", emptySet());
        driver(new DriverParameters().setIsDebug(true), dsl -> {
                    dsl.startNode(new NodeParameters()
                            .setProvidedName(new X500Name("CN=Controller,O=R3,OU=corda,L=London,C=UK"))
                            .setAdvertisedServices(ImmutableSet.of(new ServiceInfo(ValidatingNotaryService.Companion.getType(), null))));

                    try {
                        NodeHandle nodeA = dsl.startNode(new NodeParameters()
                                .setProvidedName(new X500Name("CN=NodeA,O=NodeA,L=London,C=UK"))
                                .setRpcUsers(ImmutableList.of(user))).get();
                        NodeHandle nodeB = dsl.startNode(new NodeParameters()
                                .setProvidedName(new X500Name("CN=NodeB,O=NodeB,L=New York,C=US"))
                                .setRpcUsers(ImmutableList.of(user))).get();
                        NodeHandle nodeC = dsl.startNode(new NodeParameters()
                                .setProvidedName(new X500Name("CN=NodeC,O=NodeC,L=Paris,C=FR"))
                                .setRpcUsers(ImmutableList.of(user))).get();

                        dsl.startWebserver(nodeA);
                        dsl.startWebserver(nodeB);
                        dsl.startWebserver(nodeC);

                        dsl.waitForAllNodesToFinish();
                    } catch (Throwable e) {
                        System.err.println("Encountered exception in node startup: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }
        );
    }
}