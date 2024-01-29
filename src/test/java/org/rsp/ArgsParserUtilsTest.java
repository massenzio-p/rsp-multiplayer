package org.rsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rsp.util.ArgsParserUtils;

class ArgsParserUtilsTest {

    @Test
    void testArgsPortResolution() {
        String[] args = null;
        int defaultPort = 123;
        int actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{""};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"asdfasdf"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"asdf"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"--port"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"--port", "sad;fkjasd;f"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"-p"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"-p", "as;dlkfjas;"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"-p", "--port"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);


        int customPort = 321;

        args = new String[]{"-p", "--port", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[]{"--port", "-p", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(defaultPort, actualPort);

        args = new String[] {"--port", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);

        args = new String[] {"-p", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);

        args = new String[] {"dfsdaf", "-p", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);

        args = new String[] {"-p", Integer.toString(customPort), "asd;lfj"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);

        args = new String[] {"dfsdaf", "--port", Integer.toString(customPort)};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);

        args = new String[] {"--port", Integer.toString(customPort), "asd;lfj"};
        actualPort = ArgsParserUtils.determinePort(args, defaultPort);
        Assertions.assertEquals(customPort, actualPort);
    }
}