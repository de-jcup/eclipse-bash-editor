/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.basheditor.debug.launch;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CommandStringToCommandListConverterTest {

    private CommandStringToCommandListConverter converterToTest;

    @Before
    public void before() {
        converterToTest = new CommandStringToCommandListConverter();
    }

    @Test
    public void a_space_b_space_c__are_three_entries_a_b_c() {
        assertEquals(asList("a", "b", "c"), converterToTest.convert("a b c"));
    }
    
    @Test
    public void three_spaces_only_are_empty_list() {
        assertEquals(asList(), converterToTest.convert("   "));
    }
    
    @Test
    public void three_spaces_then_a_followed_by_three_spaces_only_are_one_entry_a_list() {
        assertEquals(asList("a"), converterToTest.convert("   a   "));
    }
    
    @Test
    public void three_spaces_then_a_followed_by_three_spaces_then_b__are_two_entry_a_b_list() {
        assertEquals(asList("a","b"), converterToTest.convert("   a   b"));
    }
    
    @Test
    public void three_spaces_then_a_followed_by_three_spaces_then_b_one_space_are_two_entry_a_b_list() {
        assertEquals(asList("a","b"), converterToTest.convert("   a   b "));
    }
    
    @Test
    public void a_followd_by_2_parameters() {
        assertEquals(asList("a", "-b", "-c"), converterToTest.convert("a -b -c"));
    }

    @Test
    public void a_space_b_singlequote_x_singlequote_x_space_c__are_three_entries_a_b_quote_x_quote_x_c() {
        assertEquals(asList("a", "b'x'", "c"), converterToTest.convert("a b'x' c"));
    }
    
    @Test
    public void a_space_b_doublequote_x_doublequote_x_space_c__are_three_entries_a_b_quote_x_quote_x_c() {
        assertEquals(asList("a", "b\"x\"", "c"), converterToTest.convert("a b\"x\" c"));
    }

    @Test
    public void null_handled_as_empty_list() {
        assertEquals(asList(), converterToTest.convert(null));
    }

    @Test
    public void empty_handled_as_empty_list() {
        assertEquals(asList(), converterToTest.convert(""));
    }

}
