package org.riverock.dbrevision.manager.patch;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.exception.TwoPatchesWithSameNameException;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;

/**
 * User: SMaslyukov
 * Date: 02.08.2007
 * Time: 14:44:38
 */
public class TestPatchSorter extends TestCase {

    public void testWrongList_twoFirstPatch() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("aaa", null));
        list.add( getPatch("aaa", null));
        boolean isCorrect = false;
        try {
            PatchSorter.sort(list);
        }
        catch (TwoPatchesWithSameNameException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    public void testWrongList_twoPrevNameEmpty() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("aaa", null));
        list.add( getPatch("bbb", null));
        boolean isCorrect = false;
        try {
            PatchSorter.sort(list);
        }
        catch (TwoPatchesWithEmptyPreviousPatchException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    public void testSorter() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("bbb", "zzz"));
        list.add( getPatch("zzz", "aaa"));
        list.add( getPatch("aaa", null));
        list.add( getPatch("ccc", "ddd"));
        list.add( getPatch("ddd", "bbb"));
        list = PatchSorter.sort(list);
        assertNotNull(list);
        assertEquals(5, list.size());
        assertEquals("aaa", list.get(0).getName() );
        assertEquals("zzz", list.get(1).getName() );
        assertEquals("bbb", list.get(2).getName() );
        assertEquals("ddd", list.get(3).getName() );
        assertEquals("ccc", list.get(4).getName() );
    }


    private Patch getPatch(String name, String prevName) {
        Patch p = new Patch();
        p.setName(name);
        p.setPreviousName(prevName);
        
        return p;
    }
}
