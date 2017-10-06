package com.github.hervian.javatheripper;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmbeddedExecutable {
    /**
     * A file name including file extension indicating the file type. Fx "httrack.ext".
     */
    private String       nameOfBinary;
    private String       relativePathToBinary; //   "/httrack/windows/" or "/httrack/unix/"
    private Decompressor decompressor;
    private String       executableName;

    
    @FunctionalInterface
    public interface Decompressor {
        File decompress(File compressedFile);
    }


}