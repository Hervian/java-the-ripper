package com.github.hervian.javatheripper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.zeroturnaround.zip.ZipUtil;

import lombok.SneakyThrows;

/**
 * Service class inspired by: http://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java
 * 
 * @author Anders Granau Høfft
 *
 */
public enum CompressionUtils {
    INSTANCE;

//    private static final int BUFFER_SIZE = 4096;

    @SneakyThrows
    public File unzip(File zipFileInput) {
        ZipUtil.explode(zipFileInput);
        return zipFileInput.getParentFile();
    }

    /**
     * Unarchives and decompresses a gzip'ed tar file.
     * 
     * @param tarGzFile
     * @param destFile
     * @throws IOException
     */
    @SneakyThrows
    public File decompressGzippedTar(File tarGzFile) {
        File tarFile = File.createTempFile(getFileName(tarGzFile), "");
        tarFile.deleteOnExit(); //TODO: JVM may rarely exit if this is used in a Server. Delete "manually" instead.
        tarFile = deCompressGZipFile(tarGzFile, tarFile);
        unTarFile(tarFile);
        return tarGzFile.getParentFile();
    }

    public void unTarFile(File tarFile) throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(tarFile))) {
            TarArchiveEntry tarEntry = null;
            while ((tarEntry = tis.getNextTarEntry()) != null) {
                File outputFile = new File(tarFile.getParentFile(), tarEntry.getName());

                if (tarEntry.isDirectory()) {
                    if (!outputFile.exists()) {
                        outputFile.mkdirs();
                    }
                } else {
                    outputFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        IOUtils.copy(tis, fos);
                    }
                }
            }
        }
    }

    public File deCompressGZipFile(File gZippedFile, File tarFile) throws IOException {
        try (GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(gZippedFile))) {
            try (FileOutputStream fos = new FileOutputStream(tarFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gZIPInputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
        }
        return tarFile;
    }

    private String getFileName(File inputFile) {
        return inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
    }
    
//  @SneakyThrows
//  public File unzip(File zipFileInput, File targetDirectory) {
//      if (!targetDirectory.exists()) {
//          targetDirectory.mkdirs();
//      }
//      try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileInput))) {
//          ZipEntry zipEntry = zis.getNextEntry();
//          while (zipEntry != null) {
//              String filePath = targetDirectory.getAbsolutePath() + File.separator + zipEntry.getName();
//              if (!zipEntry.isDirectory()) { // if the entry is a file, extracts it
//                  extractFile(zis, filePath);
//              } else {
//                  File dir = new File(filePath);// if the entry is a directory, make the directory
//                  dir.mkdir();
//              }
//              zis.closeEntry();
//              zipEntry = zis.getNextEntry();
//          }
//      }
//      return zipFileInput.getParentFile();
//  }
//
//  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
//      try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
//          byte[] bytesIn = new byte[BUFFER_SIZE];
//          int read = 0;
//          while ((read = zipIn.read(bytesIn)) != -1) {
//              bos.write(bytesIn, 0, read);
//          }
//      }
//  }

}
