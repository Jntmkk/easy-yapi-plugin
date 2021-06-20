package cn.hellobike.hippo.json;

import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.processor.BaseContext;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.service.YaPiService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import lombok.Data;
import org.apache.velocity.VelocityContext;

import java.util.HashMap;
import java.util.Map;

@Data
public class ActionContext implements BaseContext {
    public static final String API_ANNOTATION = "cn.hellobike.hippo.annotation.YaPiApi";
    private AnActionEvent action;
    private YaPiService yaPiService;
    private YaPiConfig yaPiConfig;
    private Map<Class, Object> map = new HashMap<>();

    public ActionContext(AnActionEvent action, YaPiService service, YaPiConfig yaPiConfig) {
        this.action = action;
        this.yaPiService = service;
        this.yaPiConfig = yaPiConfig;
    }


    public Project getProject() {
        return action.getProject();
    }

    public Editor getEditor() {
        return action.getRequiredData(CommonDataKeys.EDITOR);
    }

    public PsiFile getPsiFile() {
        PsiFile psiFile = action.getDataContext().getData(CommonDataKeys.PSI_FILE);
        return psiFile;
    }

    public PsiClass getPsiClass() {
        return Utils.getPsiClass(getEditor(), getPsiFile());
    }

    public PsiMethod getCurrentMethod() {
        return Utils.getCurrentLineMethod(getEditor(), getPsiFile());
    }

    public PsiJavaFile getPsiJavaFile() {
        return (PsiJavaFile) getPsiClass().getContainingFile();
    }

    public void setValue(Class cls, Object obj) {
        map.put(cls, obj);
    }

    public <T> T getValue(Class<T> cls) {
        return (T) map.get(cls);
    }
}
