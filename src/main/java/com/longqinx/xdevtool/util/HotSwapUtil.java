package com.longqinx.xdevtool.util;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiFile;
import com.jcraft.jsch.*;
import com.longqinx.xdevtool.bean.HostConfig;
import com.longqinx.xdevtool.common.HotSwapContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.longqinx.xdevtool.util.SSHUtil.*;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class HotSwapUtil {
    private final HotSwapContext context;

    private HotSwapUtil(HotSwapContext context) {
        this.context = context;
    }

    public static HotSwapUtil getInstance(HotSwapContext context) {
        return new HotSwapUtil(context);
    }

    public void doSwap() {
        PsiFile compiledFile = CompileOutputUtil.getCompiledClassFileOf(context.getTargetClass());
        if (compiledFile == null) {
            NotificationUtil.warn(context.getProject(), "Please compile the project first.");
            return;
        }
        String classFilePath = compiledFile.getVirtualFile().getPath();
        String classFileName = compiledFile.getName();

        ProgressManager.getInstance().run(new Task.Backgroundable(context.getProject(), "Hot swap task") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                uploadAndRetransform(indicator, classFilePath, classFileName);
            }
        });
    }

    /**
     * TODO:支持Docker容器中替换
     */
    public void uploadAndRetransform(ProgressIndicator indicator, String classFilePath, String classFileName) {
        HostConfig hostConfig = context.getHostConfig();
        if (StringUtils.isBlank(context.getJavaPath()) || StringUtils.isBlank(context.getArthasPath())) {
            NotificationUtil.error(context.getProject(), "Java or Arthas path is empty !");
        }
        try {
            indicator.setText("Upload file...");//---------------------------------------------
            //创建ssh session
            Session session = createNewSession(hostConfig);
            //建临时文件夹
            exec(session, "mkdir -p /home/arthas-temp");
            //传输编译后的文件
            uploadFile(session, classFilePath, "/home/arthas-temp");

            indicator.setFraction(0.5);//------------------------------------------------------
            indicator.setText("Do retransform");//---------------------------------------------

            //调用arthas替换
            String cmd = "%s -jar %s --select %s -c \"retransform /home/arthas-temp/%s\" 2>&1";
            cmd = String.format(cmd, context.getJavaPath(), context.getArthasPath(), context.getServiceName(), classFileName);

            int resultCode = execAndGetReTransformResult(session, cmd);
            displayResult(resultCode, classFileName);

            indicator.setFraction(1.0);//------------------------------------------------------
            session.disconnect();
        } catch (Exception e) {
            NotificationUtil.error(context.getProject(), e.getMessage());
        }
    }

    private static final Map<String, Integer> ERROR_CODE_MAP = new HashMap<>() {{
        put("retransform success", 0);
        put("[ERROR] The telnet port 3658 is used by process", -1);
        put("These classes are not found in the JVM and may not be loaded", -2);
        put("[INFO] Found existing java process, please choose one", -3);
        put("retransform error! java.lang.UnsupportedOperationException", -4);
    }};

    private static int execAndGetReTransformResult(Session session, String cmd) throws JSchException, IOException {
        ChannelExec shell = (ChannelExec) session.openChannel("exec");
        InputStream inputStream = shell.getInputStream();
        shell.setCommand(cmd);
        shell.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            for (Map.Entry<String, Integer> entry : ERROR_CODE_MAP.entrySet()) {
                if (line.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        shell.disconnect();
        return -111;
    }

    private void displayResult(int resultCode, String classFileName) {
        switch (resultCode) {
            case 0:
                NotificationUtil.info(context.getProject(), String.format("Swap %s finished.", classFileName));
                break;
            case -1:
                //arthas端口占用错误
                NotificationUtil.error(context.getProject(), "Arthas telnet port 3658 is used, please fix it first.");
                break;
            case -2:
                //类未找到
                NotificationUtil.error(context.getProject(), String.format("Class %s not found in %s", classFileName, context.getServiceName()));
                break;
            case -3:
                //找不到服务
                NotificationUtil.error(context.getProject(), String.format("Cannot found service on target host: %s", context.getServiceName()));
                break;
            case -4:
                //不支持修改
                NotificationUtil.error(context.getProject(), "Does not support modifying class field or changing method signature.");
                break;
            default:
                NotificationUtil.error(context.getProject(), "Unknown error, please check your configuration and try again later.");
                break;
        }
    }


}
