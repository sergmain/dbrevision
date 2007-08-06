package org.riverock.dbrevision.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;

import org.riverock.dbrevision.Constants;
import org.riverock.dbrevision.system.DbStructureImport;
import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.annotation.schema.db.Patches;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.exception.InitStructureFileNotFoundException;
import org.riverock.dbrevision.exception.NoChildPatchFoundException;
import org.riverock.dbrevision.exception.PatchParseException;
import org.riverock.dbrevision.exception.PatchPrepareException;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;
import org.riverock.dbrevision.exception.VersionPathNotFoundException;
import org.riverock.dbrevision.exception.FirstVersionWithPatchdException;
import org.riverock.dbrevision.manager.patch.PatchService;
import org.riverock.dbrevision.manager.patch.PatchSorter;
import org.riverock.dbrevision.manager.dao.ManagerDaoFactory;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:04:13
 */
public class Version implements Serializable {

    private Version previousVersion=null;
    private Version nextVersion=null;

    List<Patch> patches=null;

    private Database database =null;

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
                FileInputStream fis = new FileInputStream(file);
                Patches patches;
                try {
                    patches = Utils.getObjectFromXml(Patches.class, fis);
                }
                catch (JAXBException e) {
                    throw new PatchParseException("Patch file: " + file.getAbsolutePath(), e);
                }
                list.addAll(patches.getPatches());
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

    public void applyInitStructure() {
        if (isComplete) {
            return;
        }
        if (!patches.isEmpty()) {
            throw new FirstVersionWithPatchdException();
        }
        DbSchema dbSchema;
        try {
            FileInputStream inputStream = new FileInputStream(initStructureFile);
            dbSchema = Utils.getObjectFromXml(DbSchema.class, inputStream);
        }
        catch (JAXBException e) {
            throw new PatchParseException("Init structure file: " + initStructureFile.getAbsolutePath(), e);
        }
        catch (FileNotFoundException e) {
            throw new InitStructureFileNotFoundException(e);
        }
        DbStructureImport.importStructure(database, dbSchema, true);
        ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, null);
        isComplete=true;
    }

    public void apply() {
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
            }
            ManagerDaoFactory.getManagerDao().makrCurrentVersion(database, modulePath.getName(), versionName, null);
        }
        else {
            applyInitStructure();
        }
        isComplete=true;
    }

    public void applayPatch(String patchName) {
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
