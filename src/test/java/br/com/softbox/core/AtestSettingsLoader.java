package br.com.softbox.core;

import br.com.softbox.utils.AtestUtilsException;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Controlls the settings from settings.json file.
 */
public class AtestSettingsLoader {
    private Settings _settings = null;
    private String _resourcePath = "config/settings.json";

    /**
     * Ctor
     * @throws IOException
     */
    public AtestSettingsLoader() throws AtestCoreException {
        try {
            JsonResource resource = new JsonResource();
            BufferedReader br = resource.getJsonResource(_resourcePath);
            Gson gson = new Gson();
            _settings = gson.fromJson(br, Settings.class);
        } catch (AtestUtilsException e) {
            throw new AtestCoreException(e.getMessage());
        }
    }
    /**
     * Verify whether Selenium will execute as localhost instance.
     * @return True is localhost, otherwise false.
     */
    public boolean isSeleniumLocalhost() {
        return _settings.selenium.run_as_localhost.equalsIgnoreCase("yes");
    }
    /**
     * Returns the absolute para to ATEST/BF workspace.
     * @return the path
     */
    private String getAbsoluteWorkspaceDir() {
        Path p = Paths.get(_settings.root_dir
                + File.separator
                + _settings.workspace_dir
                + File.separator);
        return p.toString();
    }
    /**
     * Returns the root system directory under which the workspace has a room.
     * @return the root directory.
     */
    public String getRootDir() {
        return _settings.root_dir;
    }
    /**
     * Returns the Selenium grid host machine.
     * @return The Seleninum grid host.
     */
    public String getSeleniumGridLocation() {
        return _settings.selenium.grid;
    }
    /**
     * Returns the place where ATEST/BF lives.
     * @return The absolute path to the ATEST/BF workspace.
     */
    public String getWorkspaceDir() {
        return getAbsoluteWorkspaceDir();
    }
    /**
     * Returns the absolute path to the evidence directory.
     * @return The evidence path.
     */
    public String getEvidenceDir() {
        return getAbsoluteWorkspaceDir()
                + File.separator
                + _settings.evidence_dir;
    }
    /**
     * Returns the absolute path to the feature directory.
     * @return The path to the feature files (BDD)
     */
    public String getFeatureDir() {
        return getAbsoluteWorkspaceDir()
                + File.separator
                + _settings.feature_dir;
    }
    /**
     * Returns the absolute path to the feature directory of store 1.
     * @return The path to the feature files (BDD)
     */
    public String getFeatureDirStore_1() {
        return getAbsoluteWorkspaceDir()
                + File.separator
                + _settings.feature_dir_store_1;
    }
    /**
     * Returns the absolute path to the feature directory of store 2.
     * @return The path to the feature files (BDD)
     */
    public String getFeatureDirStore_2() {
        return getAbsoluteWorkspaceDir()
                + File.separator
                + _settings.feature_dir_store_2;
    }
    /**
     * Returns just the directory's name of the ATEST/BF application.
     * @return The directory name where the ATEST/BF lives.
     */
    public String getRawWorkspaceDir() {
        return _settings.workspace_dir;
    }
    /**
     * Returns the directory's name for the Evidence file.
     * @return The evidence directory.
     */
    public String getRawEvidenceDir() {
        return _settings.evidence_dir;
    }
    /**
     * Returns the directory's name for the Feature file.
     * @return The feature directory.
     */
    public String getRawFeatureDir() {
        return _settings.feature_dir;
    }
    /**
     * Returns the sites available for the initial page
     * @param store The store name
     * @return the sistes list
     */
    public List<String> getSites(String store) {
        if (store.equals(_settings.store_1)) {
            return _settings.sites_store_1;
        } else if (store.equals(_settings.store_2)) {
            return _settings.sites_store_2;
        }
        return null;
    }
    public String getResourcePath() {
        return _resourcePath;
    }
    public HashMap<TestController.MainMenuOptionCode, String> getMainMenuUrlOptions(String initialSiteURL) {
        HashMap<TestController.MainMenuOptionCode, String> options = new HashMap<>();
        for (Settings.MainMenuPage mainMenuPage : _settings.main_menu_pages) {
            if (initialSiteURL.contains(mainMenuPage.id)) {
                options.put(TestController.MainMenuOptionCode.LOGIN, mainMenuPage.LOGIN);
                options.put(TestController.MainMenuOptionCode.LOGOUT, mainMenuPage.LOGOUT);
                options.put(TestController.MainMenuOptionCode.ACCOUNT, mainMenuPage.ACCOUNT);
                options.put(TestController.MainMenuOptionCode.ORDERS, mainMenuPage.ORDERS);
                options.put(TestController.MainMenuOptionCode.ADDRESSES, mainMenuPage.ADDRESSES);
                options.put(TestController.MainMenuOptionCode.WISH_LIST, mainMenuPage.WISH_LIST);
                break;
            }
        }
        return options;
    }
    public List<String> getStores() {
        return Arrays.asList(_settings.store_1, _settings.store_2);
    }
    public String getStore_1() {
        return _settings.store_1;
    }
    public String getStore_2() {
        return _settings.store_2;
    }
}
