package cn.hellobike.hippo.json;

import cn.hellobike.hippo.annotation.YaPiAnnotationEntity;
import cn.hellobike.hippo.exception.MethodNotFoundException;
import cn.hellobike.hippo.exception.ProcessException;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.entity.GetInterfaceByIdResponseEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class AnnotationProcessor extends BaseRequestProcessor {
    public static final String PROJECT_ANNOTATION = "";
    public static final String API_ANNOTATION = "";

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void process(ActionContext actionContext, AddInterfaceRequest request) throws Exception {
        String before = request.getPath();
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        if (currentMethod == null) {
            throw new MethodNotFoundException(actionContext.getEditor());
        }
        YaPiAnnotationEntity defaultAnnotationEntity = getYaPiAnnotationEntity(currentMethod);

        BeanUtil.copyProperties(defaultAnnotationEntity, request, CopyOptions.create().ignoreNullValue().ignoreCase().ignoreError());

        request.setPath(Utils.getRelativePath(before, request.getPath()));
    }


    @NotNull
    private YaPiAnnotationEntity getYaPiAnnotationEntity(PsiMethod currentMethod) throws Exception {
        PsiAnnotation apiAnnotation = currentMethod.getAnnotation(API_ANNOTATION);
        if (apiAnnotation == null) {
            throw new ProcessException("未使用@YaPiApi直接指定 service ？");
        }
        YaPiAnnotationEntity defaultAnnotationEntity = Utils.getDefaultAnnotationEntity(apiAnnotation);
        return defaultAnnotationEntity;
    }

    @Override
    public void process(ActionContext actionContext, GetInterfaceByIdResponseEntity response, UpdateInterfaceRequest request) throws Exception {
        String before = request.getPath();
        YaPiAnnotationEntity annotationEntity = getYaPiAnnotationEntity(actionContext.getCurrentMethod());
        BeanUtil.copyProperties(annotationEntity, request, CopyOptions.create().ignoreNullValue().ignoreCase().ignoreError());

        request.setPath(Utils.getRelativePath(before, request.getPath()));
    }
}
