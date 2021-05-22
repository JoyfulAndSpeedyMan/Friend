package top.pin90.common.unti.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import top.pin90.common.unti.MyBeanWrap;

import java.util.*;

public class PinyinUtils {
    public static HanyuPinyinOutputFormat format;

    static {
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static PinYinResult changeChinesePinyin(String chinese) {

        StringBuilder fullPinyin = new StringBuilder();
        StringBuilder simplePinyin = new StringBuilder();

        char[] chineseChar = chinese.toCharArray();
        for (char c : chineseChar) {
            String[] str = null;
            try {
                str = PinyinHelper.toHanyuPinyinStringArray(c, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (str != null && str.length > 0) {
                fullPinyin.append(str[0]);
                simplePinyin.append(str[0].charAt(0));
            } else {
                fullPinyin.append(c);
                simplePinyin.append(c);
            }
        }
        return new PinYinResult(fullPinyin.toString(),simplePinyin.toString().toUpperCase());
    }

    public static <T> Map<Character, ArrayList<T>> spellGroup(List<T> list, String index) {
        Map<Character, ArrayList<T>> map = new LinkedHashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            map.put(c, new ArrayList<>());
        }
        map.put('#', new ArrayList<>());

        if (list == null || list.isEmpty())
            return map;

        for (T str : list) {
            MyBeanWrap wrap = MyBeanWrap.wrap(str);
            Object o = wrap.get(index);
            String code;
            if (!(o instanceof String)) {
                continue;
            }
            else {
                code = changeChinesePinyin((String) o).getSimplePinyin();
            }
            if(code.isEmpty())
                continue;
            char c = code.charAt(0);
            ArrayList<T> arr;
            if (Character.isUpperCase(c)) {
                arr = map.get(c);
            } else {
                arr = map.get('#');
            }
            arr.add(str);
        }
        return map;
    }

    public static Map<Character, ArrayList<String>> spellGroup(List<String> list) throws BadHanyuPinyinOutputFormatCombination {
        Map<Character, ArrayList<String>> map = new LinkedHashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            map.put(c, new ArrayList<>());
        }
        map.put('#', new ArrayList<>());
        for (String str : list) {
            String code = changeChinesePinyin(str).getSimplePinyin();
            char c = code.charAt(0);

            ArrayList<String> strings;
            if (Character.isUpperCase(c)) {
                strings = map.get(c);
            } else {
                strings = map.get('#');
            }
            strings.add(str);
        }
        return map;
    }
}