package br.com.softbox.core;

import br.com.softbox.utils.AtestUtilsException;
import com.google.gson.Gson;

import java.io.*;


/**
 *
 */
public class AtestSelectorLoader {
    private SelectorEntry _selector;
    private final String _resourcePath =  "config/selectors.json";

    public String getResourcePath() {
        return _resourcePath;
    }

    public AtestSelectorLoader() throws AtestCoreException {
        try {
            JsonResource resource = new JsonResource();
            BufferedReader br = resource.getJsonResource(_resourcePath);
            Gson gson = new Gson();
            _selector = gson.fromJson(br, SelectorEntry.class);
        } catch (AtestUtilsException e) {
            throw new AtestCoreException(e.getMessage());
        } catch (Exception e) {
            throw new AtestCoreException(e.getMessage());
        }
    }

    public SelectorEntry getEntry() {
        return _selector;
    }
}
