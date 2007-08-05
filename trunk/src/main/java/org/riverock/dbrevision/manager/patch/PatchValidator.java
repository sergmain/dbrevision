package org.riverock.dbrevision.manager.patch;

import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.annotation.schema.db.Action;

/**
 * User: SergeMaslyukov
 * Date: 05.08.2007
 * Time: 14:45:29
 */
public interface PatchValidator {
    public PatchStatus validate(DatabaseAdapter adapter, Action action) throws Exception;
}
