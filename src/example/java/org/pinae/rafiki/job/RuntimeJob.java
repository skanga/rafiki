package org.pinae.rafiki.job;

import java.util.Properties;

public class RuntimeJob implements Job {
    public String getName() {
        return "Runtime";
    }

    public boolean execute() throws JobException {
        Properties props = System.getProperties(); // System properties
        System.out.println("JVM Specification Version: " + props.getProperty("java.vm.specification.version"));
        System.out.println("JVM Specification Vendor: " + props.getProperty("java.vm.specification.vendor"));
        System.out.println("JVM Specification Name: " + props.getProperty("java.vm.specification.name"));
        System.out.println("JVM Implementation Version: " + props.getProperty("java.vm.version"));
        System.out.println("JVM Implementation Vendor: " + props.getProperty("java.vm.vendor"));
        System.out.println("JVM Implementation Name: " + props.getProperty("java.vm.name"));

        return true;
    }
}
