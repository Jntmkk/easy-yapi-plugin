package cn.hellobike.hippo.processor;

import cn.hellobike.hippo.json.ActionContext;
import cn.hellobike.hippo.yapi.request.UpdateOrCreateRequest;

public class DefaultAnnotationProcessor extends AbstractContextProcessor<ActionContext, UpdateOrCreateRequest> {
    @Override
    public void process(ActionContext context, UpdateOrCreateRequest entity) throws Exception {
        doNext(context, entity);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
