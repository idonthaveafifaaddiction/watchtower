package com.tracelink.appsec.watchtower.core.rule;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracelink.appsec.watchtower.core.auth.model.UserEntity;
import com.tracelink.appsec.watchtower.core.exception.rule.RuleNotFoundException;
import com.tracelink.appsec.watchtower.core.module.interpreter.RulesetInterpreterException;

/**
 * Handles logic to retrieve and delete rules, regardless of rule type.
 *
 * @author mcool
 */
@Service
public class RuleService {

	private RuleRepository ruleRepository;

	/**
	 * Creates an instance of this service with a {@link RuleRepository}.
	 *
	 * @param ruleRepository repository to interact with the database
	 */
	public RuleService(@Autowired RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	/**
	 * Gets a list of all rules. Each rule is translated into a {@link RuleDto} for convenience.
	 *
	 * @return list of DTOs for all rules in the database
	 */
	public List<RuleDto> getRules() {
		return Collections.unmodifiableList(
				ruleRepository.findAll().stream().map(RuleEntity::toDto).sorted()
						.collect(Collectors.toList()));
	}

	/**
	 * Gets the rule with the given ID.
	 *
	 * @param id ID of the rule to retrieve
	 * @return rule with the given ID
	 * @throws RuleNotFoundException if no rule with the given ID exists
	 */
	public RuleEntity getRule(long id) throws RuleNotFoundException {
		Optional<RuleEntity> rule = ruleRepository.findById(id);
		if (!rule.isPresent()) {
			throw new RuleNotFoundException("No such rule exists.");
		}
		return rule.get();
	}

	/**
	 * Gets the rule with the given name.
	 *
	 * @param name name of the rule to retrieve
	 * @return rule with the given name, or null if not found
	 */
	public RuleEntity getRule(String name) {
		return ruleRepository.findByName(name);
	}

	public Set<RuleDto> getRulesForModule(String module) {
		List<RuleEntity> rules = ruleRepository.findAll();

		Set<RuleDto> rulesSet = new TreeSet<>(Comparator.comparing(RuleDto::getName));
		rulesSet.addAll(rules.stream().map(RuleEntity::toDto)
				.filter(r -> r.getModule().equalsIgnoreCase(module))
				.collect(Collectors.toSet()));
		return rulesSet;
	}

	/**
	 * Deletes the rule with the given ID.
	 *
	 * @param id ID of the rule to delete
	 * @throws RuleNotFoundException if no rule with the given ID exists
	 */
	public void deleteRule(long id) throws RuleNotFoundException {
		RuleEntity rule = getRule(id);
		ruleRepository.delete(rule);
	}

	/**
	 * Determines whether changing the name of the rule with the given ID to the given name will
	 * result in a name collision. Used to prevent multiple rules in the database from having the
	 * same name.
	 *
	 * @param id   ID of the rule to edit
	 * @param name proposed new name for the rule with the given ID
	 * @return true if a different rule in the database already has the given name, false otherwise
	 */
	public boolean createsNameCollision(long id, String name) {
		RuleEntity rule = getRule(name);
		return rule != null && rule.getId() != id;
	}

	/**
	 * Imports the given set of rule DTOs and stores each in the database. For each new rule entity,
	 * assign the given user as the author of the rule.
	 *
	 * @param dtos set of rule DTOs to import
	 * @param user user to assign as author of the rules
	 * @return list of database entity rules that have been imported
	 * @throws RulesetInterpreterException if there is a rule that already exists with the same name
	 */
	public List<RuleEntity> importRules(Set<RuleDto> dtos, UserEntity user)
			throws RulesetInterpreterException {
		Set<RuleEntity> rules = new HashSet<>();
		for (RuleDto rule : dtos) {
			if (ruleRepository.findByName(rule.getName()) != null) {
				throw new RulesetInterpreterException("Cannot import rule " + rule.getName()
						+ " as another rule by that name already exists");
			}
			RuleEntity ruleEntity = rule.toEntity();
			ruleEntity.setAuthor(user.getUsername());
			rules.add(ruleEntity);
		}
		List<RuleEntity> saveRules = ruleRepository.saveAll(rules);
		ruleRepository.flush();
		return saveRules;
	}
}
