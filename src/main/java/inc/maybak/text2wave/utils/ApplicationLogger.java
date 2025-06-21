package inc.maybak.text2wave.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLogger {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

}
