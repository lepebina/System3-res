package br.com.softbox.core;

import java.util.List;

/**
 *
 */
public class SelectorEntry {
    public List<String> selenium_by;
    public List<String> element_return;
    public List<Host> hosts_alias;
    public List<Bundle> bundle;

    public class Host {
        public String alias;
        public String url;
    }

    public class Bundle {
        public String id;
        public String description;
        public String element_return;
        public int timeout;
        public List<Data> data;
    }

    public class Data {
        public String alias;
        public String selenium_by;
        public String selector_text;

    }
}
