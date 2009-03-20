package org.riverock.dbrevision.db;

/**
 * User: SergeMaslyukov
 * Date: 19.03.2009
 * Time: 16:24:31
 */
public class ViewManager
{
    public static String removeOracleWithReadOnly(String viewText) {
        int idx = viewText.toUpperCase().indexOf("WITH");
        if (idx!=-1) {
            String sr = viewText.substring(idx+4).trim().toUpperCase();
            if  (sr.startsWith("READ")) {
                String so = sr.substring(4).trim();
                if (so.startsWith("ONLY")) {
                    int idxEnd = viewText.toUpperCase().indexOf("ONLY", idx);
                    viewText = viewText.substring(0, idx) + viewText.substring(idxEnd+4);
                }
            }
        }
        return viewText;
    }
}
