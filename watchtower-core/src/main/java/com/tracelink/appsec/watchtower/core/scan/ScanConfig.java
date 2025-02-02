package com.tracelink.appsec.watchtower.core.scan;

import java.nio.file.Path;

import com.tracelink.appsec.watchtower.core.ruleset.RulesetDto;

/**
 * Scan configuration object managing all sub-configurations
 *
 * @author csmith, mcool
 */
public class ScanConfig {
	/**
	 * ruleset DTO object for this scan
	 */
	private RulesetDto ruleset;
	/**
	 * path to the working directory, which contains files to be scanned
	 */
	private Path workingDirectory;
	/**
	 * number of threads to be used by this scan
	 */
	private int threads = Runtime.getRuntime().availableProcessors();
	/**
	 * whether benchmarking is enabled for this scan
	 */
	private boolean benchmarkEnabled = false;
	/**
	 * whether debug is enabled for this scan
	 */
	private boolean debugEnabled = false;

	public void setRuleset(RulesetDto ruleset) {
		if (ruleset == null) {
			throw new IllegalArgumentException("Ruleset cannot be null.");
		}
		this.ruleset = ruleset;
	}

	public RulesetDto getRuleset() {
		return ruleset;
	}

	public Path getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(Path workingDirectory) {
		if (workingDirectory == null) {
			throw new IllegalArgumentException("Working directory cannot be null.");
		}
		this.workingDirectory = workingDirectory;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		if (threads < 0) {
			throw new IllegalArgumentException("Threads cannot be negative. Threads: " + threads);
		}
		this.threads = threads;
	}

	public boolean isBenchmarkEnabled() {
		return benchmarkEnabled;
	}

	public void setBenchmarkEnabled(boolean benchmarkEnabled) {
		this.benchmarkEnabled = benchmarkEnabled;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

}
