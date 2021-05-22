package top.pin90.server.service.impl;

import org.springframework.stereotype.Service;
import top.pin90.common.unti.pinyin.PinYinResult;
import top.pin90.common.unti.pinyin.PinyinUtils;
import top.pin90.server.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public String getAllPinyin(String content) {
        PinYinResult pinYinResult = PinyinUtils.changeChinesePinyin(content);
        return pinYinResult.getFullPinyin();
    }

    @Override
    public String getSimplePinyin(String content) {
        PinYinResult pinYinResult = PinyinUtils.changeChinesePinyin(content);
        return pinYinResult.getSimplePinyin();
    }

    @Override
    public String groupName(String content) {
        String simplePinyin = getSimplePinyin(content);
        if(simplePinyin== null || simplePinyin.isEmpty())
            return "#";
        char c = simplePinyin.charAt(0);
        if(Character.isUpperCase(c))
            return Character.toString(c);
        return "#";
    }
}
