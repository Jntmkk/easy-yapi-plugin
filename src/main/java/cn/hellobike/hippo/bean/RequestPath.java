/**
 * Copyright 2021 bejson.com
 */
package cn.hellobike.hippo.bean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Auto-generated: 2021-05-25 15:27:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPath {
	private String path;
	private List<String> params;
}