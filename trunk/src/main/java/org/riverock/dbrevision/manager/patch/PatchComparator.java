package org.riverock.dbrevision.manager.patch;

import java.util.Comparator;
import java.io.Serializable;

import org.riverock.dbrevision.annotation.schema.db.Patch;
import org.riverock.dbrevision.exception.TwoPatchesWithEmptyPreviousPatchException;
import org.riverock.dbrevision.exception.TwoPatchesWithSameNameException;

/**
 * User: SMaslyukov
 * Date: 02.08.2007
 * Time: 14:29:57
 */
public class PatchComparator implements Comparator<Patch>, Serializable {
    public static final PatchComparator PATCH_COMPARATOR = new PatchComparator();

    public static PatchComparator getInstance() {
        return PATCH_COMPARATOR;
    }

    private PatchComparator() {
    }


    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     *
     * It is generally the case, but <i>not</i> strictly required that
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     *
     * @param p1 the first object to be compared.
     * @param p2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     */
    public int compare(Patch p1, Patch p2) {
        System.out.println("name #1: " +p1.getName()+", prevName #1: " +p1.getPreviousName()+", name #2: " +p2.getName()+", prevName #2: " +p2.getPreviousName());
        if (p1.getName().equals(p2.getName())) {
            throw new TwoPatchesWithSameNameException("Patch name: " +p1.getName());
        }
        if (p1.getPreviousName()==null && p2.getPreviousName()==null) {
            throw new TwoPatchesWithEmptyPreviousPatchException("Patch name #1: " +p1.getName()+", patch name #2: " +p2.getName());
        }

        if (p1.getPreviousName()==null) {
            return -1;
        }
        if (p1.getName().equals(p2.getPreviousName())) {
            return -1;
        }
        if (p1.getPreviousName().equals(p2.getName())) {
            return 1;
        }
        return 1 ;
    }
}
