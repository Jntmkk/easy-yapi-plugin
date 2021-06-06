package cn.hellobike.hippo.json;

import cn.hellobike.hippo.exception.ErrorException;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;

public class ValidatorProcessor extends BaseRequestProcessor {
    @Override
    public int getOrder() {
        return FIRST;
    }

    @Override
    public void process(ActionContext actionContext, AddInterfaceRequest request) throws Exception {
        chekFile(actionContext);
    }

    private void chekFile(ActionContext actionContext) throws ErrorException {
        if (actionContext.getPsiFile() == null || actionContext.getPsiClass() == null) {
            throw new ErrorException("只针对Java源码使用");
        }
    }
}
