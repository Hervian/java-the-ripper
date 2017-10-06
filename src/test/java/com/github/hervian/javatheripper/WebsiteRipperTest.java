package com.github.hervian.javatheripper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.junit.Test;

import lombok.NonNull;

public class WebsiteRipperTest {
    
    @Test
    public void test_rip() throws URISyntaxException, ExecuteException, IOException {
//        String[] arguments = new String[]{"http://www.example.com", "-O", "C:/mansipi/test"};
        String[] arguments = new String[]{"-O", "C:/mansipi/test5"};
        new WebsiteRipper().rip(new URI("http://www.example.com"), arguments);
    }
    
    
    @Test
    public void test_rip_johnfrandsen() throws URISyntaxException, ExecuteException, IOException {
//        String[] arguments = new String[]{"http://www.example.com", "-O", "C:/mansipi/test"};
        String[] arguments = new String[]{"-O", "C:/mansipi/dk/johnfrandsen"};
        new WebsiteRipper().rip(new URI("https://www.johnfrandsen.dk/bolig"), arguments);
    }
    


}
