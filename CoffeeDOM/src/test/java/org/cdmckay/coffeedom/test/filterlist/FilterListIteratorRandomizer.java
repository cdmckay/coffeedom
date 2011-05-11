package org.cdmckay.coffeedom.test.filterlist;

import org.cdmckay.coffeedom.Content;
import org.cdmckay.coffeedom.Element;

import java.util.*;

public final class FilterListIteratorRandomizer {
    // True if we're checking Element.getChildren(), false if Element.getContent()
    private boolean elementsOnly;

    // Where to get test objects from - points to "objects" or "elements".
    private List<? extends Content> sourceList;

    // Source of randomness.
    private Random random;

    private Element parent;
    private List<Content> referenceList;
    private List<Content> testList;
    private ListIterator<Content> referenceIterator;
    private ListIterator<Content> testIterator;

    // Can we call set() or remove() right now w/o throwing an exception?
    private boolean canModify;

    // Total number of iterations finished.
    private static long finished;

    // Total number of iterations when we last restarted the iterators.
    private static long lastRestart;

    // Constructor.
    public FilterListIteratorRandomizer(boolean elementsOnly, Random random,
                                        List<Content> objects,
                                        List<Element> elements,
                                        Element parent,
                                        List<Content> referenceList,
                                        List<Content> testList) {
        this.elementsOnly = elementsOnly;
        this.random = random;
        this.parent = parent;
        this.referenceList = referenceList;
        this.testList = testList;
        this.sourceList = elementsOnly ? elements : objects;
    }

    // Do the tests.
    public void test(int iterations) {
        try {
            internalTest(iterations);
        } catch (RuntimeException ex) {
            System.out.println("finished = " + finished + "; lastRestart = " + lastRestart);
            throw ex;
        }
    }

    private void internalTest(int iterations) {
        // The two lists should *always* be equivalent from now on.
        checkLists();

        // Run the tests.
        for (int i = 0; i < iterations; i++) {

            // Get the iterators. We get new ones if an exception occurred,
            // and every once in a while even if no exception occurred.
            if (random.nextDouble() < .01 || testIterator == null) {
                // Test either listIterator() or listIterator(int).
                if (random.nextBoolean()) {
                    referenceIterator = referenceList.listIterator();
                    testIterator = testList.listIterator();
                } else {
                    int index = random.nextInt(testList.size() + 1);
                    if (debug) {
                        System.out.println("index  = " + index);
                    }
                    referenceIterator = referenceList.listIterator(index);
                    testIterator = testList.listIterator(index);
                }
                canModify = false;
                lastRestart = finished;
            }

            if (debug) {
                dumpLists();
            }

            // Run one test.
            boolean exceptionOccurred = false;
            int test = random.nextInt(10);
            switch (test) {
                case 0:
                    exceptionOccurred = testHasNext();
                    break;
                case 1:
                    exceptionOccurred = testNext();
                    break;
                case 2:
                    exceptionOccurred = testHasPrevious();
                    break;
                case 3:
                    exceptionOccurred = testPrevious();
                    break;
                case 4:
                    exceptionOccurred = testNextIndex();
                    break;
                case 5:
                    exceptionOccurred = testPreviousIndex();
                    break;
                case 6:
                    exceptionOccurred = testAdd();
                    break;
                case 7:
                    exceptionOccurred = testSet();
                    break;
                default:
                    // Note that this gets called more often than the others,
                    // so that the list doesn't expand more than it contracts.
                    exceptionOccurred = testRemove();
                    break;
            }

            // If an exception (correctly) occurred, then we start over with new
            // iterators. We make no guarantees as to the behavior of the iterators
            // after an exception. This is consistent with ArrayList's iterator:
            // If you call previous() when hasPrevious() would return false, it throws
            // an exception, and then gets stuff, so the iterator becomes unusable.
            // (Strangely, next() works better...)
            if (exceptionOccurred) {
                referenceIterator = testIterator = null;
            }

            finished++;
            if (finished % 10000 == 0) {
                System.out.println("FLI: Finished " + finished + " iterations; list size is " + referenceList.size());
            }
        }
    }

    ///////////////////////////////////////////////////////////

    private boolean testHasNext() {
        if (debug) {
            System.out.println("## testHasNext");
        }
        assertEquals(referenceIterator.hasNext(), testIterator.hasNext());
        return true;
    }

    private boolean testNext() {
        // Most of the time, ensure that we don't throw an exception.
        // Otherwise, we'd be throwing away the iterator too often.
        if (!referenceIterator.hasNext() && random.nextDouble() < .9) {
            return false;
        }

        if (debug) {
            System.out.println("## testNext");
        }
        Object result1, result2;
        try {
            result1 = referenceIterator.next();
        } catch (Exception ex) {
            result1 = ex;
        }
        try {
            result2 = testIterator.next();
        } catch (Exception ex) {
            result2 = ex;
        }

        assertEquals(result1, result2);

        canModify = true;
        return (result1 instanceof Exception);
    }

    private boolean testHasPrevious() {
        if (debug) {
            System.out.println("## testHasPrevious");
        }
        assertEquals(referenceIterator.hasPrevious(), testIterator.hasPrevious());
        return false;
    }

    private boolean testPrevious() {
        // Most of the time, ensure that we don't throw an exception.
        // Otherwise, we'd be throwing away the iterator too often.
        if (!referenceIterator.hasPrevious() && random.nextDouble() < .9) {
            return false;
        }

        if (debug) {
            System.out.println("## testPrevious");
        }
        Object result1, result2;
        try {
            result1 = referenceIterator.previous();
        } catch (Exception ex) {
            result1 = ex;
        }
        try {
            result2 = testIterator.previous();
        } catch (Exception ex) {
            result2 = ex;
        }

        assertEquals(result1, result2);

        canModify = true;
        return (result1 instanceof Exception);
    }

    private boolean testNextIndex() {
        if (debug) {
            System.out.println("## testNextIndex");
        }
        assertEquals(referenceIterator.nextIndex(), testIterator.nextIndex());
        return false;
    }

    private boolean testPreviousIndex() {
        if (debug) {
            System.out.println("## testPreviousIndex");
        }
        assertEquals(referenceIterator.previousIndex(), testIterator.previousIndex());
        return false;
    }

    private boolean testAdd() {
        Content item = randomItem();

        // Most of the time, ensure that we don't throw an exception.
        // Otherwise, we'd be throwing away the iterator too often.
        if (referenceList.contains(item) && random.nextDouble() < .9) {
            return false;
        }

        if (debug) {
            System.out.println("## testAdd");
        }

        Exception referenceResult = null;
        Exception testResult = null;

        try {
            referenceIterator.add(item);
        } catch (Exception ex) {
            referenceResult = ex;
        }
        try {
            testIterator.add(item);
        } catch (Exception ex) {
            testResult = ex;
        }

        assertEquals(referenceResult, testResult);
        checkLists();

        canModify = false;
        return referenceResult != null;
    }

    private boolean testRemove() {
        // Most of the time, ensure that we don't throw an exception.
        // Otherwise, we'd be throwing away the iterator too often.
        if (!canModify && random.nextDouble() < .9) {
            return false;
        }

        if (debug) {
            System.out.println("## testRemove");
        }

        Exception referenceResult = null;
        Exception testResult = null;

        try {
            referenceIterator.remove();
        } catch (Exception ex) {
            referenceResult = ex;
        }
        try {
            testIterator.remove();
        } catch (Exception ex) {
            testResult = ex;
        }

        assertEquals(referenceResult, testResult);
        checkLists();

        canModify = false;
        return referenceResult != null;
    }

    private boolean testSet() {
        Content content = randomItem();

        // Most of the time, ensure that we don't throw an exception.
        // Otherwise, we'd be throwing away the iterator too often.
        if ((!canModify || referenceList.contains(content)) && random.nextDouble() < .9) {
            return false;
        }

        if (debug) {
            System.out.println("## testSet");
        }

        Exception referenceResult = null;
        Exception testResult = null;

        try {
            referenceIterator.set(content);
        } catch (Exception ex) {
            referenceResult = ex;
        }
        try {
            testIterator.set(content);
        } catch (Exception ex) {
            testResult = ex;
        }

        assertEquals(referenceResult, testResult);
        checkLists();

        return referenceResult != null;
    }

    ///////////////////////////////////////////////////////////

    private void assertEquals(boolean value1, boolean value2) {
        if (debug) {
            System.out.println("    [value  = " + value2 + "]");
        }
        if (value1 != value2) {
            throw new RuntimeException("Expected: " + value1 + "   Actual: " + value2);
        }
    }

    private void assertEquals(int value1, int value2) {
        if (debug) {
            System.out.println("    [value  = " + value2 + "]");
        }
        if (value1 != value2) {
            throw new RuntimeException("Expected: " + value1 + "   Actual: " + value2);
        }
    }

    private void assertEquals(Object value1, Object value2) {
        if (debug) {
            System.out.println("    [value  = " + value2 + "]");
        }
        boolean areEqual;
        if (value1 == null) {
            areEqual = (value2 == null);
        } else if (value1 instanceof Object[]) {
            areEqual = Arrays.equals((Object[]) value1, (Object[]) value2);
        } else if (value1 instanceof Exception) {
            areEqual = (value2 != null && value1.getClass().equals(value2.getClass()));
        } else {
            areEqual = (value1.equals(value2));
        }

        if (!areEqual) {
            throw new RuntimeException("Expected: " + value1 + "   Actual: " + value2);
        }
    }

    // Returns a random item from sourceList.
    private Content randomItem() {
        return sourceList.get(random.nextInt(sourceList.size()));
    }

    // After modification, call this method to ensure that the
    // two lists are still equivalent.
    private void checkLists() {
        if (!ourEquals(referenceList, testList)) {
            dumpLists();
            throw new RuntimeException("Lists are different");
        }
    }

    // Don't use iterators here, it messes up our debug printouts.
    private static boolean ourEquals(List list1, List list2) {
        int size = list1.size();
        if (list2.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    // Dumps the contents of both lists to System.out.
    // Don't use iterators here, it messes up our debug printouts.
    private void dumpLists() {
        System.out.println(" >> Reference List (size=" + referenceList.size() + "):");
        for (Content content : referenceList) {
            System.out.println("        " + content);
        }

        System.out.println(" >> Test List (size=" + testList.size() + "):");
        for (Content content : testList) {
            System.out.println("        " + content);
        }

        if (elementsOnly) {
            List<Content> contents = parent.getContent();
            System.out.println(" >> parent (size=" + contents.size() + "):");
            for (Content content : contents) {
                System.out.println("        " + content);
            }
        }
    }

    private static void setDebug(boolean b) {
        debug = b;
    }

    private static boolean debug;
}