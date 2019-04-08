package br.com.softbox.atest_bdd.pages;

import br.com.softbox.atest_bdd.pages.ProductConfigEntry;
import br.com.softbox.core.AtestCoreException;
import br.com.softbox.core.JsonResource;
import br.com.softbox.utils.AtestUtilsException;
import com.google.common.reflect.TypeToken;
import gherkin.deps.com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages the ProductPage Settings.
 */
public class ProductConfigLoader {
    private List<ProductConfigEntry> _products = new ArrayList<ProductConfigEntry>();
    private final String _resourcePath = "config/product.json";

    /**
     * Ctor.
     */
    public ProductConfigLoader() throws AtestCoreException {
        try {
            JsonResource resource = new JsonResource();
            BufferedReader br = resource.getJsonResource(_resourcePath);
            Gson gson = new Gson();
            Type colletionType = new TypeToken<Collection<ProductConfigEntry>>(){}.getType();
            Collection<ProductConfigEntry> entries = gson.fromJson(br, colletionType);
            for (ProductConfigEntry e : entries) {
                _products.add(e);
            }
        } catch (AtestUtilsException e) {
            throw new AtestCoreException(e.getMessage());
        } catch (Exception e) {
            throw new AtestCoreException(e.getMessage());
        }
    }

    public String getResourcePath() {
        return _resourcePath;
    }

    /**
     * Returns the ProductConfigEntry entries that came from product.json file.
     * @return A list of ProductConfigEntry entries.
     */
    public List<ProductConfigEntry> getProductEntries() {
        return _products;
    }
}
