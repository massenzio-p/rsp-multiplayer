package org.rsp.util;

import java.util.logging.Logger;

public final class ArgsParserUtils {
    private static final Logger logger = Logger.getLogger(ArgsParserUtils.class.getName());

    private ArgsParserUtils() {}

    public static int determinePort(String[] args, int defaultPort) {
        int port = defaultPort;

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if ((args[i].equals("--port") || args[i].equals("-p")) && i < args.length - 1) {
                    try {
                        port = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        logger.warning(String.format("Port parameter [%s] is invalid, keeping default value.", args[0]));
                    }
                    break;
                }
            }
        }

        return port;
    }
}
