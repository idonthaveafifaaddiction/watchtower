package com.tracelink.appsec.module.eslint;

import java.util.Arrays;
import java.util.List;

import com.tracelink.appsec.module.eslint.designer.EsLintRuleDesigner;
import com.tracelink.appsec.module.eslint.editor.EsLintRuleEditor;
import com.tracelink.appsec.module.eslint.engine.LinterEngine;
import com.tracelink.appsec.module.eslint.interpreter.EsLintRulesetInterpreter;
import com.tracelink.appsec.module.eslint.scanner.EsLintScanner;
import com.tracelink.appsec.watchtower.core.auth.model.PrivilegeEntity;
import com.tracelink.appsec.watchtower.core.module.AbstractModule;
import com.tracelink.appsec.watchtower.core.module.WatchtowerModule;
import com.tracelink.appsec.watchtower.core.module.designer.IRuleDesigner;
import com.tracelink.appsec.watchtower.core.module.interpreter.IRulesetInterpreter;
import com.tracelink.appsec.watchtower.core.module.ruleeditor.IRuleEditor;
import com.tracelink.appsec.watchtower.core.module.scanner.IScanner;

/**
 * Module to hold implementations for ESLint rules, scanner, designer, editor and interpreter.
 *
 * @author mcool
 */
@WatchtowerModule
public class EsLintModule extends AbstractModule {

	public static final String MODULE_NAME = "ESLint";
	public static final String ESLINT_RULE_EDIT_PRIVILEGE_NAME = "ESLint Rule Editor";
	public static final String ESLINT_RULE_DESIGNER_PRIVILEGE_NAME = "ESLint Rule Designer";

	public EsLintModule() {
		// ensure the LinterEngine is started
		LinterEngine.getInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSchemaHistoryTable() {
		return "eslint_schema_history";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMigrationsLocation() {
		return "db/eslint";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IScanner getScanner() {
		return new EsLintScanner();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRuleDesigner getRuleDesigner() {
		return new EsLintRuleDesigner();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRuleEditor getRuleEditor() {
		return new EsLintRuleEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRulesetInterpreter getInterpreter() {
		return new EsLintRulesetInterpreter();
	}

	@Override
	public List<PrivilegeEntity> getModulePrivileges() {
		return Arrays.asList(new PrivilegeEntity().setName(ESLINT_RULE_EDIT_PRIVILEGE_NAME)
				.setCategory("Rule Editor").setDescription(
						"User may edit ESLint rules in the Rule Editor. Caution! This may lead to remote code execution as the rules are run as server-side javascript on the Watchtower machine"),
				new PrivilegeEntity().setName(ESLINT_RULE_DESIGNER_PRIVILEGE_NAME)
						.setCategory("Rule Designer").setDescription(
								"User may create and test ESLint rules in the Rule Designer. Caution! This may lead to remote code execution as the rules are run as server-side javascript on the Watchtower machine"));
	}
}
