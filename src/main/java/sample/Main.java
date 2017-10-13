package sample;

import config.*;
import javafx.application.*;
import javafx.stage.*;
import model.*;
import org.apache.logging.log4j.*;
import org.opencv.core.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;

@Service
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.getProperty("java.library.path");

        logger.info("Starting application");

        Platform.setImplicitExit(true);

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScreensConfig screens = context.getBean(ScreensConfig.class);
        LanguageModel lang = context.getBean(LanguageModel.class);

        screens.setLangModel(lang);
        screens.setPrimaryStage(primaryStage);
        screens.showMainScreen();
        screens.loadFirst();
    }


}
