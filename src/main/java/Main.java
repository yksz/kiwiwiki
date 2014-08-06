import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        URL war = Main.class.getProtectionDomain().getCodeSource().getLocation();
        WebAppContext webapp = new WebAppContext();
        webapp.setWar(war.toExternalForm());

        Server server = new Server(port);
        server.setHandler(webapp);
        server.start();
        server.join();
    }

}
