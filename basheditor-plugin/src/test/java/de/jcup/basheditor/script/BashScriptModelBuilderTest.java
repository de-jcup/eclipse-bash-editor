/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.basheditor.script;

import static de.jcup.basheditor.script.AssertScriptModel.assertThat;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.TestScriptLoader;

public class BashScriptModelBuilderTest {

    private BashScriptModelBuilder builderToTest;

    @Before
    public void before() {
        builderToTest = new BashScriptModelBuilder();
    }
    @Test
    public void an_empty_script_has_novariables() throws Exception{
        /* prepare */
        String script = "";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasNoErrors().
            hasNoVariables();
        /* @formatter:on */
    }
    @Test
    public void a_variable_xxx_is_recognized() throws Exception{
        /* prepare */
        String script = "xxx=1234";

        /* execute */
        builderToTest.setDebug(true);
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasNoErrors().
            hasVariable("xxx").withValue("1234");
        /* @formatter:on */
    }
    
    @Test
    public void a_variable_xxx_is_recognized_inside_function() throws Exception{
        /* prepare */
        String script = "function abc {xxx=1234}";

        /* execute */
        builderToTest.setDebug(true);
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasNoErrors().
            hasNoVariables().
            hasVariable("xxx").withValue("1234");
        /* @formatter:on */
    }
    
    @Test
    public void a_variable_xxx_and_changedf_in_another_line_is_recognized() throws Exception{
        /* prepare */
        String script = "xxx=1234\n#some remarks\nxxx=5432";

        /* execute */
        builderToTest.setDebug(true);
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasNoErrors().
            hasVariable("xxx").withValue("1234").hasAssignments(2);
        /* @formatter:on */
    }
    
    @Test
    public void bugfix_130_case_esac_no_problems() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_130.sh");

        /* execute */
        builderToTest.setDebug(true);
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasNoErrors();
        /* @formatter:on */
    }
    
    @Test
    public void bugfix_115_one_function_with_keyword_and_name_but__space_in_brackets_is_recognized() throws Exception {
        /* prepare */
        String script = "function deploy ( ) {\n\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasFunction("deploy").
            hasFunctions(1).
            hasNoErrors();
        /* @formatter:on */
    }
    
    @Test
    public void bugfix_116_one_function_with_keyword_and_name_but__space_in_brackets_is_recognized() throws Exception {
        /* prepare */
        String script = "function test ( ) {\n\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasFunction("test").
            hasFunctions(1);
        /* @formatter:on */
    }
    
    @Test
    public void bugfix_116_one_function_with_name_only_but__space_in_brackets_is_recognized() throws Exception {
        /* prepare */
        String script = "test ( ) {\n\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasFunction("test").
            hasFunctions(1);
        /* @formatter:on */
    }
    
    @Test
    public void bugfix_116_functions_with_spaces_in_brackets_are_recognized_as_well() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_116.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test @formatter:off*/
        assertThat(bashScriptModel).
            hasFunction("warn").
            hasFunction("die").
            hasFunction("other").
            hasFunctions(3);
        /* @formatter:on */
    }

    @Test
    public void none_of_the_testscripts_contains_any_failure() throws Exception {
        /* prepare */
        StringBuilder errorCollector = new StringBuilder();
        List<File> scriptFiles = TestScriptLoader.fetchAllTestScriptFiles();
        for (File scriptFile : scriptFiles) {

            String script = TestScriptLoader.loadScript(scriptFile);

            /* execute */
            try {
                BashScriptModel bashScriptModel = builderToTest.build(script);
                /* test */
                if (bashScriptModel.hasErrors()) {
                    errorCollector.append("script file:").append(scriptFile).append(" contains errors:\n");
                    for (BashError error : bashScriptModel.getErrors()) {
                        errorCollector.append("-");
                        errorCollector.append(error.getMessage());
                        errorCollector.append("\n");
                    }
                }
            } catch (BashScriptModelException e) {
                /* test */
                errorCollector.append("script file:").append(scriptFile).append(" contains errors:\n");
                errorCollector.append("-");
                Throwable root = e;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                errorCollector.append("Root cause:" + root.getMessage());
                errorCollector.append("\n");

                root.printStackTrace();
            }

        }
        if (errorCollector.length() > 0) {
            fail(errorCollector.toString());
        }
    }

    @Test
    public void has_no_debugtoken_list__when_debug_is_turned_off_means_default() throws Exception {
        /* prepare */
        String script = "a b";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasNoDebugTokens();
    }

    @Test
    public void has_debugtoken_list___when_debug_is_turned_on() throws Exception {
        /* prepare */
        String script = "a b";
        builderToTest.setDebug(true);

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasDebugTokens(2);
    }

    @Test
    public void bugfix_52_$x_followed_by_comment_line_with_if_results_in_no_error() throws Exception {
        /* prepare */
        String script = "a=$x\n# check if the host is pingable";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_47_no_longer_errors_for_file() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_47.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_46_no_longer_errors_for_file() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_46.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_41_1_handle_arrays() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_1.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_41_2_handle_arrays() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_2.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_41_3_handle_arrays_simplified() throws Exception {
        /* prepare */
        String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_3.sh");

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(script);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void bugfix_39__variable_with_hash_do_not_result_errors() throws Exception {
        /* prepare */
        String code = "declare -A TitleMap\nif [ ${#TitleMap[*]} -eq 0 ]\nthen\n   displayerr \"Map is empty\"\n    exit 1\nfi";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasErrors(0);
    }

    @Test
    public void function_a_open_bracket_open_bracket_close_bracket_has_error() throws Exception {
        /* prepare */
        String code = "function a {{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunction("a").hasErrors(1);
    }

    @Test
    public void usage_space_x_msg_space_y_fatal_space_z() throws Exception {
        /* prepare */
        String code = "Usage () {x} Msg () {y} Fatal () {z}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunction("Usage").hasFunction("Msg").hasFunction("Fatal").hasFunctions(3);
    }

    @Test
    public void usage_x_msg_y_fatal_z() throws Exception {
        /* prepare */
        String code = "Usage() {x} Msg() {y} Fatal() {z}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunction("Usage").hasFunction("Msg").hasFunction("Fatal").hasFunctions(3);
    }

    @Test
    public void semicolon_function_xy_is_recognized_as_function_xy() throws Exception {
        /* prepare */
        String code = ";function xy{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
    }

    @Test
    public void method_Usage_space_open_close_brackets__is_recognized_as_function_Usage() throws Exception {
        /* prepare */
        String code = "Usage () {}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("Usage");
    }

    @Test
    public void space_semicolon_function_xy_is_recognized_as_function_xy() throws Exception {
        /* prepare */
        String code = " ;function xy{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
    }

    @Test
    public void space_semicolon_space_function_xy_is_recognized_as_function_xy() throws Exception {
        /* prepare */
        String code = " ; function xy{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
    }

    @Test
    public void semicolon_space_function_xy_is_recognized_as_function_xy() throws Exception {
        /* prepare */
        String code = "; function xy{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
    }

    @Test
    public void a_comments_with_function_is_not_handled_as_function() throws Exception {
        /* prepare */
        String code = "#\n# this function displays...\nfunction display {\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunction("display").hasFunctions(1).hasNoErrors();
    }

    @Test
    /**
     * Bash does not support functions inside functions - so if somebody such
     * things it's not allowed
     */
    public void function_f1_has_only_open_bracket__must_have_no_function_but_two_error() throws Exception {
        /* prepare */
        String code = "function f1(){";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasNoFunctions().hasErrors(2); // function
                                                                   // build has
                                                                   // one error
                                                                   // and one
                                                                   // of the
                                                                   // valdiators
                                                                   // too
    }

    @Test
    /**
     * Bash does not support functions inside functions - so if somebody such
     * things it's not allowed
     */
    public void function_f1_containing_illegal_child_function_f1b__followed_by_function_f2__results_in_functions_f1_f2__only() throws Exception {
        /* prepare */
        String code = "function f1(){function f1b() {}} function f2 {}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(2).hasFunction("f1").hasFunction("f2").hasNoFunction("f1b");
    }

    @Test
    public void function_xyz_no_curly_brackets_is_not_recognized_as_function_and_has_an_error() throws Exception {
        /* prepare */
        String code = "function xy";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasNoFunctions().hasErrors(1);
    }

    @Test
    public void function_read_hyphen_file__curlyBrackets_open_close__is_recognized_as_function_read_hyphen_file__and_has_no_errors()
            throws Exception {
        /* prepare */
        String code = "function read-file{}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("read-file").hasNoErrors();
    }

    @Test
    public void function_read_hyphen_file_curlyBraceOpen_NewLine__content_NewLine_curlybraceClose_is_recognized_as_function_read_hyphen_file()
            throws Exception {
        /* prepare */
        String code = "function read-file{\n#something\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("read-file");
    }

    @Test
    public void function_read_hyphen_file_hypen_format_followed_with_brackets_is_recognized_as_function_read_hyphen_file_hypen_format()
            throws Exception {
        /* prepare */
        String code = "function read-file-format()\n{\n}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("read-file-format");
    }

    @Test
    public void function_read_hyphen_file_hypen_format_space_followed_with_brackets_is_recognized_as_function_read_hyphen_file_hypen_format()
            throws Exception {
        /* prepare */
        String code = "function read-file-format (){}";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(code);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("read-file-format");
    }

    @Test
    public void an_empty_line_returns_not_null_AST() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("");
        assertNotNull(bashScriptModel);
    }

    @Test
    public void a_line_with_Xfunction_test_is_NOT_recognized() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("Xfunction test {}");
        /* test */
        assertThat(bashScriptModel).hasNoFunctions();

    }

    @Test
    public void a_line_with_method_having_underscores_is_correct_parsed() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("function show_something_else{}");
        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("show_something_else");
    }

    @Test
    public void a_line_with_function_test_is_recognized_and_returns_function_with_name_test() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("function test {}");
        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunction("test");
    }

    @Test
    public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2() throws Exception {
        BashScriptModel bashScriptModel = builderToTest
                .build("function test1 {\n#something\n}\n #other line\n\nfunction test2 {\n#something else\n}\n");
        /* test */
        assertThat(bashScriptModel).hasFunction("test1").hasFunction("test2").hasFunctions(2);
    }

    @Test
    public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2__but_with_backslash_r()
            throws Exception {
        /* prepare */
        String bashScript = "function test1 {\n#something\n}\n #other line\n\nfunction test2 {\r\n#something else\r\n}\r\n";

        /* execute */
        BashScriptModel bashScriptModel = builderToTest.build(bashScript);
        /* test */
        assertThat(bashScriptModel).hasNoErrors().hasFunction("test1").hasFunction("test2").hasFunctions(2);
    }

    @Test
    public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_0_and_has_no_errors() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("function test {}");
        assertNotNull(bashScriptModel);

        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 0).hasNoErrors();
    }

    @Test
    public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_1_when_first_line_empty() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("\nfunction test {}");
        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 1);
    }

    @Test
    public void a_line_with_5_spaces_and_function_test_is_recognized_and_returns_function_with_pos_5() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("     function test {}");
        /* test */
        assertThat(bashScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 5);
    }

    @Test
    public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() throws Exception {
        BashScriptModel bashScriptModel = builderToTest.build("     Xfunction test {}");
        /* test */
        assertThat(bashScriptModel).hasNoFunctions();

    }

}
