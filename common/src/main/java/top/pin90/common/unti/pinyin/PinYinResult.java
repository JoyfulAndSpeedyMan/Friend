package top.pin90.common.unti.pinyin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PinYinResult {
    private String fullPinyin;
    private String simplePinyin;
}
