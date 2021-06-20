package cn.hellobike.hippo.processor;

import cn.hellobike.hippo.exception.ParseException;
import cn.hellobike.hippo.json.ActionContext;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.request.UpdateOrCreateRequest;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;

public class DefaultParamsBuilderProcessor extends AbstractContextProcessor<ActionContext, UpdateOrCreateRequest> {
    @Override
    public void process(ActionContext context, UpdateOrCreateRequest entity) throws Exception {
        PsiClass psiClass = context.getPsiClass();
        if (psiClass == null) {
            throw new ParseException("当前不是Java源码文件！");
        }
        PsiMethod currentMethod = context.getCurrentMethod();
        if (currentMethod == null) {
            throw new ParseException("当前行未检测方法！");
        }
        // path title desc comment method projectId
        String projectId = context.getYaPiConfig().getProjectId();

        String defaultTitle = Utils.getDefaultTitle(context);
        String defaultPath = Utils.getDefaultPath(context);

        PsiDocComment clsDoc = psiClass.getDocComment();
        PsiDocComment methodDoc = currentMethod.getDocComment();
        String defaultDesc = null;
        if (clsDoc != null) {
            defaultDesc = clsDoc.getText();
        }
        if (methodDoc != null) {
            defaultDesc += methodDoc.getText();
        }

        entity.setProjectId(Integer.valueOf(projectId));
        entity.setPath(defaultPath);
        entity.setTitle(defaultTitle);
        entity.setDesc(defaultDesc);
        entity.setMethod("POST");
        entity.setToken(context.getYaPiConfig().getToken());
        doNext(context, entity);
    }

    @Override
    public int getOrder() {
        return Order.FIRST;
    }
}
