package br.com.softbox.core;

import br.com.softbox.utils.AtestUtilsException;
import com.google.common.reflect.TypeToken;
import gherkin.deps.com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages the BDD Settings.
 */
public class BDDSettingsLoader {
    private List<BDDEntry> _bdds = new ArrayList<BDDEntry>();
    private final String _resourcePath =  "config/bdd.json";

    /**
     * Ctor.
     * @throws IOException
     */
    public BDDSettingsLoader() throws AtestCoreException {
        try {
            JsonResource resource = new JsonResource();
            BufferedReader br = resource.getJsonResource(_resourcePath);
            Gson _gson = new Gson();
            Type colletionType = new TypeToken<Collection<BDDEntry>>(){}.getType();
            Collection<BDDEntry> entries = _gson.fromJson(br, colletionType);
            for (BDDEntry e : entries) {
                _bdds.add(e);
            }
        } catch (AtestUtilsException e) {
            throw new AtestCoreException(e.getMessage());
        } catch (Exception e) {
            throw new AtestCoreException(e.getMessage());
        }
    }
    /**
     * Returns the BDD entries that came from bdd.json file.
     * @return A list of BDD entries.
     */
    public List<BDDEntry> getBDDs() {
        return _bdds;
    }

    public String getResourcePath() {
        return _resourcePath;
    }
}
