package com.longqinx.xdevtool.ui;

import com.intellij.openapi.wm.WindowManager;
import com.longqinx.xdevtool.bean.Configuration;
import com.longqinx.xdevtool.bean.HostConfig;
import com.longqinx.xdevtool.common.HotSwapContext;
import com.longqinx.xdevtool.util.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * @author xiaos
 * @since 2023/10/17
 */
@SuppressWarnings("unchecked")
public class HotSwapConfirmDialog extends JDialog {
    private JComboBox<HostConfig> hostList;
    private JComboBox<String> serviceList;
    private JPanel contentPanel;
    private JButton okButton;
    private JButton cancelButton;

    private final HotSwapContext context;

    public HotSwapConfirmDialog(HotSwapContext context) {
        this.context = context;
        setContentPane(contentPanel);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        okButton.addActionListener(e -> {
            Object selectHost = hostList.getSelectedItem();
            Object serviceName = serviceList.getSelectedItem();
            if (selectHost == null || serviceName == null) {
                NotificationUtil.warn(this.context.getProject(), "choose nothing...");
                return;
            }
            context.setHostConfig((HostConfig) selectHost);
            context.setServiceName((String) serviceName);
            Configuration config = ConfigurationUtil.loadFromDisk(context.getProject());
            context.setJavaPath(config.getJavaPath());
            context.setArthasPath(config.getArthasPath());

            CompileUtil.compile(context.getProject(),
                    context.getTargetClass().getContainingFile().getVirtualFile(),
                    r -> HotSwapUtil.getInstance(context).doSwap(),
                    "Compile file failed");
//            HotSwapUtil.getInstance(context).doSwap();
            dispose();
        });
        cancelButton.addActionListener(e -> {
            dispose();
        });
    }

    public JPanel getPanel() {
        return contentPanel;
    }

    public void open() {
        Configuration configuration = ConfigurationUtil.loadFromDisk(context.getProject());
        List<HostConfig> hosts = configuration.getHostList();
        List<String> services = configuration.getServiceList();
        if (CollectionUtil.isEmpty(hosts) || CollectionUtil.isEmpty(services)) {
            NotificationUtil.warn(context.getProject(), "Please config hosts and services first");
            return;
        }
        hosts.forEach(h -> hostList.addItem(h));
        services.forEach(s -> serviceList.addItem(s));

        setTitle("Choose Target");
        pack();
        setLocationRelativeTo(WindowManager.getInstance().getFrame(context.getProject()));
        setVisible(true);
    }
}
