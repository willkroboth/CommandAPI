package dev.jorel.commandapi;

import java.util.List;

/**
 * Class to store a registered command which has its command name and a
 * list of arguments as a string. The arguments are expected to be of the
 * form node_name:class_name, for example value:IntegerArgument
 */
public record RegisteredCommand(String command, List<String> argsAsStr) {};