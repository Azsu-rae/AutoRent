package orm.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static final Logger logger = Logger.getLogger("MyPain");
    static {
        try {
            FileHandler fileHandler = new FileHandler("MyPain.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setEncoding("UTF-8");
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
