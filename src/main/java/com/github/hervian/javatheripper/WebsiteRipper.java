package com.github.hervian.javatheripper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteRipper {

    //TODO:  make sure that the HTTrack is only unzipped once and then reused.
    
    private Path pathToHttrackExecutable;
    
    //Example arguments:
    //  -*
    //  +www.example.com/*.html
    //  +www.example.com/*.php
    //  +www.example.com/*.asp
    //  +www.example.com/*.gif 
    //  +www.example.com/*.jpg 
    //  +www.example.com/*.png
    //  -mime:*/* +mime:text/html +mime:image/*
    public void rip(URI uri, String[] arguments) throws URISyntaxException, ExecuteException, IOException {
        EmbeddedExecutable embeddedExecutable = createEmbeddedExecutable();
        execute(embeddedExecutable, uri, arguments);
        log.info(String.format("%s copied.", uri));
    }


    private void execute(EmbeddedExecutable embeddedExecutable, URI uri, String[] arguments) throws IOException, URISyntaxException, ExecuteException {
        Path executablePath = getPathToExecutable(embeddedExecutable);
        CommandLine cmd = new CommandLine(executablePath.toString());
        cmd.addArgument(uri.toString());
        cmd.addArguments(arguments);
        DefaultExecutor executor = new DefaultExecutor();
        executor.execute(cmd);
    }


    private Path getPathToExecutable(EmbeddedExecutable embeddedExecutable) throws IOException, URISyntaxException {
        Path localPathToHttrackExecutable = pathToHttrackExecutable;
        if (localPathToHttrackExecutable==null || !new File(localPathToHttrackExecutable.toString()).canRead()){
            synchronized(this) {                
                localPathToHttrackExecutable = pathToHttrackExecutable;
                if (localPathToHttrackExecutable==null){
                    localPathToHttrackExecutable = pathToHttrackExecutable = HTTrackExecutableProvider.createCommandLine(embeddedExecutable);
                }
            }
        }
        return localPathToHttrackExecutable;
    }

    /**
     * HTTrack is compiled for different Operating Systems. This method identifies the OS of the currently running project, and chooses to appropriate HTTrach binary to invoke.
     * @return
     */
    private EmbeddedExecutable createEmbeddedExecutable() {
        EmbeddedExecutable embeddedExecutable = null;
        switch (JvmUtils.operatingSystem){
        case LINUX:
            //NB Deliberate fallthrough to MAC case!
        case MAC:
            embeddedExecutable = EmbeddedExecutable
                .builder()
                .relativePathToBinary("httrack/unix/")
                .nameOfBinary("httrack.unix.tar.gz")
                .executableName("ltmain.sh")
                .decompressor(file -> CompressionUtils.INSTANCE.decompressGzippedTar(file))
                .build();
            break;
        case WINDOWS:
            embeddedExecutable = EmbeddedExecutable
                .builder()
                .relativePathToBinary("httrack/windows/")
                .nameOfBinary("httrack.windows.zip")
                .executableName("httrack.exe")
                .decompressor(file -> CompressionUtils.INSTANCE.unzip(file))
                .build();
            break;
        default:
            break;
        }
        return embeddedExecutable;
    }


}
