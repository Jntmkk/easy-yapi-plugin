package cn.hellobike.hippo.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class User {
    @ApiModelProperty(required = true, name = "姓名", value = "value", example = "example", notes = "notes",allowEmptyValue = true,allowableValues = "")
    private String name;
    private String pwd;
    @ApiModelProperty(required = true,name = "age",value = "value",example = "example",notes = "note",allowableValues = "1,100")
    private Integer age;
}
