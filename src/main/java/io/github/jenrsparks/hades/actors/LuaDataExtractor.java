package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuaDataExtractor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public LuaDataExtractor() {
        // intentionally empty
    }
    
    /**
     * Convert the provided file contents into a native Java map comprised of 
     * key/value pairs. No intepretation will happen here; this is a simple
     * extract and deliver mechanism. The names or values may not seem terribly
     * useful at this stage, but another layer should be responible for 
     * interpeting the contents themselves.
     * 
     * @param inputFile Provided file in .lua format
     * @return Map of key / value pairs
     */
    public Map<String,Object> extract(File inputFile) {
        Map<String, Object> data = new HashMap<>();

        Globals globals = JsePlatform.standardGlobals();
        globals.loadfile( inputFile.getAbsolutePath() ).call();

        LuaValue key = LuaValue.NIL;
        /* Iterate through each subsequent key until running out of values, at 
        ** which point a NIL will be returned to start over from the top, so 
        ** we'll break out.
        **/
        while(true) {
            Varargs pair = globals.next(key);
            key = pair.arg1();

            if(key.isnil()) {
                break;
            }

            // Key is actually part of Lua's standard environment strings and/or functions
            if(this.getLuaEnvironmentKeyNames().contains(key.tojstring())) {
                continue;
            }

            LuaValue luaData = pair.arg(2);
            if (!luaData.isnil() && !luaData.isfunction()) { // make sure this is relevant data
                Set<LuaTable> seenTables = new HashSet<>();
                Object converted = convertToNativeType(luaData, seenTables);
                data.put(key.tojstring(), converted);
            }

        }

        return data;
    }

    protected Object convertToNativeType(LuaValue luaData, Set<LuaTable> seenTables) {
        if(luaData == null)     return null;
        if(luaData.isnil())     return null;
        if(luaData.isfunction()) return null;
        if(luaData.isboolean()) return luaData.toboolean();
        if(luaData.isint())     return luaData.toint();
        if(luaData.islong())    return luaData.tolong();
        if(luaData.isnumber())  return luaData.todouble();
        if(luaData.isstring())  return luaData.tojstring();

        if(luaData.istable()) { 
            LuaTable table = luaData.checktable();

            if(seenTables.contains(table)) {
                logger.warn("Already seen this table, skipping to avoid infinite recursion : " + table.toString());
                return "[Circular Reference]";
            }

            if(table.length() > 0) { // Indicative of a list type
                return convertList(table, seenTables);
            } else { // Otherwise, assume it's a map
                return convertMap(table, seenTables);
            }
        }
        // fallthrough scenario - something didn't get detected as expected
        logger.warn("Unsure what type this is!?");
        return luaData.tojstring(); // force a string value
    }

    protected List<Object> convertList(LuaTable table, Set<LuaTable> seenTables) {
        List<Object> entryList = new ArrayList<>();
        for (int i = 1; i <= table.length(); i++) {
            Object convertedItem = convertToNativeType(table.get(i), seenTables);

            if(convertedItem != null) {
                entryList.add(convertedItem);
            }
        }
        return entryList;
    }

    protected Map<String, Object> convertMap(LuaTable table, Set<LuaTable> seenTables) {
        Map<String, Object> map = new HashMap<>();

        LuaValue key = LuaValue.NIL;
        do {
            Varargs next = table.next(key);
            key = next.arg1();
            if (!key.isnil()) {
                LuaValue value = next.arg(2);
                // potential recursion entrance point:
                Object convertedValue = convertToNativeType(value, seenTables);
                if (convertedValue != null) {
                    map.put(key.tojstring(), convertedValue);
                }
            }
        } while (!key.isnil());

        return map;

    }

    private List<String> getLuaEnvironmentKeyNames() {
        // Ignore ALL of Lua's standard environment libraries
        return List.of("_G", "_VERSION", "package", "string", "math", "table",
                "coroutine", "os", "debug", "io", "bit32", "luajava");
    }

}