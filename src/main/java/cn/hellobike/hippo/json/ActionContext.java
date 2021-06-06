package cn.hellobike.hippo.json;

import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.service.YaPiService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import lombok.Data;

@Data
public class ActionContext {
    private AnActionEvent action;
    private YaPiService yaPiService;
    private YaPiConfig yaPiConfig;

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
}
