package com.tracelink.appsec.watchtower.core.rule;

import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents a data transfer object for the {@link RuleEntity}. All fields in this object are in
 * plain text. Contains the fields inherited by all rule DTOs, regardless of type.
 *
 * @author mcool
 */
public abstract class RuleDto implements Comparable<RuleDto> {
	protected static final String CANNOT_BE_NULL = " cannot be null.";
	protected static final String CANNOT_BE_EMPTY = " cannot be empty.";
	private Long id;

	@NotNull(message = "Author" + CANNOT_BE_NULL)
	@NotEmpty(message = "Author" + CANNOT_BE_EMPTY)
	private String author;

	@NotNull(message = "Name" + CANNOT_BE_NULL)
	@NotEmpty(message = "Name" + CANNOT_BE_EMPTY)
	@Size(max = 100, message = "Name cannot have a length of more than 100 characters.")
	private String name;

	@NotNull(message = "Message" + CANNOT_BE_NULL)
	@NotEmpty(message = "Message" + CANNOT_BE_EMPTY)
	private String message;

	@NotNull(message = "External URL" + CANNOT_BE_NULL)
	@NotEmpty(message = "External URL" + CANNOT_BE_EMPTY)
	@Size(max = 255, message = "External URL cannot have a length of more than 256 characters.")
	private String externalUrl;

	@NotNull(message = "Priority" + CANNOT_BE_NULL)
	private RulePriority priority;

	private Set<String> rulesets = new TreeSet<>();

	/**
	 * This returns the name of the module that the rule is associated with.
	 *
	 * @return module name, representing the rule type
	 */
	public abstract String getModule();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public RulePriority getPriority() {
		return priority;
	}

	public void setPriority(RulePriority priority) {
		this.priority = priority;
	}

	public Set<String> getRulesets() {
		return rulesets;
	}

	public void setRulesets(Set<String> rulesets) {
		this.rulesets = rulesets;
	}

	@Override
	public int compareTo(RuleDto o) {
		int priorityCompare = getPriority().getPriority() - o.getPriority().getPriority();
		if (priorityCompare == 0) {
			return getName().compareTo(o.getName());
		}
		return priorityCompare;
	}

	/**
	 * Converts this data transfer object into a database entity object. Used to help import rules
	 * to the database.
	 *
	 * @return database entity object representing this rule DTO
	 */
	public abstract RuleEntity toEntity();
}
