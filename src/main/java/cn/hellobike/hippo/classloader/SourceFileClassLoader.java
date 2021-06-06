package cn.hellobike.hippo.classloader;

import cn.hellobike.hippo.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Auther: yuewenbo971@hellobike.com
 * @Date: 2021/5/22 17:42
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
public class SourceFileClassLoader extends ClassLoader {
	private String pac;
	@Override
	protected Class<?> findClass(String name) {
		Class cls = null;
		try {
			File file = new File(name);
			if (file.exists() && file.isFile()) {
				String pureName = Utils.trimSubfix(Utils.getFileName(name));
				byte[] bytes = FileUtils.readFileToByteArray(file);
				cls = defineClass(pac + ".bean." + pureName, bytes, 0, bytes.length);
			}
		} catch (Exception e) {

		}
		return cls;
	}
}