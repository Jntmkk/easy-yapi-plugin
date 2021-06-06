package cn.hellobike.hippo.actions;

import cn.hellobike.hippo.annotation.YaPiAnnotationEntity;
import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.utils.Utils;
import cn.hellobike.hippo.yapi.entity.CategoryEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.request.UpdateInterfaceRequest;
import cn.hellobike.hippo.yapi.response.GetInterfaceByIdResponse;
import cn.hellobike.hippo.yapi.response.UpdateInterfaceResponse;
import cn.hellobike.hippo.yapi.service.YaPiService;
import cn.hellobike.hippo.yapi.service.YaPiServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import lombok.SneakyThrows;
import org.codehaus.groovy.ast.tools.BeanUtils;

import java.io.IOException;

public class UploadToYaPi extends AnAction {
	public static Logger log = Logger.getInstance(UploadToYaPi.class);
	public static final String TARGET_ANNOTATION = "cn.hellobike.hippo.annotation.YaPiApi";

	@SneakyThrows
	@Override
	public void actionPerformed(AnActionEvent e) {

		YaPiConfig yaPiConfig = checkConfig();
		if (yaPiConfig == null) {
			return;
		}
		// TODO: insert action logic here
		ClassLoader parent = getClass().getClassLoader();
		Project project = e.getProject();
		if (parent == null) {
			return;
		}
		Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
		PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
		PsiClass psiClass = Utils.getPsiClass(editor, psiFile);
		if (psiFile == null || psiClass == null) {
			Utils.showErrorHint(editor, "只针对 Java 源码使用！");
			return;
		}
		log.info(psiClass.toString() + "-----" + psiFile.toString());
		PsiMethod psiMethod = Utils.getCurrentLineMethod(editor, psiFile);
		if (psiMethod == null) {
			HintManager.getInstance().showInformationHint(editor, "当前行未检测到方法");
			return;
		}

		YaPiService service = new YaPiServiceImpl(yaPiConfig.getUrl(), yaPiConfig.getToken());
		// 先判断根据 path 判断是否已存在接口，不存在则创建，否则更新

		// 先获取注解信息
		YaPiAnnotationEntity annotationEntity = getAnnotationEntity(editor, psiMethod);
		if (annotationEntity == null) {
			Utils.showErrorHint(editor, "获取注解失败!");
			return;
		}
		// 先获得接口基本信息，根据 path 唯一性判断，若未指定

		GetInterfaceByIdResponse interfaceByIdResponse = Utils.getOrCreateInterface(annotationEntity, yaPiConfig, service);

		UpdateInterfaceRequest updateRequest = cn.hellobike.hippo.yapi.Utils.mapGetResponseToUpdateRequest(interfaceByIdResponse.getData());

		applyMethodReqAndResBody(project, editor, psiFile, psiClass, psiMethod, updateRequest);

		UpdateInterfaceResponse updateInterfaceResponse = service.updateInterface(updateRequest);
		if (updateInterfaceResponse.getErrcode() == 0) {
			Utils.notify("更新成功");
		} else {
			Utils.notify(updateInterfaceResponse.getErrmsg());
		}

	}


	public YaPiAnnotationEntity getAnnotationEntity(Editor editor, PsiMethod psiMethod) {
		PsiAnnotation[] annotations = psiMethod.getAnnotations();
		PsiAnnotation psiAnnotation = Utils.hasTargetAnnotation(annotations, TARGET_ANNOTATION);
		if (psiAnnotation == null) {
			Utils.showErrorHint(editor, "未使用 @YaPiApi 注解？");
			return null;
		}
		return Utils.getAnnotationEntityFromPsiAnnotation(psiAnnotation);
	}


	private String getCatIdByTitleOrDefault(YaPiService yaPiService, YaPiConfig yaPiConfig, String title) {
		if (title == null) {
			title = "公共分类";
		}
		CategoryEntity categoryByTitle = yaPiService.getCategoryByTitle(yaPiConfig.getProjectId(), title);
		return String.valueOf(categoryByTitle.get_id());
	}

	private YaPiConfig checkConfig() {
//		YaPiConfigService service = YaPiConfigService.getConfigService();
//		if (service == null) {
//			Utils.notify("获取config service 失败");
//			return null;
//		}
		YaPiConfig config = YaPiConfig.getConfig();
		if (config == null) {
			Utils.notify("获取config失败");
			return null;
		}
		if (config.getUrl() == null || config.getProjectId() == null || config.getToken() == null) {
			Utils.notify("请先配置 token、projectID、URL");
			return null;
		}
		return config;
	}

	private boolean applyMethodReqAndResBody(Project project, Editor editor, PsiFile psiFile, PsiClass psiClass, PsiMethod psiMethod, UpdateInterfaceRequest request) throws ClassNotFoundException, IOException {
		PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
		if (parameters.length > 1) {
			HintManager.getInstance().showErrorHint(editor, "只支持一个参数，请将参数用 POJO 包装！！！");
			return true;
		}
		for (PsiParameter parameter : parameters) {
			String jsonSchema = Utils.getPsiTypeJsonSchemaAsString(project, editor, psiFile, psiClass, psiMethod, parameter.getType(), false);
			request.setReq_body_other(jsonSchema);
			request.setReq_body_is_json_schema(Boolean.TRUE);
		}
		String returnJsonSchema = Utils.getPsiTypeJsonSchemaAsString(project, editor, psiFile, psiClass, psiMethod, psiMethod.getReturnType(), true);
		request.setRes_body(returnJsonSchema);
		request.setRes_body_is_json_schema(Boolean.TRUE);
		return false;
	}
}
