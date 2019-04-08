package br.com.softbox.core;

import br.com.softbox.utils.AtestUtilsException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

/**
 *
 */
public class JsonResource {
    public JsonResource() {
    }
    public BufferedReader getJsonResource(String resourcePath) throws AtestUtilsException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(resourcePath);
        if (url == null) {
            throw new AtestUtilsException("unable to locate the resource [" + resourcePath + "]");
        }
        String filename = url.getFile();
        if (filename.contains("!")) {
            filename = filename.substring(filename.indexOf('!') + 2);
        }
        File file = new File(filename);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new AtestUtilsException("could not open file [" + filename + "]");
        }
        return br;
    }
}
