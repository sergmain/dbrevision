/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.riverock.dbrevision.manager.patch;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.riverock.dbrevision.schema.db.v3.Patch;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;
import org.riverock.dbrevision.exception.FirstPatchNotFoundException;
import org.riverock.dbrevision.exception.TwoPatchesWithSameNameException;
import org.riverock.dbrevision.exception.NoChildPatchFoundException;

/**
 * User: SMaslyukov
 * Date: 02.08.2007
 * Time: 15:26:14
 */
public class PatchSorter {
    
    public static List<Patch> sort(List<Patch> patches) {
        // looking for 1st element
        Patch firstPatch=null;
        List<Patch> list = new ArrayList<Patch>(patches.size());
        for (Patch patch : patches) {
            patch.setProcessed(false);
            if (patch.getPreviousName()==null) {
                if (firstPatch!=null) {
                    throw new TwoPatchesWithEmptyPreviousPatchException("Patch name #1: " +firstPatch.getName()+", patch name #2: " +patch.getName());
                }
                firstPatch = patch;
            }
            else {
                list.add(patch);
            }
        }
        if (firstPatch==null) {
            throw new FirstPatchNotFoundException();
        }

        checkDuplicateNames(patches);

        List<Patch> result = new ArrayList<Patch>(patches.size());
        Patch current = firstPatch;
        while (!list.isEmpty()) {
            result.add(current);
            Iterator<Patch> it = list.iterator();
            boolean isFound=false;
            while (it.hasNext()) {
                Patch patch = it.next();
                if (current.getName().equals(patch.getPreviousName())) {
                    current = patch;
                    it.remove();
                    isFound=true;
                    break;
                }
            }
            if (!isFound && !list.isEmpty()) {
                throw new NoChildPatchFoundException("patch name: " +current.getName());
            }
        }
        result.add(current);
        return result;
    }

    private static void checkDuplicateNames(List<Patch> patches) {
        List<Patch> result = new ArrayList<Patch>(patches);
        while (!result.isEmpty()) {
            if (patches.size()==1) {
                break;
            }
            Patch current = result.get(0);
            result.remove(0);
            for (Patch patch : result) {
                if (current.getName().equals(patch.getName())) {
                    throw new TwoPatchesWithSameNameException("Patch name: " + current.getName());
                }
            }
        }
    }
}
