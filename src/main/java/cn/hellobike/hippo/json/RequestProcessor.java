package cn.hellobike.hippo.json;

import cn.hellobike.hippo.yapi.entity.AddInterfaceResponseEntity;
import cn.hellobike.hippo.yapi.entity.GetInterfaceByIdResponseEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import cn.hellobike.hippo.yapi.response.AddInterfaceResponse;

public interface RequestProcessor extends Order {
    default void process(ActionContext actionContext, AddInterfaceRequest request) throws Exception {

    }

    default void process(ActionContext actionContext, GetInterfaceByIdResponseEntity response, UpdateInterfaceRequest request) throws Exception {

    }

    RequestProcessor getNext();
}

