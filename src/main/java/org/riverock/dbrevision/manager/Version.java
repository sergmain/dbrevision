package org.riverock.dbrevision.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.annotation.schema.db.Patches;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.exception.InitStructureFileNotFoundException;
import org.riverock.dbrevision.exception.NoChildPatchFoundException;
import org.riverock.dbrevision.exception.PatchParseException;
import org.riverock.dbrevision.exception.PatchPrepareException;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;
import org.riverock.dbrevision.exception.VersionPathNotFoundException;
import org.riverock.dbrevision.manager.dao.ManagerDaoFactory;
import org.riverock.dbrevision.manager.patch.PatchService;
import org.riverock.dbrevision.manager.patch.PatchSorter;
import org.riverock.dbrevision.system.DbStructureImport;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:04:13
 */
public class Version implements Serializable {
    private final static Logger log = Logger.getLogger(Version.class);

    private Version previousVersion=null;
    private Version nextVersion=null;

    List<Patch> patches=null;

    Database database =null;

    private boolean isComplete = false;

    private String versionName;

    private File versionPath=null;

    private File initStructureFile=null;

    private File patchPath=null;
    
    private File modulePath=null;

    public Version(Database database, File modulePath, String versionName) {
        this.database = database;
        this.modulePath = modulePath;
        this.versionName = versionName;
        this.versionPath = new File(modulePath, this.versionName);
        if (!versionPath.exists()) {
            throw new VersionPathNotFoundException("Version path not found: " + versionPath.getAbsolutePath() );
        }
        this.initStructureFile = new File(versionPath, Constants.INIT_STRUCTURE_FILE_NAME);
        if (!initStructureFile.exists()) {
            throw new InitStructureFileNotFoundException("Init structure file not found: " + initStructureFile.getAbsolutePath() );
        }
        this.patchPath = new File(versionPath, Constants.PATCH_DIR_NAME);
        initPatches(); 
    }

    private void initPatches() {
        File[] files = patchPath.listFiles(
            new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            }
        );
        if (files==null) {
            patches = new ArrayList<Patch>();
            return;
        }
        List<Patch> list = new ArrayList<Patch>();
        try {
            for (File file : files) {
                FileInputStream inputStream=null;
                Patches patches;
                try {
                    inputStream = new FileInputStream(file);
                    try {
                        patches = Utils.getObjectFromXml(Patches.class, inputStream);
                    }
                    catch (JAXBException e) {
                        throw new PatchParseException("Patch file: " + file.getAbsolutePath(), e);
                    }
                    list.addAll(patches.getPatches());
                }
                finally {
                    if (inputStream!=null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException e1) {
                            log.error("Error close input stream", e1);
                        }
                    }
                }
            }
            try {
                patches = PatchSorter.sort(list);
            }
            catch (TwoPatchesWithEmptyPreviousPatchException e) {
                String s="";
                for (File file : files) {
                    s+=("\n"+file.getAbsolutePath());
                }
                throw new TwoPatchesWithEmptyPreviousPatchException(s, e);
            }
            catch (NoChildPatchFoundException e) {
                String s="";
                for (File file : files) {
                    s+=("\n"+file.getAbsolutePath());
                }
                throw new NoChildPatchFoundException(s, e);
            }
        }
        catch (Exception e) {
            throw new PatchPrepareException(e);
        }
    }

    public void applyInitStructure() throws SQLException {
        if (isComplete) {
            return;
        }
        DbSchema dbSchema;
        FileInputStream inputStream=null;
        try {
            inputStream = new FileInputStream(initStructureFile);
            dbSchema = Utils.getObjectFromXml(DbSchema.class, inputStream);
        }
        catch (JAXBException e) {
            throw new PatchParseException("Init structure file: " + initStructureFile.getAbsolutePath(), e);
        }
        catch (FileNotFoundException e) {
            throw new InitStructureFileNotFoundException(e);
        }
        finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                }
                catch (IOException e1) {
                    log.error("Error close input stream", e1);
                }
            }
        }
        DbStructureImport.importStructure(database, dbSchema, true);
        ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, null);
database.getConnection().commit();
        isComplete=true;

    }

    public void apply() throws SQLException {
        if (isComplete) {
            return;
        }

        if (DbRevisionChecker.isModuleReleased(database, modulePath.getName())) {
            for (Patch patch : patches) {
                if (patch.isProcessed()) {
                    continue;
                }
                PatchService.processPatch(database, patch);
                ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, patch.getName());
database.getConnection().commit();
            }
            ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, null);
database.getConnection().commit();
        }
        else {
            applyInitStructure();

        }
        isComplete=true;
    }

    public void applayPatch(String patchName) throws SQLException {
        if (isComplete || StringUtils.isBlank(patchName)) {
            return;
        }
        Patch firstNotProcessed=null;
        for (Patch patch : patches) {
            if (!patch.isProcessed()) {
                firstNotProcessed = patch;
                break;
            }
        }
        
        if (firstNotProcessed!=null && firstNotProcessed.getName().equals(patchName)) {
            PatchService.processPatch(database, firstNotProcessed);
            ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, firstNotProcessed.getName());
database.getConnection().commit();
        }
    }

    public Version getPreviousVersion() {
        return previousVersion;
    }

    void setPreviousVersion(Version previousVersion) {
        this.previousVersion = previousVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public File getPatchPath() {
        return patchPath;
    }

    public void setPatchPath(File patchPath) {
        this.patchPath = patchPath;
    }

    public Patch getLastPatch() {
        if (patches.isEmpty()) {
            return null;
        }
        return patches.get(patches.size()-1);
    }

    public boolean isComplete() {
        return isComplete;
    }

    void setComplete(boolean complete) {
        isComplete = complete;
    }

    public List<Patch> getPatches() {
        return patches;
    }

    void setPatches(List<Patch> patches) {
        this.patches = patches;
    }

    Version getNextVersion() {
        return nextVersion;
    }

    void setNextVersion(Version nextVersion) {
        this.nextVersion = nextVersion;
    }

    public String toString() {
        return "["+versionName+";"+isComplete+"]";
    }
}
