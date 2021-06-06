/**
 * Copyright 2021 bejson.com
 */
package cn.hellobike.hippo.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auto-generated: 2021-05-25 15:24:36
 *
 * @author bejson.com (i@bejson.com)
 *  x@website http://www.bejson.com/java2pojo/
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestHeader {

	private String required;
	private String _id;
	private String name;
	private String value;
}