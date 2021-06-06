package cn.hellobike.hippo.json;

import cn.hellobike.hippo.yapi.Utils;
import cn.hellobike.hippo.yapi.entity.GetInterfaceByIdResponseEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import cn.hellobike.hippo.yapi.response.GetInterfaceByIdResponse;
import cn.hellobike.hippo.yapi.service.YaPiService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

public class FetchRemoteInfo extends BaseRequestProcessor {
    @Override
    public void process(ActionContext actionContext, AddInterfaceRequest request) throws Exception {
        YaPiService service = actionContext.getYaPiService();
        GetInterfaceByIdResponse interfaceOrCreate = service.getInterfaceOrCreate(request);
        BeanUtil.copyProperties(interfaceOrCreate, request, CopyOptions.create().ignoreNullValue().ignoreError().ignoreCase());
    }

    @Override
    public void process(ActionContext actionContext, GetInterfaceByIdResponseEntity response, UpdateInterfaceRequest request) throws Exception {
        UpdateInterfaceRequest updateInterfaceRequest = Utils.mapGetResponseToUpdateRequest(response);
        BeanUtil.copyProperties(updateInterfaceRequest, request, CopyOptions.create().ignoreCase().ignoreError().ignoreNullValue());

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
