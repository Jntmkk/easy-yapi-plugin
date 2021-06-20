package cn.hellobike.hippo.actions;

import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.exception.EditorInfoException;
import cn.hellobike.hippo.exception.HttpRequestException;
import cn.hellobike.hippo.exception.YaPiException;
import cn.hellobike.hippo.json.ActionContext;
import cn.hellobike.hippo.processor.*;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.request.UpdateOrCreateRequest;
import cn.hellobike.hippo.yapi.response.UpdateOrCreateResponse;
import cn.hellobike.hippo.yapi.service.YaPiService;
import cn.hellobike.hippo.yapi.service.YaPiServiceImpl;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;

import java.util.*;

public class UploadToYaPi extends AnAction {
    public static Logger log = Logger.getInstance(UploadToYaPi.class);
    public static final String TARGET_ANNOTATION = "cn.hellobike.hippo.annotation.YaPiApi";
    public static final String PROJECT_TARGET_ANNOTATION = "cn.hellobike.hippo.annotation.YaPiProject";

    @SneakyThrows
    @Override
    public void actionPerformed(AnActionEvent e) {

        YaPiConfig yaPiConfig = checkConfig(e.getProject());
        if (yaPiConfig == null) {
            return;
        }
        YaPiService service = new YaPiServiceImpl(yaPiConfig.getUrl(), yaPiConfig.getToken());
        ActionContext actionContext = new ActionContext(e, service, yaPiConfig);


        try {
            UpdateOrCreateRequest request = new UpdateOrCreateRequest();
            for (AbstractContextProcessor abstractContextProcessor : getProcessor()) {
                abstractContextProcessor.process(actionContext, request);

                UpdateOrCreateResponse updateOrCreateResponse = service.updateOrCreate(request);
                if (updateOrCreateResponse.getErrcode() != 0) {
                    throw new YaPiException(updateOrCreateResponse.getErrmsg());
                }
            }
        } catch (EditorInfoException editorInfoException) {
            Utils.showErrorHint(actionContext.getEditor(), editorInfoException.getMessage());
            log.info(editorInfoException);
            return;
        } catch (ClassNotFoundException notFoundException) {
            String s = notFoundException.getMessage().replaceAll("/", ".");
            Utils.notify(String.format("未发现class文件%s编译一下？", s), NotificationType.INFORMATION);
            return;
        } catch (HttpRequestException requestException) {
            log.info(requestException);
            Utils.notify(String.format("出错啦--%s", requestException.getMessage()), NotificationType.ERROR);
            return;
        } catch (Exception exception) {
            log.error(exception);
            Utils.notify("出错了，告诉我日志吧", NotificationType.ERROR);
            return;
        }
        Utils.notify("保存成功", NotificationType.INFORMATION);
    }

    private List<AbstractContextProcessor> getProcessor() {
        List<AbstractContextProcessor> ans = new LinkedList<>();
        ans.add(new DefaultParamsBuilderProcessor());
        ans.add(new PostMethodProcessor());
        Collections.sort(ans, Comparator.comparingInt(Order::getOrder));
        for (int i = 0; i < ans.size() - 1; i++) {
            ans.get(i).setNext(ans.get(i + 1));
        }
        return ans;
    }

    private YaPiConfig checkConfig(Project project) {
        YaPiConfig config = project.getComponent(YaPiConfig.class);
        if (config == null) {
            Utils.notify("获取config失败", NotificationType.INFORMATION);
            return null;
        }
        if (config.getUrl() == null || config.getProjectId() == null || config.getToken() == null) {
            Utils.notify("请先配置 token、projectID、URL", NotificationType.INFORMATION);
            return null;
        }
        return config;
    }
}
