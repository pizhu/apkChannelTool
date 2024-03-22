package pz.tools.apkchannel;

import java.io.File;

@FunctionalInterface
public interface FileSelectProcessor {
    void selected(File file);
}
