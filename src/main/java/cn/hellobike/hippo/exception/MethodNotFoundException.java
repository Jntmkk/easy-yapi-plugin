package cn.hellobike.hippo.exception;

import cn.hellobike.hippo.utils.Utils;
import com.intellij.openapi.editor.Editor;

public class MethodNotFoundException extends ProcessException {
    public MethodNotFoundException(Editor editor) {
        this(String.format("在第{}行未检测到方法", Utils.getRow(editor)));
    }

    public MethodNotFoundException(String message) {
        super(message);
    }
}
