package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.jenrsparks.hades.constants.LuaConstant;

public class LuaDataExtractor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public LuaDataExtractor() {
        // intentionally empty
    }
    
    /**
     * Convert the provided file contents into a native Java map comprised of key/value pairs. No intepretation 
     * will happen here; this is a simple extract and deliver mechanism. The names or values may not seem terribly useful
     * at this stage, but another layer should be responible for interpeting the contents themselves.
     * 
     * @param inputFile Provided file in .lua format
     * @return Map of key / value pairs
     */
    public Map<String,Object> extract(File inputFile) {
        Map<String, Object> data = new HashMap<>();

        Globals globals = JsePlatform.standardGlobals();
        globals.loadfile( inputFile.getAbsolutePath() ).call();

        // TODO Change to list from resource file (?)
        List<LuaConstant> topLevelTargets = List.of( LuaConstant.LUA_DATA_KEY );
        for(LuaConstant target : topLevelTargets) {
            LuaValue luaData = globals.get( target.getFieldName() );

            if (luaData.isnil()) {
                logger.warn("Could not find " + target.getFieldName() + " in the file.");
            } else {
                Object converted = convert(luaData);
                data.put(target.getFieldName(), converted);
            }
        }
        return data;
    }

    protected Object convert(LuaValue luaData) {
        if(luaData == null) return null;
        if(luaData.isboolean()) return luaData.toboolean();
        if(luaData.isint())     return luaData.toint();
        if(luaData.isnumber())  return luaData.tolong(); // no doubles here, folks
        if(luaData.isstring())  return luaData.tostring();
        if(luaData.isnil())     return null;
        if(luaData.istable()) { 
            LuaTable table = luaData.checktable();
            if(table.length() > 0) { // Indicative of a list type
                return convertList(table);
            } else { // Otherwise, assume it's a map
                return convertMap(table);
            }
        }
        // fallthrough scenario - something didn't get detected as expected
        return luaData.tojstring(); // force a string value
    }

    protected List<Object> convertList(LuaTable table) {
        List<Object> entryList = new ArrayList<>();
        for (int i = 0; i < table.length(); i++) {
            Object convertedItem = convert(table.get(i));
            entryList.add(convertedItem);
        }
        return entryList;
    }

    protected Map<String, Object> convertMap(LuaTable table) {
        Map<String, Object> map = new HashMap<>();

        LuaValue key = LuaValue.NIL;
        do {
            Varargs next = table.next(key);
            key = next.arg1();
            if (!key.isnil()) {
                LuaValue value = next.arg(2);
                // potential recursion entrance point:
                Object convertedValue = convert(value);
                map.put(key.tojstring(), convertedValue);
            }
        } while (!key.isnil());

        return map;

    }

}