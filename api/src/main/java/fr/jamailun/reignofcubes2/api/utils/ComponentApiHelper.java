package fr.jamailun.reignofcubes2.api.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * I hate so much this chat-Component API...
 */
public final class ComponentApiHelper {
    private ComponentApiHelper() {}

    private static final Map<Character, String> CONVERSIONS = new HashMap<>() {{
        put('0', "<black>");
        put('1', "<dark_blue>");
        put('2', "<dark_green>");
        put('3', "<dark_aqua>");
        put('4', "<dark_red>");
        put('5', "<purple>");
        put('6', "<gold>");
        put('7', "<gray>");
        put('8', "<dark_gray>");
        put('9', "<blue>");
        put('a', "<green>");
        put('b', "<aqua>");
        put('c', "<red>");
        put('d', "<light_purple>");
        put('e', "<yellow>");
        put('f', "<white>");

        put('k', "<obf>"); // obfuscated
        put('l', "<b>");   // bold
        put('m', "<st>");  // strikethrough
        put('n', "<u>");   // underline
        put('o', "<i>");   // italic
        put('r', "<!k><!l><!m><!n><!o><white>");
    }};

    public static String convertLegacy(String legacy) {
        if(legacy == null || legacy.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        char[] chars = legacy.toCharArray();

        // init
        char previous = chars[0];
        boolean previousStart = isStart(previous);
        boolean ignoreNext = false;
        if(!previousStart) {
            output.append(previous);
        }

        print(chars, 0, previousStart, '\0', previous, "", ignoreNext);

        // loop
        for(int i = 1; i < chars.length; i++) {
            char c = chars[i];
            print(chars, i, previousStart, previous, c, output.toString(),ignoreNext);

            if(ignoreNext) {
                previous = c;
                previousStart = isStart(c);
                if(!previousStart) {
                    output.append(c);
                }
                ignoreNext = false;
                continue;
            }

            // Do we have a color ?
            if(previousStart) {
                // color !
                if((previous != c) && CONVERSIONS.containsKey(c)) {
                    output.append(CONVERSIONS.get(c));
                    ignoreNext = true;
                    previousStart = false;
                    continue;
                }
                // not a color : put the previous char back, and continue !
                output.append(previous);
            }

            previous = c;

            // Potential start of a future color.
            previousStart = isStart(c);
            if( ! previousStart ) {
                output.append(c);
            }
        }

        print(chars, chars.length, previousStart, previous, previous, output.toString(), ignoreNext);

        return output.toString();
    }

    private static boolean isStart(char c) {
        return c == '§' || c == '&';
    }

    private static void print(char[] chars, int i, boolean previousStart, char previous, char c, String out, boolean ignoreNext) {
        //System.out.println("> "+i+":["+ String.valueOf(chars, i, chars.length - i) +"]:'"+c+"'. " +
        //        "prev='"+previous+"':" + previousStart + "." + (ignoreNext?"IGNORE_NEXT":"") + " output=\""+out+"\"");
    }


}
