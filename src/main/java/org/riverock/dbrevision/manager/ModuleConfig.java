package org.riverock.dbrevision.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 1:07:47
 */
public class ModuleConfig {
    private String description;
    private String name;
    private List<String> versions=new ArrayList<String>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }
}
