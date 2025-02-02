package com.tracelink.appsec.watchtower.core.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tracelink.appsec.watchtower.core.auth.model.UserEntity;
import com.tracelink.appsec.watchtower.core.exception.rule.RuleNotFoundException;
import com.tracelink.appsec.watchtower.core.mock.MockRule;
import com.tracelink.appsec.watchtower.core.module.interpreter.RulesetInterpreterException;
import com.tracelink.appsec.watchtower.core.module.ruleeditor.IRuleEditor;

@ExtendWith(SpringExtension.class)
public class RuleServiceTest {

	private static final String RULE_NAME = "Abstract Rule";

	@MockBean
	private RuleRepository ruleRepository;

	private RuleService ruleService;
	private RuleEntity rule;

	@Mock
	private IRuleEditor mockRuleManager;

	@BeforeEach
	public void setup() {
		ruleService = new RuleService(ruleRepository);
		rule = new MockRule();
	}


	@Test
	public void testGetRules() {
		RuleEntity rule2 = new MockRule();
		BDDMockito.when(ruleRepository.findAll()).thenReturn(Arrays.asList(rule, rule2));
		List<RuleDto> rules = ruleService.getRules();
		Assertions.assertEquals(2, rules.size());
		Assertions.assertEquals(rule.getName(), rules.get(0).getName());
		Assertions.assertEquals(rule.getMessage(), rules.get(0).getMessage());
		Assertions.assertEquals(rule2.getName(), rules.get(1).getName());
		Assertions.assertEquals(rule2.getMessage(), rules.get(1).getMessage());
	}

	@Test
	public void testGetRule() throws Exception {
		BDDMockito.when(ruleRepository.findByName(RULE_NAME)).thenReturn(rule);
		Assertions.assertEquals(rule, ruleService.getRule(RULE_NAME));
	}

	@Test
	public void testGetRuleNotFound() throws Exception {
		Assertions.assertNull(ruleService.getRule(RULE_NAME));
	}

	@Test
	public void testGetRuleId() throws Exception {
		BDDMockito.when(ruleRepository.findById(123L)).thenReturn(Optional.of(rule));
		Assertions.assertEquals(rule, ruleService.getRule(123L));
	}

	@Test
	public void testGetRuleIdNotFound() throws Exception {
		Assertions.assertThrows(RuleNotFoundException.class,
				() -> {
					ruleService.getRule(123L);
				});
	}

	@Test
	public void testGetRulesForModule() {
		RuleEntity rule2 = new MockRule();
		rule2.setName("A");
		String scannerType = rule.toDto().getModule();
		BDDMockito.when(ruleRepository.findAll()).thenReturn(Arrays.asList(rule, rule2));
		Set<RuleDto> rules = ruleService.getRulesForModule(scannerType);
		MatcherAssert.assertThat(rules, Matchers.hasSize(2));
		RuleDto[] rulesArr = rules.toArray(new RuleDto[2]);
		MatcherAssert.assertThat(rulesArr[0].getName(), Matchers.is(rule2.getName()));
		MatcherAssert.assertThat(rulesArr[1].getName(), Matchers.is(rule.getName()));
	}

	@Test
	public void testDeleteRule() throws Exception {
		BDDMockito.when(ruleRepository.findById(BDDMockito.anyLong()))
				.thenReturn(Optional.of(rule));
		ruleService.deleteRule(1L);
		BDDMockito.verify(ruleRepository).delete(rule);
	}

	@Test
	public void testDeleteRuleNotFound() throws Exception {
		Assertions.assertThrows(RuleNotFoundException.class,
				() -> {
					ruleService.deleteRule(1L);
				});
	}

	@Test
	public void testCreatesNameCollision() {
		BDDMockito.when(ruleRepository.findByName(RULE_NAME)).thenReturn(rule);
		Assertions.assertTrue(ruleService.createsNameCollision(1L, RULE_NAME));
	}

	@Test
	public void testCreatesNameCollisionSameId() {
		BDDMockito.when(ruleRepository.findByName(RULE_NAME)).thenReturn(rule);
		Assertions.assertFalse(ruleService.createsNameCollision(0L, RULE_NAME));
	}

	@Test
	public void testCreatesNameCollisionNoMatch() {
		Assertions.assertFalse(ruleService.createsNameCollision(1L, RULE_NAME));
	}

	@Test
	public void testImportRules() throws RulesetInterpreterException {
		UserEntity user = new UserEntity();
		user.setUsername("jdoe");
		ruleService.importRules(Collections.singleton(rule.toDto()), user);

		ArgumentCaptor<Iterable<RuleEntity>> argumentCaptor =
				ArgumentCaptor.forClass(Iterable.class);
		BDDMockito.verify(ruleRepository, Mockito.times(1)).saveAll(argumentCaptor.capture());
		BDDMockito.verify(ruleRepository, Mockito.times(1)).flush();
		Set<RuleEntity> rules = (Set<RuleEntity>) argumentCaptor.getValue();
		Assertions.assertEquals(rule.getName(), rules.iterator().next().getName());
		Assertions.assertEquals(user.getUsername(), rules.iterator().next().getAuthor());
	}
}
