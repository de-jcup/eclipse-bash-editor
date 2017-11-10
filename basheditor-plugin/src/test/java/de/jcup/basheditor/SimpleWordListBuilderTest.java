package de.jcup.basheditor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SimpleWordListBuilderTest {

	private SimpleWordListBuilder builderToTest;
	private List<String> listExpected;

	@Before
	public void before(){
		builderToTest = new SimpleWordListBuilder();
		listExpected=new ArrayList<>();
	}
	
	@Test
	public void albert_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert sarah"));
	}
	
	@Test
	public void albert_qestion_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert?sarah"));
	}
	
	@Test
	public void albert_space_space_tab_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert  \t sarah"));
	}
	
	@Test
	public void test_open_backet_close_bracket_break__results_in_test_break() {
		assertEquals(expect("test","break").listExpected, builderToTest.build("test() break"));
	}
	
	@Test
	public void test_open_backet_xx_close_bracket_break__results_in_test_xx_break() {
		assertEquals(expect("test","xx","break").listExpected, builderToTest.build("test(xx) break"));
	}
	
	@Test
	public void albert_dot_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert.sarah"));
	}
	
	@Test
	public void albert_commata_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert,sarah"));
	}
	
	@Test
	public void albert_commata_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert, sarah"));
	}
	
	@Test
	public void albert_dot_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert. sarah"));
	}
	
	@Test
	public void albert_semicolon_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert; sarah"));
	}

	private SimpleWordListBuilderTest expect(String ... strings){
		for (String string: strings){
			listExpected.add(string);
		}
		return this;
	}
}
