package pz.tools.apkchannel;

import com.google.gson.Gson;

import java.io.*;

public class ConfigHelper {

    private static final Gson gson = new Gson();
    private static final File configFile = new File("config.txt");

    public static ConfigBean read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            StringBuilder sb = new StringBuilder();

            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");  // 逐行读取并拼接内容
            }

            ConfigBean config = gson.fromJson(sb.toString(), ConfigBean.class);
            return config;

        } catch (Exception e) {
            return null;
        }
    }

    public static void save(ConfigBean configBean) {
        FileWriter fileWriter=null;
        try {
            System.out.println("配置文件:"+configFile.getAbsolutePath());
            configFile.createNewFile();

            fileWriter = new FileWriter(configFile);
            fileWriter.write(gson.toJson(configBean));
            fileWriter.flush();
        } catch (Exception e) {

        }finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
