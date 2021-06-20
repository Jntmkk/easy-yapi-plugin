# easy-yapi-plugin

非入侵式：API参数有类名、方法名等信息生成 入侵式：提供注解指定API参数信息

**依赖**

| 名称 | 作用 | 备注 |
| :-----| ----: | :----: |
| easy-yapi-core | 由 `eays-yapi-plugin`使用，提供简单的与 YAPI接口及交互的 API | 单元格 |
| easy-yapi-annotation | 注解信息 | 项目可引入 |

```java

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD})
@Inherited
public @interface YaPiApi {
	/**
	 * 路径,唯一，根据路径反查询，是更新还是新建
	 *
	 * @return
	 */
	String path() default "";

	/**
	 * 不提供就公共分类，暂不提供按分类名称
	 *
	 * @return
	 */
	String catId() default "";

	/**
	 * 分类名称，由于分类名称可重复，因此{@link this#catId()}优先级比当前高
	 * 如果存在多个同名分类，则取第一个
	 *
	 * @return
	 */
	String catText() default "";

	/**
	 * 属于哪个服务，若不存在则提示
	 *
	 * @return
	 */
	String service();

	/**
	 * 接口标题
	 *
	 * @return
	 */
	String title() default "title";

	/**
	 * 请求方法
	 *
	 * @return
	 */
	String method() default "POST";

	/**
	 * 描述
	 *
	 * @return
	 */
	String desc() default "description";

}
```

各字段默认取值如下：

| 字段名称 | 默认取值 | 备注 |
| :-----| ----: | :----: |
| path | 函数名 | 单元格 |
| catId | catText 对应的 id | catId优先级比 catText 高 |
|   catText    |   null    |       |
|  service     |   null    |       |
|   method    |   POST    |       |
|   title    |   方法的注释    |       |
|   desc    |   方法的注释    |       |

