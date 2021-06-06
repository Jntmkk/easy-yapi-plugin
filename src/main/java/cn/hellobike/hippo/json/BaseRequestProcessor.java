package cn.hellobike.hippo.json;

import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequestProcessor implements RequestProcessor {
    private BaseRequestProcessor next;

}
