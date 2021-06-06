package cn.hellobike.hippo.utils;

import cn.hellobike.hippo.annotation.YaPiAnnotationEntity;
import cn.hellobike.hippo.classloader.ReloadableClassLoader;
import cn.hellobike.hippo.classloader.SourceFileClassLoader;
import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.json.SimpleJsonNode;
import cn.hellobike.hippo.yapi.entity.CategoryEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import cn.hellobike.hippo.yapi.response.GetInterfaceByIdResponse;
import cn.hellobike.hippo.yapi.service.YaPiService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationGroupEP;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Utils {
	public static final Logger log = Logger.getInstance(Utils.class);

	public static String parseSimpleJsonNodeToString(SimpleJsonNode node) throws Exception {
		SourceFileClassLoader fileClassLoader = new SourceFileClassLoader("com.example.demo");
		Class<?> cls = fileClassLoader.loadClass("/Users/yuewenbo14971/frame-wp/test/target/classes/com/example/demo/bean/User.class");
		SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
		SchemaGeneratorConfig config = configBuilder.build();
		SchemaGenerator generator = new SchemaGenerator(config);

		JsonNode jsonSchema = generator.generateSchema(cls);
		System.out.println(jsonSchema.toString());
		return jsonSchema.toString();
	}

	public static String trimSubfix(String path) {
		int i = path.lastIndexOf(".");
		if (i == -1) {
			return path;
		}
		return path.substring(0, i);
	}

	public static String getFileName(String absPath) {
		int index = absPath.lastIndexOf("/");
		if (index == -1) {
			return absPath;
		}
		return absPath.substring(index + 1);
	}

	public static Class reloadClass(ClassLoader parent, String classPath, String qualifyName) throws ClassNotFoundException {
		ReloadableClassLoader classloader = new ReloadableClassLoader(parent, classPath);
		Class<?> cls = classloader.loadClass(qualifyName);
		return cls;

	}

	public static void compileJavaFile(String absolutePath, String targetDir) {

	}

	public static PsiClass getPsiClass(@NotNull Editor editor, @NotNull PsiFile psiFile) {
		PsiElement elementAt = getPsiElement(editor, psiFile);
		if (elementAt == null) {
			return null;
		} else {
			PsiClass target = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
			return target instanceof SyntheticElement ? null : target;
		}
	}

	public static PsiElement getPsiElement(@NotNull Editor editor, @NotNull PsiFile psiFile) {
		int offset = editor.getCaretModel().getOffset();
		return psiFile.findElementAt(offset);
	}

	public static PsiElement getVisualPsiElement(@NotNull Editor editor, @NotNull PsiFile psiFile) {
		return psiFile.findElementAt(getVisualOfLineOffset(editor));
	}

	public static int getStartOfLineOffset(@NotNull Editor editor) {
		int column = editor.getCaretModel().getLogicalPosition().column;
		return editor.getCaretModel().getOffset() - column;
	}

	public static PsiElement getPsiElement(@NotNull PsiFile psiFile, int offset) {
		return psiFile.findElementAt(offset);
	}

	public static int getVisualOfLineOffset(@NotNull Editor editor) {
		int startOfLineOffset = getStartOfLineOffset(editor);
		int column = editor.getCaretModel().getVisualPosition().column;
		return startOfLineOffset + column - 1;
	}

	public static PsiMethod getPsiMethod(@NotNull PsiElement element) {
		PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
				PsiTreeUtil.getParentOfType(element, PsiMethod.class);
		if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) {
			return getPsiMethod(method.getParent());
		}
		return method;
	}

	public static void showErrorHint(Editor editor, String msg) {
		HintManager.getInstance().showErrorHint(editor, msg);
	}

	public static PsiMethod getCurrentLineMethod(Editor editor, PsiFile psiFile) {
		PsiMethod psiMethod = null;
		int visualOfLineOffset = getVisualOfLineOffset(editor);
		int startOfLineOffset = getStartOfLineOffset(editor);
		for (int offset : new int[]{visualOfLineOffset, startOfLineOffset}) {
			PsiElement psiElement = getPsiElement(psiFile, offset);
			if (psiElement != null) {
				psiMethod = getPsiMethod(psiElement);
			}
			if (psiMethod != null) {
				return psiMethod;
			}
		}
		return null;
	}

	public static String getPsiTypeJsonSchemaAsString(Project project, Editor editor, PsiFile psiFile, PsiClass psiClass, PsiMethod psiMethod, PsiType psiType, boolean wrap) throws ClassNotFoundException, IOException {
		PsiClass paramClass = PsiTypesUtil.getPsiClass(psiType);
		String qualifiedName = paramClass.getQualifiedName();
		Module module = ModuleUtil.findModuleForFile(psiFile);
		String moduleMainSourceRoot = null;
		VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
		if (contentRoots == null || contentRoots.length < 1) {
			return null;
		}
		moduleMainSourceRoot = Paths.get(contentRoots[0].getPath(), "target", "classes").toString();

		if (moduleMainSourceRoot == null) {
			return null;
		}
		log.info("root:" + moduleMainSourceRoot);
		log.info("qualifyName:" + qualifiedName);
		ReloadableClassLoader classLoader = new ReloadableClassLoader(Utils.class.getClassLoader(), moduleMainSourceRoot);
		Class<?> cls = classLoader.loadClass(qualifiedName);
		if (!wrap) {
			return getJsonSchemaAsString(cls);
		}
		Class wrappedClass = classLoader.getWrappedClass(qualifiedName);
		return getJsonSchemaAsString(wrappedClass);
	}

	public static String getJsonSchemaAsString(Class cls) {
		SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
		SchemaGeneratorConfig config = configBuilder.build();
		SchemaGenerator generator = new SchemaGenerator(config);
		return generator.generateSchema(cls).toString();
	}

	public static String getMsgFromHttpResponse(HttpResponse response) {
		if (response == null || response.body() == null) {
			return "null";
		}
		JSONObject jsonObject = JSONObject.parseObject(response.body());
		if (jsonObject == null) {
			return "null";
		}
		String errmsg = jsonObject.getString("errmsg");
		return errmsg;
	}

	public static final NotificationGroup GROUP_DISPLAY_ID_INFO =
			new NotificationGroup("My notification group",
					NotificationDisplayType.BALLOON, true);

	public static void notify(final String message) {
		ApplicationManager.getApplication().invokeLater(() -> {
			Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(message, NotificationType.ERROR);
			Project[] projects = ProjectManager.getInstance().getOpenProjects();
			Notifications.Bus.notify(notification, projects[0]);
		});
	}

	public static String getValueFromAnnotation(PsiAnnotation annotation, String key) {
		if (annotation == null) {
			return null;
		}
		for (PsiNameValuePair attribute : annotation.getParameterList().getAttributes()) {
			if (attribute.getAttributeName().equals(key)) {
				return attribute.getLiteralValue();
			}
		}
		return null;
	}

	public static PsiAnnotation hasTargetAnnotation(PsiAnnotation[] annotations, String qualifyName) {
		if (annotations == null) {
			return null;
		}
		for (PsiAnnotation annotation : annotations) {
			if (annotation.getQualifiedName().equals(qualifyName)) {
				return annotation;
			}
		}
		return null;
	}

	public static YaPiAnnotationEntity getAnnotationEntityFromPsiAnnotation(PsiAnnotation psiAnnotation) {
		if (psiAnnotation == null) {
			return null;
		}
		String path = Utils.getValueFromAnnotation(psiAnnotation, "path");
		String catId = Utils.getValueFromAnnotation(psiAnnotation, "catId");
		String catText = Utils.getValueFromAnnotation(psiAnnotation, "catText");
		String service = Utils.getValueFromAnnotation(psiAnnotation, "service");
		String title = Utils.getValueFromAnnotation(psiAnnotation, "title");
		String method = Utils.getValueFromAnnotation(psiAnnotation, "method");
		String desc = Utils.getValueFromAnnotation(psiAnnotation, "desc");
		YaPiAnnotationEntity annotationEntity = YaPiAnnotationEntity.builder()
				.catId(catId)
				.catText(catText)
				.desc(desc)
				.method(method)
				.path(path)
				.title(title)
				.service(service)
				.build();
		return annotationEntity;
	}

	public static GetInterfaceByIdResponse getOrCreateInterface(YaPiAnnotationEntity annotationEntity, YaPiConfig config, YaPiService yaPiService) {
		AddInterfaceRequest addInterfaceRequest = new AddInterfaceRequest();
		BeanUtil.copyProperties(annotationEntity, addInterfaceRequest, CopyOptions.create().ignoreCase().ignoreNullValue());

		if (addInterfaceRequest.getCatid() == null && annotationEntity.getCatText() != null) {
			CategoryEntity categoryByTitle = yaPiService.getCategoryByTitle(config.getProjectId(), annotationEntity.getCatText());
			if (categoryByTitle != null) {
				addInterfaceRequest.setCatid(String.valueOf(categoryByTitle.get_id()));
			}
		}
		GetInterfaceByIdResponse interfaceOrCreate = yaPiService.getInterfaceOrCreate(addInterfaceRequest);
		return interfaceOrCreate;
	}

}
