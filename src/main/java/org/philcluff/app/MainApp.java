package org.philcluff.app;

import org.apache.camel.main.Main;
import org.philcluff.route.SimpleRouteBuilder;

public class MainApp {


    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new SimpleRouteBuilder());
        main.run(args);
    }

}

