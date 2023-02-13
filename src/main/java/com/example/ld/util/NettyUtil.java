package com.example.ld.util;

import com.example.ld.pojo.TaskInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HeYiyu
 * @date 2020/6/12
 */
public class NettyUtil {
    private static final Map<Character, Class<?>> characterClassMap = new HashMap<>();
    private static final Map<Class<?>, Character> classCharacterMap = new HashMap<>();

    static {
        characterClassMap.put('b', byte[].class);
        characterClassMap.put('s', String.class);
        characterClassMap.put('t', TaskInfo.class);
        classCharacterMap.put(byte[].class, 'b');
        classCharacterMap.put(String.class, 's');
        classCharacterMap.put(TaskInfo.class, 't');
    }

    public static Character getCharacter(Class<?> clazz) {
        return classCharacterMap.get(clazz);
    }

    public static Class<?> getClazz(Character character) {
        return characterClassMap.get(character);
    }
}
