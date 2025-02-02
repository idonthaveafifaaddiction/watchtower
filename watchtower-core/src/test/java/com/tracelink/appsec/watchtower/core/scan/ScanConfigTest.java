package com.tracelink.appsec.watchtower.core.scan;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.tracelink.appsec.watchtower.core.ruleset.RulesetDto;

public class ScanConfigTest {

	@Test
	public void testNullWorkingDirectory() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					new ScanConfig().setWorkingDirectory(null);
				});
	}

	@Test
	public void testNegativeThreads() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> {
					new ScanConfig().setThreads(-1);
				});
	}

	@Test
	public void testDefaults() throws Exception {
		Path workingDirectory = Files.createTempDirectory(null);
		ScanConfig config = new ScanConfig();
		config.setWorkingDirectory(workingDirectory);

		Assertions.assertEquals(workingDirectory, config.getWorkingDirectory());
		Assertions.assertFalse(config.isBenchmarkEnabled());
		Assertions.assertFalse(config.isDebugEnabled());
		Assertions.assertEquals(Runtime.getRuntime().availableProcessors(), config.getThreads());
	}

	@Test
	public void testReflexiveStatus() throws Exception {
		Path workingDirectory = Files.createTempDirectory(null);
		int threads = 1;
		RulesetDto ruleset = new RulesetDto();
		ScanConfig config = new ScanConfig();
		config.setRuleset(ruleset);
		config.setWorkingDirectory(workingDirectory);
		config.setBenchmarkEnabled(true);
		config.setDebugEnabled(true);
		config.setThreads(threads);

		Assertions.assertEquals(workingDirectory, config.getWorkingDirectory());
		Assertions.assertTrue(config.isBenchmarkEnabled());
		Assertions.assertTrue(config.isDebugEnabled());
		Assertions.assertEquals(threads, config.getThreads());
		Assertions.assertEquals(ruleset, config.getRuleset());
	}

}
