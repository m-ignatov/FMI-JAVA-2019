package bg.sofia.uni.fmi.mjt.p2p.client.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Command {
    REGISTER("register", 2),
    UNREGISTER("unregister", 2),
    LIST_FILES("list-files", 0),
    LIST_USERS("list-users", -1),
    DOWNLOAD("download", 3),
    DISCONNECT("disconnect", 0);

    private final String name;
    private final Integer arguments;

    private static final Map<String, Command> ENUM_MAP;

    Command(String name, Integer arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public Integer getArguments() {
        return arguments;
    }

    static {
        Map<String, Command> map = new ConcurrentHashMap<>();
        for (Command command : Command.values()) {
            map.put(command.getName(), command);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Command get(String name) {
        return ENUM_MAP.get(name);
    }
}
