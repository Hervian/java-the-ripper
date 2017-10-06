package com.github.hervian.javatheripper;


public class JvmUtils {

    enum OS {MAC, WINDOWS, LINUX}
    static OS operatingSystem;
    
    static {
        String jvmProperty = getJvmProperty("os.name").toLowerCase();
        if (jvmProperty.contains("mac")) {
            operatingSystem = OS.MAC;
        } else if (jvmProperty.contains("nix") || jvmProperty.contains("nux") || jvmProperty.contains("aix") ) {
            operatingSystem = OS.LINUX;
        } else if (jvmProperty.contains("win")) {
            operatingSystem = OS.WINDOWS;
        }
    }
    
    public OS getOperatingSystem(){
        return operatingSystem;
    }
    
    /**
     * Get a Jvm property / environment variable
     * @param prop the property to get
     * @return the property value
     */
    private static String getJvmProperty(String prop) {
        return (System.getProperty(prop, System.getenv(prop)));
    }
    
}
