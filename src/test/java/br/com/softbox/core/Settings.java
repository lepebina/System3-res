package br.com.softbox.core;

import java.util.List;

/**
 * A mapping for the settings file.
 */
public class Settings {
    public String root_dir;
    public String workspace_dir;
    public String feature_dir;
    public String feature_dir_store_1;
    public String feature_dir_store_2;
    public String evidence_dir;
    public Selenium selenium;
    public String store_1;
    public String store_2;
    public List<String> sites_store_1;
    public List<String> sites_store_2;

    public static class Selenium {
        public String run_as_localhost;
        public String grid;
    }

    public static class MainMenuPage {
        String id;
        String LOGIN;
        String LOGOUT;
        String ACCOUNT;
        String ORDERS;
        String ADDRESSES;
        String WISH_LIST;
    }
    public List<MainMenuPage> main_menu_pages;
}
