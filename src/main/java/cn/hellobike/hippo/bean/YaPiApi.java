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
 * Auto-generated: 2021-05-25 15:22:35
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YaPiApi {

	private RequestPath query_path;
	private int edit_uid;
	private String status;
	private String type;
	private boolean req_body_is_json_schema;
	private boolean res_body_is_json_schema;
	private boolean api_opened;
	private int index;
	private List<String> tag;
	private int _id;
	private String method;
	private int catid;
	private String title;
	private String path;
	private int project_id;
	private List<String> req_params;
	private String res_body_type;
	private int uid;
	private long add_time;
	private long up_time;
	private List<RequestQuery> req_query;
	private List<RequestHeader> req_headers;
	private List<String> req_body_form;
	private int __v;
	private String markdown;
	private String desc;
	private String res_body;
	private String req_body_type;
	private String req_body_other;
	private String username;
}