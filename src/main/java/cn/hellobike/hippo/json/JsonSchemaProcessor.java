package cn.hellobike.hippo.json;

import cn.hellobike.hippo.exception.MethodNotFoundException;
import cn.hellobike.hippo.exception.ProcessException;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.entity.GetInterfaceByIdResponseEntity;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonSchemaProcessor extends BaseRequestProcessor {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void process(ActionContext actionContext, GetInterfaceByIdResponseEntity response, UpdateInterfaceRequest request) throws Exception {
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        if (currentMethod == null) {
            throw new MethodNotFoundException(actionContext.getEditor());
        }
        PsiParameter[] parameters = currentMethod.getParameterList().getParameters();
        if (parameters != null) {
            if (parameters.length > 1) {
                throw new ProcessException(String.format("只支持一个请求参数，当前{}个", parameters.length));
            }
            String psiTypeJsonSchemaAsString = Utils.getPsiTypeJsonSchemaAsString(actionContext.getPsiFile(), parameters[0].getType(), false);
            request.setReq_body_is_json_schema(true);
            request.setReq_body_other(psiTypeJsonSchemaAsString);
            request.setReq_body_type("json");
        }
        PsiType returnType = currentMethod.getReturnType();
        if (returnType != null) {
            request.setRes_body_is_json_schema(true);
            request.setRes_body_type("json");
            String returnJsonSchema = Utils.getPsiTypeJsonSchemaAsString(actionContext.getPsiFile(), returnType, true);
            request.setRes_body(returnJsonSchema);
        }
        getNext().process(actionContext, response, request);
    }
}
