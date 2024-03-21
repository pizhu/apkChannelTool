package pz.tools.apkchannel;


import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

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
import java.io.File;
import java.util.Locale;

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

    private File apkFile;
    private File channelsFile;

    public MainUI() {
        btnApkSelect.addActionListener(e -> {
            apkFile = choseFile(".apk", "APK Files (*.apk)");

            if (apkFile != null) {
                tfApkFile.setText(apkFile.getAbsolutePath());
            }
        });
        btnChannelSelect.addActionListener(e -> {
            channelsFile = choseFile(".txt", "TXT Files (*.apk)");
            if (channelsFile != null) {
                tfChannelsFile.setText(channelsFile.getAbsolutePath());
            }
        });

        btnGo.addActionListener(e -> {
            try {

                if (apkFile == null || channelsFile == null) {
                    JOptionPane.showMessageDialog(null, "请选择apk和渠道配置文件", "错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                File file = new File(this.getClass().getResource("/walle/walle-cli-all.jar").toURI());

                System.out.println("存在:"+file.exists()+";path"+file.getAbsolutePath());

                ProcessBuilder processBuilder = new ProcessBuilder();

                // 设置命令和参数
                processBuilder.command(
                        "java", "-jar", file.getAbsolutePath(), "batch", "-f",
                        channelsFile.getAbsolutePath(),
                        apkFile.getAbsolutePath());

                // 启动进程
                Process process = processBuilder.start();
                btnGo.setEnabled(false);

                int result = process.waitFor();
                btnGo.setEnabled(true);

                if (result == 0) {
                    JOptionPane.showMessageDialog(null, "打包成功", "消息", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "打包失败，未知错误.code="+result, "消息", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "打包失败:" + ex.getMessage(), "消息", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void show() {
        JFrame frame = new JFrame("远智多渠道打包工具");
        frame.setContentPane(new MainUI().mainPanel);
        frame.setSize(600, 400); // 设置窗口的初始大小
        frame.setMinimumSize(new Dimension(600, 400)); // 设置最小尺寸
        frame.setMaximumSize(new Dimension(600, 400)); // 设置最大尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
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
        mainPanel.setLayout(new GridLayoutManager(4, 4, new Insets(40, 20, 40, 20), -1, -1));
        mainPanel.setAutoscrolls(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        labelApk = new JLabel();
        Font labelApkFont = this.$$$getFont$$$(null, -1, 16, labelApk.getFont());
        if (labelApkFont != null) labelApk.setFont(labelApkFont);
        labelApk.setText("apk文件");
        mainPanel.add(labelApk, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfApkFile = new JTextField();
        tfApkFile.setBackground(new Color(-1));
        tfApkFile.setEditable(false);
        tfApkFile.setText("");
        tfApkFile.setToolTipText("");

        new DropTarget(tfApkFile, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
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
                                if (file.isFile()&&file.getName().toLowerCase().endsWith(".apk")) {
                                    apkFile=file;
                                    tfApkFile.setText(file.getAbsolutePath());
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


        mainPanel.add(tfApkFile, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 38), null, 0, false));
        btnApkSelect = new JButton();
        Font btnApkSelectFont = this.$$$getFont$$$("JetBrains Mono", -1, 18, btnApkSelect.getFont());
        if (btnApkSelectFont != null) btnApkSelect.setFont(btnApkSelectFont);
        btnApkSelect.setText("选择");
        mainPanel.add(btnApkSelect, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelChannels = new JLabel();
        Font labelChannelsFont = this.$$$getFont$$$(null, -1, 16, labelChannels.getFont());
        if (labelChannelsFont != null) labelChannels.setFont(labelChannelsFont);
        labelChannels.setText("渠道号配置文件");
        mainPanel.add(labelChannels, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfChannelsFile = new JTextField();
        tfChannelsFile.setBackground(new Color(-1));
        tfChannelsFile.setEditable(false);
        tfChannelsFile.setInheritsPopupMenu(false);
        tfChannelsFile.setMargin(new Insets(2, 6, 2, 6));
        tfChannelsFile.setOpaque(true);
        tfChannelsFile.setText("");

        new DropTarget(tfChannelsFile, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
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
                                if (file.isFile()&&file.getName().toLowerCase().endsWith(".txt")) {
                                    channelsFile=file;
                                    tfChannelsFile.setText(file.getAbsolutePath());
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

        mainPanel.add(tfChannelsFile, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 38), null, 0, false));
        btnChannelSelect = new JButton();
        Font btnChannelSelectFont = this.$$$getFont$$$("JetBrains Mono", -1, 18, btnChannelSelect.getFont());
        if (btnChannelSelectFont != null) btnChannelSelect.setFont(btnChannelSelectFont);
        btnChannelSelect.setText("选择");
        mainPanel.add(btnChannelSelect, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setEnabled(true);
        Font label1Font = this.$$$getFont$$$("Courier New", -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("<html>用法（支持拖动）：<br>1.选择已签名的apk <br>2.选择.txt格式的渠道号配置文件，示例格式：huawei # 华为 <br>3.多个渠道就换行添加<br><br><br></html>");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        jpanel_go = new JPanel();
        jpanel_go.setLayout(new GridLayoutManager(1, 1, new Insets(16, 0, 0, 0), -1, -1));
        mainPanel.add(jpanel_go, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnGo = new JButton();
        Font btnGoFont = this.$$$getFont$$$("JetBrains Mono", -1, 16, btnGo.getFont());
        if (btnGoFont != null) btnGo.setFont(btnGoFont);
        btnGo.setMargin(new Insets(0, 20, 0, 20));
        btnGo.setText("打包");
        jpanel_go.add(btnGo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
