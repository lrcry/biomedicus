/*
 * Copyright (c) 2015 Regents of the University of Minnesota.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umn.biomedicus.common.viterbi;

import edu.umn.biomedicus.common.grams.Bigram;
import edu.umn.biomedicus.common.grams.Ngram;
import mockit.Verifications;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mockit.Deencapsulation.getField;
import static org.testng.Assert.*;

/**
 * Test for {@link Ancestor}.
 */
public class AncestorTest {

    @Test
    public void testCreateInitialIterable() throws Exception {
        List<String> initialStates = new ArrayList<>();
        initialStates.add("1");
        initialStates.add("2");
        initialStates.add("3");

        Ancestor<String> ancestor = Ancestor.createInitial(initialStates, Ngram::create);

        new Verifications() {{
            @SuppressWarnings("unchecked")
            HistoryChain<String> field = getField(ancestor, "historyChain");
            assertEquals(field.getState(), "3");
            assertEquals(field.getPrevious().getState(), "2");
            assertEquals(field.getPrevious().getPrevious().getState(), "1");
            assertNull(field.getPrevious().getPrevious().getPrevious());

            assertEquals(0, ancestor.getLogProbability(), 1e-15);
        }};
    }

    @Test
    public void testCreateInitialVarargs() throws Exception {
        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3");

        new Verifications() {{
            @SuppressWarnings("unchecked")
            HistoryChain<String> field = getField(ancestor, "historyChain");
            assertEquals(field.getState(), "3");
            assertEquals(field.getPrevious().getState(), "2");
            assertEquals(field.getPrevious().getPrevious().getState(), "1");
            assertNull(field.getPrevious().getPrevious().getPrevious());

            assertEquals(0, ancestor.getLogProbability(), 1e-15);
        }};
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateInitialFailure() throws Exception {
        List<String> initialStates = new ArrayList<>();

        Ancestor.createInitial(initialStates, Ngram::create);

        fail();
    }

    @Test
    public void testGetHistory() throws Exception {
        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);

        List<String> history = ancestor.getHistory("4");

        assertEquals(history, Arrays.asList("1", "2", "3", "4"));
    }

    @Test
    public void testCreateDescendant() throws Exception {
        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);

        Ancestor<String> descendant = ancestor.createDescendant(-3.22, "5");

        assertEquals(descendant.getHistory("4"), Arrays.asList("1", "2", "3", "4", "5"));
    }

    @Test
    public void testSkip() throws Exception {

        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);

        Ancestor<String> descendant = ancestor.skip();

        assertEquals(descendant.getHistory("4"), Arrays.asList("1", "2", "3", "4", "4"));
    }

    @Test
    public void testGetBigram() throws Exception {


        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);

        Bigram<String> bigram = ancestor.getBigram();

        assertEquals(bigram.getFirst(), "2");
        assertEquals(bigram.getSecond(), "3");
    }

    @Test
    public void testMostRecent() throws Exception {

        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);

        assertEquals(ancestor.mostRecent(), "3");

    }

    @Test
    public void testMoreProbable() throws Exception {
        Ancestor<String> ancestor = Ancestor.createInitial(Ngram::create, "1", "2", "3", null);
        Ancestor<String> first = ancestor.createDescendant(-0.5, "a");
        Ancestor<String> second = ancestor.createDescendant(-1.0, "b");

        assertEquals(Ancestor.moreProbable(first, second).mostRecent(), "a");
    }
}