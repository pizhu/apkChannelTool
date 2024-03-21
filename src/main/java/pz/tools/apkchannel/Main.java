package pz.tools.apkchannel;


import java.io.File;
import java.net.URL;

public class Main {
    public static void main(String[] args) {

        URL url=Main.class.getResource("/walle/walle-cli-all.jar");

        System.out.println(new File(url.getFile()).getAbsolutePath());

/*        MainUI mainUI = new MainUI();
        mainUI.show();*/
    }
}