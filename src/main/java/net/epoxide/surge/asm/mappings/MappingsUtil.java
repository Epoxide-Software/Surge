package net.epoxide.surge.asm.mappings;

import java.util.HashMap;
import java.util.Map;

public class MappingsUtil {
    private static Map<String, String> PRIMITIVE_MAP = new HashMap<>();

    static {
        PRIMITIVE_MAP.put("void", "V");
        PRIMITIVE_MAP.put("int", "I");
        PRIMITIVE_MAP.put("boolean", "Z");
        PRIMITIVE_MAP.put("char", "C");
        PRIMITIVE_MAP.put("byte", "B");
        PRIMITIVE_MAP.put("float", "F");
        PRIMITIVE_MAP.put("long", "J");
        PRIMITIVE_MAP.put("double", "D");
    }

    public static String getPrimitiveBytecode(Class<?> clazz) {

        if (PRIMITIVE_MAP.containsKey(clazz.getName()))
            return PRIMITIVE_MAP.get(clazz.getName());
        return "L";
    }

    public static String getDescriptorFromClass(Class<?> clazz) {

        String descriptor = MappingsUtil.getPrimitiveBytecode(clazz);
        if (descriptor.equals("L"))
            descriptor += clazz.getName().replace(".", "/") + ";";
        return descriptor;
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>[] params) {

        if (returnType == null) {
            throw new RuntimeException("ReturnType can't be null");
        }
        String descriptor = "(";
        if (params != null) {
            for (Class<?> clazz : params) {
                descriptor += getDescriptorFromClass(clazz);
            }
        }
        descriptor += ")" + getPrimitiveBytecode(returnType);
        return descriptor;
    }
}
