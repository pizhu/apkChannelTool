package pz.tools.apkchannel;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.meituan.android.walle.WalleCommandLine;
import com.meituan.android.walle.commands.BatchCommand;
import com.meituan.android.walle.commands.IWalleCommand;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainUI {
    private JTextField tfApkFile;
    private JButton btnApkSelect;
    private JTextField tfChannelsFile;
    private JButton btnChannelSelect;
    private JButton btnGo;
    private JPanel mainPanel;
    private JLabel labelApk;
    private JLabel labelChannels;
    private JPanel jpanel_go;
    private JTextField tfKeystoreFile;
    private JButton btnKeystoreSelect;
    private JTextField tfKey;
    private JTextField tfAlikey;
    private JLabel label_key;
    private JLabel label_ali_key;

    private File apkFile;
    private File channelsFile;

    private File keyStoreFile;

    public MainUI() {
        btnApkSelect.addActionListener(e -> {
            apkFile = choseFile(".apk", "APK Files (*.apk)");

            if (apkFile != null) {
                tfApkFile.setText(apkFile.getAbsolutePath());
            }
        });
        btnChannelSelect.addActionListener(e -> {
            channelsFile = choseFile(".txt", "TXT Files (*.txt)");
            if (channelsFile != null) {
                tfChannelsFile.setText(channelsFile.getAbsolutePath());
            }
        });

        btnKeystoreSelect.addActionListener(e -> {
            channelsFile = choseFile(".jks", "Keystore Files (*.jks)");
            if (channelsFile != null) {
                tfChannelsFile.setText(channelsFile.getAbsolutePath());
            }
        });

        btnGo.addActionListener(e -> {
            try {

                if (apkFile == null) {
                    JOptionPane.showMessageDialog(null, "请选择apk文件", "错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                build();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "打包失败:" + ex.getMessage(), "消息", JOptionPane.WARNING_MESSAGE);
            }
        });

        initDropActionListener();
    }

    /**
     * 设置拖动文件到文本框
     */
    private void initDropActionListener() {
        setDropTarget(tfApkFile, ".apk", file -> {
            apkFile = file;
        });
        setDropTarget(tfChannelsFile, ".txt", file -> {
            channelsFile = file;
        });
        setDropTarget(tfKeystoreFile, ".jks", file -> {
            keyStoreFile = file;
        });

    }

    private void setDropTarget(JTextField textField, String suffix, FileSelectProcessor selectProcessor) {
        new DropTarget(textField, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    // 检查是否有文件被拖进来
                    Transferable transferable = dtde.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    for (DataFlavor flavor : flavors) {
                        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            @SuppressWarnings("unchecked")
                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
                            if (files.size() == 1) {
                                File file = files.get(0);
                                if (file.isFile() && file.getName().toLowerCase().endsWith(suffix)) {
                                    selectProcessor.selected(file);
                                    textField.setText(file.getAbsolutePath());
                                }
                            }
                            dtde.dropComplete(true);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dtde.rejectDrop();
            }
        });
    }

    public void show() {
        JFrame frame = new JFrame("远智多渠道打包工具");
        frame.setContentPane(new MainUI().mainPanel);
        frame.setSize(600, 480); // 设置窗口的初始大小
        frame.setMinimumSize(new Dimension(600, 480)); // 设置最小尺寸
        frame.setMaximumSize(new Dimension(600, 480)); // 设置最大尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void build() {
        try {
            File signFile = signBuild();

            if (signFile != null) {
                channelsBuild(signFile);
            } else {
                channelsBuild(apkFile);
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * 签名apk
     *
     * @return 签名后的文件
     */
    private File signBuild() throws Exception {
        if (keyStoreFile == null || tfKey.getText().isEmpty() || tfAlikey.getText().isEmpty()) {
            return null;
        }

        String keyPwd = tfKey.getText();
        String keyAlias = tfAlikey.getText();
        File apksignerFile = new File("tools/apksigner.jar");

        File signFile = new File(apkFile.getAbsolutePath().replace(".apk", "_sign.apk"));

        try {
            // 构建命令行参数
            ProcessBuilder pb = new ProcessBuilder(
                    apksignerFile.getAbsolutePath(),
                    "sign",
                    "--v1-signing-enabled", "true",
                    "--v2-signing-enabled", "true",
                    "--ks", keyStoreFile.getAbsolutePath(),
                    "--ks-pass", "pass:" + keyPwd,
                    "--key-pass", "pass:" + keyAlias,
                    "--out", signFile.getAbsolutePath(), // 设置签名输出路径
                    apkFile.getAbsolutePath()
            );

            // 启动进程
            Process process = pb.start();
            // 读取进程的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }
            // 等待命令执行完成
            int exitCode = process.waitFor();
            // 检查进程的退出状态
            if (exitCode == 0) {
                return signFile;
            } else {
                JOptionPane.showMessageDialog(null, "签名失败，执行命令失败" + sb, "错误", JOptionPane.WARNING_MESSAGE);
                throw new RuntimeException("execute fail");
            }
        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(null, "签名失败，error:" + e.getMessage(), "错误", JOptionPane.WARNING_MESSAGE);
            throw e;
        }

    }


    /**
     * 多渠道打包
     */
    private void channelsBuild(File sourceApk) {
        if (channelsFile == null) {
            JOptionPane.showMessageDialog(null, "渠道配置文件不能为空", "消息", JOptionPane.WARNING_MESSAGE);
            return;
        }


        btnGo.setEnabled(false);
        Map<String, IWalleCommand> subCommandList = new HashMap();
        subCommandList.put("batch", new BatchCommand());
        WalleCommandLine walleCommandLine = new WalleCommandLine();
        JCommander commander = new JCommander(walleCommandLine);
        Iterator var4 = subCommandList.entrySet().iterator();

        while (var4.hasNext()) {
            Map.Entry<String, IWalleCommand> commandEntry = (Map.Entry) var4.next();
            commander.addCommand((String) commandEntry.getKey(), commandEntry.getValue());
        }

        try {
            commander.parse("batch", "-f", channelsFile.getAbsolutePath(), sourceApk.getAbsolutePath());
        } catch (ParameterException var6) {
            System.out.println(var6.getMessage());
            commander.usage();
            JOptionPane.showMessageDialog(null, "打包失败,error：" + var6.getMessage(), "消息", JOptionPane.WARNING_MESSAGE);
            return;
        }

        walleCommandLine.parse(commander);
        String parseCommand = commander.getParsedCommand();
        if (parseCommand != null) {
            ((IWalleCommand) subCommandList.get(parseCommand)).parse();
        }

        JOptionPane.showMessageDialog(null, "打包成功", "消息", JOptionPane.INFORMATION_MESSAGE);
        btnGo.setEnabled(true);
    }


    private File choseFile(String suffix, String description) {
        JFileChooser apkFileChooser = new JFileChooser();
        apkFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(suffix);
            }

            @Override
            public String getDescription() {
                return description;
            }
        });


        int result = apkFileChooser.showOpenDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            return apkFileChooser.getSelectedFile();
        }

        return null;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 6, new Insets(40, 20, 40, 20), -1, -1));
        mainPanel.setAutoscrolls(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tfApkFile = new JTextField();
        tfApkFile.setBackground(new Color(-263173));
        tfApkFile.setEditable(false);
        tfApkFile.setText("");
        tfApkFile.setToolTipText("");
        mainPanel.add(tfApkFile, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 38), null, 0, false));
        btnApkSelect = new JButton();
        Font btnApkSelectFont = this.$$$getFont$$$("JetBrains Mono", -1, 18, btnApkSelect.getFont());
        if (btnApkSelectFont != null) btnApkSelect.setFont(btnApkSelectFont);
        btnApkSelect.setText("选择");
        mainPanel.add(btnApkSelect, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelChannels = new JLabel();
        Font labelChannelsFont = this.$$$getFont$$$(null, -1, 16, labelChannels.getFont());
        if (labelChannelsFont != null) labelChannels.setFont(labelChannelsFont);
        labelChannels.setText("渠道号配置文件");
        mainPanel.add(labelChannels, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfChannelsFile = new JTextField();
        tfChannelsFile.setBackground(new Color(-263173));
        tfChannelsFile.setEditable(false);
        tfChannelsFile.setInheritsPopupMenu(false);
        tfChannelsFile.setMargin(new Insets(2, 6, 2, 6));
        tfChannelsFile.setOpaque(true);
        tfChannelsFile.setText("");
        mainPanel.add(tfChannelsFile, new GridConstraints(5, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 38), null, 0, false));
        btnChannelSelect = new JButton();
        Font btnChannelSelectFont = this.$$$getFont$$$("JetBrains Mono", -1, 18, btnChannelSelect.getFont());
        if (btnChannelSelectFont != null) btnChannelSelect.setFont(btnChannelSelectFont);
        btnChannelSelect.setText("选择");
        mainPanel.add(btnChannelSelect, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setEnabled(true);
        Font label1Font = this.$$$getFont$$$("Courier New", -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("<html>用法（支持拖动）：<br>1.选择已签名的apk <br>2.选择.txt格式的渠道号配置文件，示例格式：huawei # 华为 <br>3.多个渠道就换行添加<br><br><br></html>");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        jpanel_go = new JPanel();
        jpanel_go.setLayout(new GridLayoutManager(1, 1, new Insets(16, 0, 0, 0), -1, -1));
        mainPanel.add(jpanel_go, new GridConstraints(6, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnGo = new JButton();
        Font btnGoFont = this.$$$getFont$$$("JetBrains Mono", -1, 16, btnGo.getFont());
        if (btnGoFont != null) btnGo.setFont(btnGoFont);
        btnGo.setMargin(new Insets(0, 20, 0, 20));
        btnGo.setText("打包");
        jpanel_go.add(btnGo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelApk = new JLabel();
        Font labelApkFont = this.$$$getFont$$$(null, -1, 16, labelApk.getFont());
        if (labelApkFont != null) labelApk.setFont(labelApkFont);
        labelApk.setText("apk文件");
        mainPanel.add(labelApk, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, 16, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("签名文件");
        mainPanel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnKeystoreSelect = new JButton();
        Font btnKeystoreSelectFont = this.$$$getFont$$$("JetBrains Mono", -1, 18, btnKeystoreSelect.getFont());
        if (btnKeystoreSelectFont != null) btnKeystoreSelect.setFont(btnKeystoreSelectFont);
        btnKeystoreSelect.setText("选择");
        mainPanel.add(btnKeystoreSelect, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfKeystoreFile = new JTextField();
        tfKeystoreFile.setBackground(new Color(-263173));
        tfKeystoreFile.setEditable(false);
        tfKeystoreFile.setEnabled(true);
        mainPanel.add(tfKeystoreFile, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 38), null, 0, false));
        label_key = new JLabel();
        Font label_keyFont = this.$$$getFont$$$(null, -1, 14, label_key.getFont());
        if (label_keyFont != null) label_key.setFont(label_keyFont);
        label_key.setText("密钥");
        mainPanel.add(label_key, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfKey = new JTextField();
        mainPanel.add(tfKey, new GridConstraints(3, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
        label_ali_key = new JLabel();
        Font label_ali_keyFont = this.$$$getFont$$$(null, -1, 14, label_ali_key.getFont());
        if (label_ali_keyFont != null) label_ali_key.setFont(label_ali_keyFont);
        label_ali_key.setText("别名密码");
        mainPanel.add(label_ali_key, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfAlikey = new JTextField();
        mainPanel.add(tfAlikey, new GridConstraints(4, 2, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 30), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
