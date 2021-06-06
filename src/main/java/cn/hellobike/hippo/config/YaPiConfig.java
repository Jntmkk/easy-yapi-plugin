package cn.hellobike.hippo.config;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * @Auther: yuewenbo971@hellobike.com
 * @Date: 2021/5/28 19:36
 * @Description:
 */
@Data
@State(name = "YaPiConfig", storages = {@Storage("yapi.xml")})
public class YaPiConfig implements PersistentStateComponent<YaPiConfig>, Serializable {
    String token;
    String projectId;
    String url;

    @Override
    public @Nullable YaPiConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull YaPiConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static YaPiConfig getConfig() {
        return ServiceManager.getService(YaPiConfig.class);
    }
}