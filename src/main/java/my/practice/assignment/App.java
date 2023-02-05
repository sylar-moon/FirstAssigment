package my.practice.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

public class App {
    public static final String PATH_PROPERTIES = "config.properties";
    public static final String PATH_LOGGER = "logback.xml";
    private static final Logger logger = getLoggerAndSetConfigPath();

    public static void main(String[] args) {
        String arg = args.length < 1 ? "" : args[0];
        String message = getMessage(arg, getUsername());
        logger.trace("Message output");
        logger.error(message);
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
        return arg.toLowerCase(Locale.ROOT).equals("xml") ? getXmlMessage(username) : getJsonMessage(username);
    }

    /**
     * Creates a JSON string with the given username
     *
     * @param username name from properties
     * @return Json format string
     */
    private static String getJsonMessage(String username) {
        logger.warn("Create a string in Json format with a message");
        ObjectMapper mapper = new ObjectMapper();
        String message = "";
        try {
            message = mapper.writeValueAsString(new Message(username));
        } catch (JsonProcessingException e) {
            logger.error("Json string could not be created: {}", e.getMessage(), e);
        }
        return message;
    }

    /**
     * Creates a Xml string with the given username
     *
     * @param username name from properties
     * @return Xml format string
     */
    private static String getXmlMessage(String username) {
        logger.warn("Create a string in Xml format with a message");
        XmlMapper xmlMapper = new XmlMapper();
        String message = "";
        try {
            message = xmlMapper.writeValueAsString(new Message(username));
        } catch (JsonProcessingException e) {
            logger.error("Xml string could not be created: {}", e.getMessage(), e);
        }
        return message;
    }

    private static Logger getLoggerAndSetConfigPath() {
        System.setProperty("logback.configurationFile", PATH_LOGGER);
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.debug("Get Logger and set the path to the logback configuration file");
        return logger;
    }

    /**
     * The object to serialize that creates a message "Привіт <name user>!"
     */
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
