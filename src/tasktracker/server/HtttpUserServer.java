package tasktracker.server;

import com.sun.net.httpserver.HttpServer;

import tasktracker.managers.TaskManager;
import tasktracker.managers.UserManager;

public class HtttpUserServer {

    public  static final int PORT = 8080;
    private HttpServer server;

    private TaskManager taskManager;
    private UserManager userManager;


}
