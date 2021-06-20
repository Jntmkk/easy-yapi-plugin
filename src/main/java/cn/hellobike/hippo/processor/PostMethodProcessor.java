package cn.hellobike.hippo.processor;

import cn.hellobike.hippo.json.ActionContext;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.entity.ApiRequestHeaderEntity;
import cn.hellobike.hippo.yapi.request.UpdateOrCreateRequest;
import cn.hutool.core.collection.CollectionUtil;
import com.intellij.psi.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PostMethodProcessor extends AbstractContextProcessor<ActionContext, UpdateOrCreateRequest> {
    public static final Set<String> TARGET_METHOD = new HashSet<>(Arrays.asList(new String[]{"POST", "Post"}));

    @Override
    public void process(ActionContext context, UpdateOrCreateRequest entity) throws Exception {
        if (TARGET_METHOD.contains(entity.getMethod())) {
            PsiParameter[] parameters = context.getCurrentMethod().getParameterList().getParameters();
            if (parameters != null && parameters.length == 1) {
                entity.setReq_headers(Arrays.asList(ApiRequestHeaderEntity.builder().name("Content-Type").value("application/json").build()));
                entity.setReq_body_type("json");
                entity.setReq_body_other(Utils.getPsiTypeJsonSchemaAsString(context.getPsiFile(), parameters[0].getType(), false));
            }

            entity.setRes_body_type("json");
            entity.setRes_body(Utils.getPsiTypeJsonSchemaAsString(context.getPsiFile(), context.getCurrentMethod().getReturnType(), true));
        }
        doNext(context, entity);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
