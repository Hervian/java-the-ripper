package com.github.hervian.javatheripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

public class HTTrackExecutableProvider {

    /**
     * Returns a {@link CommandLine} by the following steps:
     * <ol>
     * <li>Locate zip/tar.gz of executable file within jar
     * <li>Copy zip file to temp directory
     * <li>Unzip/untar file.
     * <li>Create and return CommandLine
     * </ol>
     * 
     * @param embeddedExecutable
     * @param arguments
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Path createCommandLine(EmbeddedExecutable embeddedExecutable) throws IOException, URISyntaxException {
        File directoryOfBinary = makeExecutableAccessibleInTempDir(embeddedExecutable);
        return java.nio.file.Files.find(
                Paths.get(directoryOfBinary.getAbsolutePath()), 5,
                (path, attr) -> {
                    System.out.println(path.getFileName());
                    return path.getFileName().toString().contains(embeddedExecutable.getExecutableName());
                })
                .findFirst().get();
    }

    private static File makeExecutableAccessibleInTempDir(EmbeddedExecutable embeddedExecutable) throws IOException, URISyntaxException {
        InputStream source = getSourceFile(embeddedExecutable);
        File targetFile = createDestination(embeddedExecutable);
        FileUtils.copyInputStreamToFile(source, targetFile);
        File folderOfDecompressedData = embeddedExecutable.getDecompressor().decompress(targetFile);
        return folderOfDecompressedData;
    }

    private static File createDestination(EmbeddedExecutable embeddedExecutable) throws IOException {
        String prefix = embeddedExecutable.getNameOfBinary().substring(0, embeddedExecutable.getNameOfBinary().lastIndexOf('.'));
        File tmpDir = Files.createTempDir();
        return new File(tmpDir, prefix);
    }

    /**
     * We must handle two cases: 1) when running throug an IDE, and 2, when running the jar.
     */
    @SuppressWarnings("resource")
    private static InputStream getSourceFile(EmbeddedExecutable embeddedExecutable) throws URISyntaxException, ZipException, IOException {
        final URI uriOfRunningCode = getJarURI();
        File sourceDirectoryOfRunningCode = new File(uriOfRunningCode);
        InputStream source;
        if (sourceDirectoryOfRunningCode.isDirectory()) {
            source = getFileFromDirectory(embeddedExecutable, uriOfRunningCode);
        } else {
            source = getFileFromJar(embeddedExecutable, sourceDirectoryOfRunningCode);
        }
        return source;
    }

    private static InputStream getFileFromJar(EmbeddedExecutable embeddedExecutable, File sourceDirectoryOfRunningCode) throws FileNotFoundException, IOException, ZipException {
        InputStream source;
        try (ZipFile zipFile = new ZipFile(sourceDirectoryOfRunningCode)) {
            final ZipEntry entry = zipFile.getEntry(embeddedExecutable.getNameOfBinary());
            if (entry == null) {
                throw new FileNotFoundException("cannot find file: " + embeddedExecutable.getNameOfBinary() + " in archive: " + zipFile.getName());
            }
            source = zipFile.getInputStream(entry);
        }
        return source;
    }

    private static InputStream getFileFromDirectory(EmbeddedExecutable embeddedExecutable, final URI uriOfRunningCode) throws IOException, MalformedURLException {
        URL urlToFile = new URL(uriOfRunningCode.toString() + embeddedExecutable.getRelativePathToBinary() + embeddedExecutable.getNameOfBinary());
        return urlToFile.openStream();
    }

    /**
     * An alternative would be: ClassLoader.getSystemClassLoader().getResource(".").toURI(); //This shows /target/test-classes (in Eclipse) and not as the others target/classes
     * @return
     * @throws URISyntaxException
     */
    private static URI getJarURI() throws URISyntaxException {
        return WebsiteRipper.class.getProtectionDomain().getCodeSource().getLocation().toURI();
    }

    


}
