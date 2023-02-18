package my.practice.assignment;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * Program for outputting strings in XML or Json format to logger (depending on the received argument)
 * with the message: "Привіт <username>!"
 * Where username is the username from the config.properties file
 */

public class App {
    public static final String PATH_PROPERTIES = "config.properties";
    public static final String PATH_LOGGER = "logback.xml";
    private static final Logger logger = getLoggerAndSetConfigPath();

    public static void main(String[] args) {
        String arg = args.length < 1 ? "" : args[0];
        String message = getMessage(arg, getUsername());
        logger.trace("Message output");
        logger.error(message);
        logger.info("End program");
    }

    /**
     * Reads a property with the key: username from a file with properties
     *
     * @return Property with the key: username
     */
    private static String getUsername() {
        logger.info("Get username property");
        MyProperties properties = new MyProperties();
        try {
            properties.readPropertyFile(PATH_PROPERTIES);
        } catch (IOException e) {
            logger.error("Properties file not found : {}", e.getMessage(), e);
        }
        String username = properties.getProperty("username");
        return username == null ? "користувач" : username;
    }

    /**
     * Returns a string with a message depending on the received argument.
     * If the argument is Xml then the message is in Xml format
     * in other cases the message is in Json format
     *
     * @param arg      argument
     * @param username name from properties
     * @return Message : "Привіт <username>!
     */
    private static String getMessage(String arg, String username) {
        String format = arg.toLowerCase(Locale.ROOT).equals("xml")?"xml":"Json";
        logger.warn("Create a string in {} format with a message",format);
        ObjectMapper mapper = format.equals("xml")?new XmlMapper():new ObjectMapper();
        String message = "";
        try {
            message = mapper.writeValueAsString(new Message(username));
        } catch (JsonProcessingException e) {
            logger.error("The string could not be created: {}", e.getMessage(), e);
        }
        return message;
    }

    /**
     * Sets path to logback.configurationFile and gets logger from LoggerFactory
     * @return an object of type Logger
     */
    private static Logger getLoggerAndSetConfigPath() {
        System.setProperty("logback.configurationFile", PATH_LOGGER);
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.debug("Get Logger and set the path to the logback configuration file");
        return logger;
    }

    /**
     * The object to serialize that creates a message "Привіт <name user>!"
     */
    @JsonRootName("")
    private static class Message {
        String message;

        Message(String name) {
            message = String.format("Привіт %s!", name);
        }

        public String getMessage() {
            return message;
        }
    }
}
