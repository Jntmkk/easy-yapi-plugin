package cn.hellobike.hippo.json;

import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: yuewenbo971@hellobike.com
 * @Date: 2021/5/22 17:13
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleJsonNode {
	JsonNodeType type;
	String description;
	String id;
	String title;
	String mock;
	List<SimpleJsonNode> properties;
	List<String> required;
}