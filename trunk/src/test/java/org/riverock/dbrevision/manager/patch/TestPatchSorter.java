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

import junit.framework.TestCase;

import org.riverock.dbrevision.schema.db.Patch;
import org.riverock.dbrevision.exception.TwoPatchesWithSameNameException;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;
import org.riverock.dbrevision.exception.FirstPatchNotFoundException;
import org.riverock.dbrevision.exception.NoChildPatchFoundException;

/**
 * User: SMaslyukov
 * Date: 02.08.2007
 * Time: 14:44:38
 */
public class TestPatchSorter extends TestCase {

    public void testWrongList_listWithOneElement() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("aaa", null));
        assertNotNull(PatchSorter.sort(list));
    }

    public void testWrongList_firstPatchNotFound() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("aaa", "zzz"));
        list.add( getPatch("aaa", "bbb"));
        boolean isCorrect = false;
        try {
            PatchSorter.sort(list);
        }
        catch (FirstPatchNotFoundException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    public void testWrongList_twoFirstPatch() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("aaa", null));
        list.add( getPatch("aaa", "bbb"));
        boolean isCorrect = false;
        try {
            PatchSorter.sort(list);
        }
        catch (TwoPatchesWithSameNameException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    public void testWrongList_totalWrong() throws Exception {
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
        catch (TwoPatchesWithEmptyPreviousPatchException e) {
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

    public void testNoChildFoundSorter() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("webmill_init_def_v2", null));
        list.add( getPatch("test_0_0", "webmill_init_def_v2"));
        list.add( getPatch("test_1_0", "test_0_0"));
        list.add( getPatch("test_1_1", "test_1_0"));
        list.add( getPatch("test_2_1", "test_1_1"));
        list.add( getPatch("test_0_2", "test_0_1"));
        boolean isCorrect = false;
        try {
            PatchSorter.sort(list);
        }
        catch (NoChildPatchFoundException e) {
            isCorrect = true;
        }
        assertTrue(isCorrect);
    }

    public void testTwoChildrenSorter() throws Exception {
        List<Patch> list = new ArrayList<Patch>();
        list.add( getPatch("webmill_init_def_v2", null));
        list.add( getPatch("test_0_0", "webmill_init_def_v2"));
        list.add( getPatch("test_1_0", "test_0_0"));
        list.add( getPatch("test_1_1", "test_1_0"));
        list.add( getPatch("test_2_1", "test_1_1"));
        list.add( getPatch("test_1_2", "test_2_1"));
        list.add( getPatch("test_0_2", "test_1_2"));
        list = PatchSorter.sort(list);
        assertNotNull(list);
        assertEquals(7, list.size());
        assertEquals("webmill_init_def_v2", list.get(0).getName() );
        assertEquals("test_0_0", list.get(1).getName() );
        assertEquals("test_1_0", list.get(2).getName() );
        assertEquals("test_1_1", list.get(3).getName() );
        assertEquals("test_2_1", list.get(4).getName() );
        assertEquals("test_1_2", list.get(5).getName() );
        assertEquals("test_0_2", list.get(6).getName() );
    }


    private Patch getPatch(String name, String prevName) {
        Patch p = new Patch();
        p.setName(name);
        p.setPreviousName(prevName);
        
        return p;
    }
}
