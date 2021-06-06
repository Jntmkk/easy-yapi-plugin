package cn.hellobike.hippo.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class DemoAction extends AnAction {
	public static final Logger log = Logger.getInstance(DemoAction.class);
	public static final NotificationGroup GROUP_DISPLAY_ID_INFO =
			new NotificationGroup("My notification group",
					NotificationDisplayType.BALLOON, true);
	@Override
	public void actionPerformed(AnActionEvent e) {
		// TODO: insert action logic here
		String message = "hello world";
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(message, NotificationType.ERROR);
				Project[] projects = ProjectManager.getInstance().getOpenProjects();
				Notifications.Bus.notify(notification, projects[0]);
			}
		});
	}
}
