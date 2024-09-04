package pz.tools.apkchannel;

public class ConfigBean {

    /**
     * 签名文件路径
     */
    private String keystoreFile;
    /**
     * 密钥
     */
    private String keyPwd;
    /**
     * 别名密钥
     */
    private String aliasPwd;
    /**
     * 密钥别名
     */
    private String keyAlias;

    /**
     * 渠道文件
     */
    private String channelsFile;

    private Boolean isSign=true;
    private Boolean isChannel=true;

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeyPwd() {
        return keyPwd;
    }

    public void setKeyPwd(String keyPwd) {
        this.keyPwd = keyPwd;
    }

    public String getAliasPwd() {
        return aliasPwd;
    }

    public void setAliasPwd(String aliasPwd) {
        this.aliasPwd = aliasPwd;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getChannelsFile() {
        return channelsFile;
    }

    public void setChannelsFile(String channelsFile) {
        this.channelsFile = channelsFile;
    }

    public Boolean getSign() {
        return isSign;
    }

    public void setSign(Boolean sign) {
        isSign = sign;
    }

    public Boolean getChannel() {
        return isChannel;
    }

    public void setChannel(Boolean channel) {
        isChannel = channel;
    }
}
