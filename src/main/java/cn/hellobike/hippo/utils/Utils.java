package cn.hellobike.hippo.utils;

import cn.hellobike.hippo.actions.UploadToYaPi;
import cn.hellobike.hippo.annotation.YaPiApiEntity;
import cn.hellobike.hippo.annotation.YaPiApiEntity;
import cn.hellobike.hippo.classloader.ReloadableClassLoader;
import cn.hellobike.hippo.classloader.SourceFileClassLoader;
import cn.hellobike.hippo.config.YaPiConfig;
import cn.hellobike.hippo.exception.YaPiException;
import cn.hellobike.hippo.json.ActionContext;
import cn.hellobike.hippo.yapi.entity.CategoryEntity;
import cn.hellobike.hippo.yapi.request.AddInterfaceRequest;
import cn.hellobike.hippo.yapi.response.GetInterfaceByIdResponse;
import cn.hellobike.hippo.yapi.service.YaPiService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.*;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.notification.*;
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
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

public class Utils {
    public static final Logger log = Logger.getInstance(Utils.class);


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

    public static int getRow(@NotNull Editor editor) {
        int row = editor.getCaretModel().getLogicalPosition().line;
        return row;
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

    public static String getPsiTypeJsonSchemaAsString(PsiFile psiFile, PsiType psiType, boolean wrap) throws ClassNotFoundException, IOException {
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

    public static void notify(final String message, NotificationType type) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(message, type);
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

    public static String assemble(char c, String... args) {
        if (args == null) {
            return null;
        }
        if (args.length == 1) {
            return trim(args[0], c);
        }
        StringBuffer buffer = new StringBuffer();
        for (String arg : args) {
            if (arg == null) {
                continue;
            }
            buffer.append(c);
            buffer.append(trim(arg, c));
        }
        return buffer.toString();
    }

    public static String getRelativePath(String... args) {
        return assemble('/', args);
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

    public static YaPiApiEntity getAnnotationEntityFromPsiAnnotation(PsiClass psiClass, PsiMethod psiMethod, PsiAnnotation psiAnnotation) {
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
        YaPiApiEntity annotationEntity = YaPiApiEntity.builder()
                .catId(catId)
                .catText(catText)
                .desc(desc)
                .method(method)
                .path(path)
                .title(title)
                .service(service)
                .build();
        YaPiApiEntity defaultAnnotationEntity = getDefaultAnnotationEntity(psiClass, psiMethod);
        BeanUtil.copyProperties(annotationEntity, defaultAnnotationEntity, CopyOptions.create().ignoreNullValue().ignoreCase().ignoreError());
        return defaultAnnotationEntity;
    }

    public static YaPiApiEntity getDefaultAnnotationEntity(PsiClass psiClass, PsiMethod psiMethod) {
        PsiAnnotation psiAnnotation = hasTargetAnnotation(psiClass.getAnnotations(), UploadToYaPi.PROJECT_TARGET_ANNOTATION);
        String value = getValueFromAnnotation(psiAnnotation, "value");
        PsiDocComment docComment = psiMethod.getDocComment();
        YaPiApiEntity entity = YaPiApiEntity.builder()
                .title(docComment == null ? "title" : docComment.toString())
                .method("POST")
                .desc(docComment == null ? "desc" : docComment.toString())
//                .catId("")
//                .catText()
                .path("/" + psiMethod.getName())
//                .service()
                .build();
        if (value != null) {
            entity.setPath("/" + trim(value, '/') + "/" + trim(entity.getPath(), '/'));
        }
        return entity;
    }

    public static YaPiApiEntity getDefaultAnnotationEntity(PsiAnnotation psiAnnotation) throws Exception {
        YaPiApiEntity entity = new YaPiApiEntity();
        if (psiAnnotation == null) {
            return entity;
        }
        for (PsiNameValuePair attribute : psiAnnotation.getParameterList().getAttributes()) {
            setValueIfExist(entity, attribute.getName(), attribute.getLiteralValue());
        }
        return entity;
    }

    /**
     * @param obj
     * @param key
     * @param value
     * @throws Exception
     */
    public static void setValueIfExist(Object obj, String key, Object value) throws Exception {
        if (value == null) {
            return;
        }
        Method method = getSetMethod(obj.getClass(), key);
        if (method == null) {
            return;
        }
        method.invoke(obj, value);
    }

    public static Method getSetMethod(Class cls, String fieldName) {
        return getMethod(cls, "set" + StringUtils.capitalize(fieldName));
    }

    public static Method getMethod(Class cls, String methodName) {
        for (Method method : cls.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static String trim(String target, char c) {
        if (target == null || target.length() == 0) {
            return "";
        }
        int left = 0, right = target.length() - 1;
        while (target.charAt(left) == c) {
            left++;
        }
        while (target.charAt(right) == c && right > left) {
            right--;
        }
        if (target.charAt(target.length() - 1) == c) {
            right--;
        }
        return target.substring(left, right + 1);
    }

    public static GetInterfaceByIdResponse getOrCreateInterface(YaPiApiEntity annotationEntity, YaPiConfig config, YaPiService yaPiService) throws YaPiException {
        AddInterfaceRequest addInterfaceRequest = new AddInterfaceRequest();
        BeanUtil.copyProperties(annotationEntity, addInterfaceRequest, CopyOptions.create().ignoreCase().ignoreNullValue());
        addInterfaceRequest.setProject_id(config.getProjectId());
        if (addInterfaceRequest.getCatid() == null && annotationEntity.getCatText() != null) {
            CategoryEntity categoryByTitle = yaPiService.getCategoryByTitle(config.getProjectId(), annotationEntity.getCatText());
            if (categoryByTitle != null) {
                addInterfaceRequest.setCatid(String.valueOf(categoryByTitle.get_id()));
            }
        }
        GetInterfaceByIdResponse interfaceOrCreate = yaPiService.getInterfaceOrCreate(addInterfaceRequest);
        return interfaceOrCreate;
    }

    public static String getDefaultPath(ActionContext actionContext) {
        YaPiConfig config = actionContext.getYaPiConfig();
        PsiClass psiClass = actionContext.getPsiClass();
        PsiJavaFile psiJavaFile = actionContext.getPsiJavaFile();
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        return getRelativePath(config.getPathPrefix(), lastSubString(psiJavaFile.getPackageName(), '.'), currentMethod.getName());
    }

    public static String getDefaultTitle(ActionContext actionContext) {
        YaPiConfig config = actionContext.getYaPiConfig();
        PsiClass psiClass = actionContext.getPsiClass();
        PsiJavaFile psiJavaFile = actionContext.getPsiJavaFile();
        PsiMethod currentMethod = actionContext.getCurrentMethod();
        String assemble = assemble('.', lastSubString(psiJavaFile.getPackageName(), '.'), psiClass.getName(), currentMethod.getName());
        PsiDocComment docComment = currentMethod.getDocComment();
        if (docComment == null) {
            assemble += ("(" + currentMethod.getName() + ")");
        } else {
            assemble += ("(" + docComment.getText() + ")");
        }
        return assemble;
    }

    public static String lastSubString(String s, char c) {
        int index = s.lastIndexOf(c);
        if (index == -1) {
            return s;
        }
        return s.substring(index + 1);
    }

    public static <T> T getAnnotationEntity(PsiAnnotation psiAnnotation, Class cls) throws Exception {
        Object obj = cls.getDeclaredConstructor().newInstance();
        if (psiAnnotation == null) {
            return (T) obj;
        }
        for (PsiNameValuePair attribute : psiAnnotation.getParameterList().getAttributes()) {
            setValueIfExist(obj, attribute.getAttributeName(), attribute.getLiteralValue());
        }
        return (T) obj;
    }

    public static String getSubStr(String s, char c) {
        if (s == null) {
            return null;
        }
        int c1 = s.lastIndexOf(c);
        if (c1 == -1) {
            return s;
        }
        return s.substring(0, c1);
    }
}
