package cn.hellobike.hippo.json;

import cn.hellobike.hippo.exception.MethodNotFoundException;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.entity.GetInterfaceByIdResponseEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 为前期参数添加默认值，此时还未处理{@link cn.hellobike.hippo.annotation.YaPiApi}和{@link cn.hellobike.hippo.annotation.YaPiProject}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultProcessor extends BaseRequestProcessor {

    @Override
    public void process(ActionContext actionContext, AddInterfaceRequest request) throws Exception {
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        if (currentMethod == null) {
            throw new MethodNotFoundException(actionContext.getEditor());
        }
        String name = currentMethod.getName();
        request.setPath(Utils.getRelativePath(request.getPath(), name));

        PsiDocComment docComment = currentMethod.getDocComment();
        if (docComment != null) {
            request.setTitle(docComment.toString());
        }
        request.setProject_id(actionContext.getYaPiConfig().getProjectId());
        request.setMethod("POST");
    }

    @Override
    public void process(ActionContext actionContext, GetInterfaceByIdResponseEntity response, UpdateInterfaceRequest request) throws Exception {
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        if (currentMethod == null) {
            throw new MethodNotFoundException(actionContext.getEditor());
        }
        String name = currentMethod.getName();
        request.setPath(Utils.getRelativePath(request.getPath(), name));

        PsiDocComment docComment = currentMethod.getDocComment();
        if (docComment != null) {
            request.setTitle(docComment.toString());
            request.setDesc(docComment.toString());
        }
        request.setMethod("POST");
    }

    @Override
    public int getOrder() {
        return RequestProcessor.FIRST;
    }
}
