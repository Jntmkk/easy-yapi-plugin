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
@State(name = "cn.hellobike.hippo.config.YaPiConfig", storages = {@Storage("SdkSettingsPlugin.xml")})
public class YaPiConfig implements PersistentStateComponent<YaPiConfig>, Serializable {
	String token="afadb0d289b7772fe43a9d6fa05d2206cb12166cfe264bc086a42bafd84a75c4";
	String projectId = "11";
	String url = "http://127.0.0.1:40001";

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