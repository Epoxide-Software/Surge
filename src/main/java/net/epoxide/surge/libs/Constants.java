package net.epoxide.surge.libs;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
    
    public static final String MOD_ID = "Surge";
    public static final String MOD_NAME = "Surge";
    public static final String VERSION_NUMBER = "0.0.0.0";
    public static final String CLIENT_PROXY_CLASS = "net.epoxide.surge.client.ProxyClient";
    public static final String SERVER_PROXY_CLASS = "net.epoxide.surge.common.ProxyCommon";
    public static final String DEPENDENCIES = "required-after:bookshelf@[1.2.3.368,);";
    public static final Random RANDOM = new Random();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
}
