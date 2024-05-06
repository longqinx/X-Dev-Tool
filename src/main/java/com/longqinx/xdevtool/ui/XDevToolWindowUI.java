package com.longqinx.xdevtool.ui;

import com.intellij.openapi.project.Project;
import com.longqinx.xdevtool.bean.Configuration;
import com.longqinx.xdevtool.bean.HostConfig;
import com.longqinx.xdevtool.util.CollectionUtil;
import com.longqinx.xdevtool.util.ConfigurationUtil;
import com.longqinx.xdevtool.util.NotificationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.stream.Collectors;

public class XDevToolWindowUI {
    private JPanel contentPane;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JButton configAddButton;
    private JTable confInfoTable;
    private JTextField serviceNameField;
    private JButton serviceAddButton;
    private JTable serviceNameTable;
    private JTextField arthasPathField;
    private JTextField javaPathField;
    private JButton updatePathButton;
    private JButton flushButton;


    private final Project project;
    private Configuration configuration;
    private final Vector<Vector<String>> hostListData = new Vector<>();
    private final Vector<Vector<String>> serviceListData = new Vector<>();

    public XDevToolWindowUI(Project project) {
        this.project = project;
        configuration = ConfigurationUtil.loadFromDisk(this.project);
        loadData();

        confInfoTable.setModel(getTableModel());
        serviceNameTable.setModel(getServiceTableModel());
        javaPathField.setToolTipText("Full path of Java executable file, not Java home");
        arthasPathField.setToolTipText("Full path of Arthas executable jar file");
        addressField.setToolTipText("Input format : user@ip:port");


        configAddButton.addActionListener(e -> onHostAddBtnClick());
        serviceAddButton.addActionListener(e -> onServiceAddBtnClick());
        updatePathButton.addActionListener(e -> onUpdatePathBtnClick());
        flushButton.addActionListener(e -> onFlushBtnClick());
    }

    private void loadData() {
        if (this.configuration == null) {
            this.configuration = ConfigurationUtil.loadFromDisk(this.project);
        }
        List<HostConfig> hostList = configuration.getHostList();
        if (!CollectionUtil.isEmpty(hostList)) {
            hostListData.clear();
            hostList.forEach(host -> {
                Vector<String> vec = new Vector<>(4);
                vec.add(host.getIp());
                vec.add(host.getPort());
                vec.add(host.getUsername());
                vec.add(host.getPassword());
                hostListData.add(vec);
            });
        }
        List<String> serviceList = configuration.getServiceList();
        if (!CollectionUtil.isEmpty(serviceList)) {
            serviceListData.clear();
            serviceList.forEach(service -> {
                Vector<String> vec = new Vector<>(1);
                vec.add(service);
                serviceListData.add(vec);
            });
        }
        javaPathField.setText(configuration.getJavaPath());
        arthasPathField.setText(configuration.getArthasPath());
    }

    private void updateHosts() {
        if (this.configuration == null) {
            return;
        }
        List<HostConfig> hosts = new ArrayList<>();
        for (Vector<String> eachLine : hostListData) {
            hosts.add(new HostConfig(eachLine));
        }
        configuration.setHostList(hosts);
        ConfigurationUtil.update(this.project, configuration);
    }

    public JPanel getContent() {
        return contentPane;
    }

    private TableModel getTableModel() {
        return new DefaultTableModel(hostListData, new Vector<>(Arrays.asList("IP", "Port", "Username", "Password")));
    }

    private TableModel getServiceTableModel() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(serviceListData, new Vector<>(List.of("Service Name")));
        return tableModel;
    }

    private void onHostAddBtnClick() {
        String addrStr = addressField.getText();
        if (addrStr == null || !addrStr.contains("@")) {
            NotificationUtil.info(project, "Wrong input format");
            return;
        }
        int atIndex = addrStr.lastIndexOf('@');
        String username = addrStr.substring(0, atIndex);
        String ip;
        String port = "22";
        int commaIndex = addrStr.lastIndexOf(':');
        if (commaIndex != -1) {
            ip = addrStr.substring(atIndex + 1, commaIndex);
            port = addrStr.substring(commaIndex + 1);
        } else {
            ip = addrStr.substring(atIndex + 1);
        }

        String password = new String(passwordField.getPassword());

        hostListData.add(new Vector<>(Arrays.asList(ip, port, username, password)));

        confInfoTable.updateUI();

        updateHosts();
    }

    private void onServiceAddBtnClick() {
        serviceListData.add(new Vector<>(Collections.singletonList(serviceNameField.getText())));
        serviceNameTable.updateUI();

        List<String> services = serviceListData.stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (configuration != null) {
            configuration.setServiceList(services);
            ConfigurationUtil.update(this.project, configuration);
        }
    }

    private void onUpdatePathBtnClick() {
        if (configuration != null) {
            configuration.setJavaPath(javaPathField.getText());
            configuration.setArthasPath(arthasPathField.getText());
            ConfigurationUtil.update(this.project, configuration);
        }
    }

    private void onFlushBtnClick() {
        configuration = ConfigurationUtil.loadFromDisk(this.project);
        loadData();
        confInfoTable.updateUI();
        serviceNameTable.updateUI();
    }
}
